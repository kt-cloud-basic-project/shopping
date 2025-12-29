package com.kt.service.order;

import static com.kt.common.support.ObjectUtils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.common.request.Paging;
import com.kt.common.support.Preconditions;
import com.kt.domain.discount.Discount;
import com.kt.domain.discount.policy.DiscountPolicy;
import com.kt.domain.discount.policy.DiscountPolicyFactory;
import com.kt.domain.membership.Membership;
import com.kt.domain.order.Order;
import com.kt.domain.order.OrderStatus;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.product.ProductStatus;
import com.kt.domain.shoppingaddress.ShoppingAddress;
import com.kt.domain.user.User;
import com.kt.dto.discount.response.DiscountResult;
import com.kt.dto.order.OrderCreateRequest;
import com.kt.dto.order.OrderStatusUpdateRequest;
import com.kt.dto.order.OrderUpdateRequest;
import com.kt.dto.order.response.OrderListResponse;
import com.kt.dto.order.response.OrderDetailResponse;
import com.kt.dto.order.response.OrderProductResponse;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.discountmembership.DiscountMembershipRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.order.OrderRepositoryCustom;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.shoppingaddress.ShoppingAddressRepository;
import com.kt.repository.user.UserRepository;
import com.kt.repository.variant.VariantRepository;
import com.kt.service.discount.DiscountCalcService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final ShoppingAddressRepository shoppingAddressRepository;
	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;
	private final VariantRepository variantRepository;
	private final OrderRepositoryCustom orderRepositoryCustom;
	private final DiscountMembershipRepository discountMembershipRepository;
	private final DiscountCalcService discountCalcService;

	public Long create(Long userId, OrderCreateRequest request) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		ShoppingAddress address;

		if (request.receiverAddressId() != null) {
			address = shoppingAddressRepository.findByIdOrThrow(request.receiverAddressId(),
				ErrorCode.NOT_FOUND_SHOPPING_ADDRESS);

			// 사용자 본인이 등록한 배송지인지 검증
			Preconditions.validate(address.getUser().getId().equals(userId), ErrorCode.NOT_FOUND_SHOPPING_ADDRESS);
		} else {
			address = shoppingAddressRepository.findFirstByUserIdAndIsDefaultTrueOrderByIdDesc(userId)
				.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_SHOPPING_ADDRESS));
		}

		// 1. 주문 생성
		var newOrder = new Order(
			request.receiverName(),
			request.receiverPhone(),
			address.getAddress(),
			user
		);

		List<OrderProduct> orderProducts = new ArrayList<>();

		// 2. 전체 product 검증
		request.products().forEach(product -> {
			var targetProduct = productRepository.findByIdOrThrow(product.productId(), ErrorCode.NOT_FOUND_PRODUCT);

			Preconditions.validate(targetProduct.getStatus().equals(ProductStatus.ACTIVATED),
				ErrorCode.CANNOT_PURCHASE_PRODUCT);
			Preconditions.validate(targetProduct.getStock() >= product.productCount(), ErrorCode.NOT_ENOUGH_STOCK);
			//선택한 상품의 옵션이 맞는지 검증
			var variant = variantRepository.findByIdAndDeletedFalseOrThrow(product.productVariantId(),
				ErrorCode.NOT_FOUND_VARIANT);
			Preconditions.validate(variant.getProduct().getId().equals(targetProduct.getId()),
				ErrorCode.INVALID_VARIANT);

			// OrderProduct 생성
			var newOrderProduct = new OrderProduct(
				product.productCount(),
				product.productVariantId(),
				targetProduct,
				newOrder
			);

			orderProducts.add(newOrderProduct);
		});

		// 3. stock 차감
		orderProducts.forEach(newProduct -> {
			var product = newProduct.getProduct();
			product.updateStock(product.getStock() - newProduct.getCount());
		});

		// 4. 저장
		orderRepository.save(newOrder);
		orderProductRepository.saveAll(orderProducts);

		return newOrder.getId();
	}

	public Page<OrderListResponse> getOrderList(Long userId, Paging paging) {
		Page<Order> orders = orderRepositoryCustom.getOrders(userId, paging.toPageable());

		if (orders.isEmpty()) {
			return Page.empty(paging.toPageable());
		}

		// 멤버십 할인 조회
		Discount membershipDiscount = discountCalcService.getMembershipDiscount(userId);

		// 상품별 할인 조회
		Map<Long, List<Discount>> productDiscount = discountCalcService.getProductsDiscount(
			orders.getContent().stream()
				.flatMap(order -> order.getOrderProducts().stream())
				.map(op -> op.getProduct().getId())
				.distinct()
				.toList()
		);

		return orders.map(order ->  {
			Map<Long, DiscountResult> orderProductDiscounts = new HashMap<>();

			for (var orderProduct : order.getOrderProducts()) {
				DiscountResult discountResult = discountCalcService.calculate(
					orderProduct.getProduct().getPrice() * orderProduct.getCount(),
					membershipDiscount,
					productDiscount.getOrDefault(orderProduct.getProduct().getId(), List.of())
				);

				orderProductDiscounts.put(orderProduct.getId(), discountResult);
			}

			return OrderListResponse.from(order, orderProductDiscounts);
		});
	}

	public void cancel(Long orderId, Long userId) {
		// orderId 존재 여부 검증
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		// userId 랑 order 가 갖고있는 userId 랑 같은지 검증
		Preconditions.validate(order.getUser().getId().equals(userId), ErrorCode.NOT_ORDER_OWNER);

		// 주문 취소 가능 여부 검증
		Preconditions.validate(order.getOrderStatus() == OrderStatus.ORDERED ||
			order.getOrderStatus() == OrderStatus.PAID, ErrorCode.CANNOT_CANCEL_ORDER);

		order.cancel();
	}

	public OrderDetailResponse getOrderDetail(Long userId, Long orderId) {
		var order = orderRepository.findByIdAndUserIdOrThrow(orderId, userId, ErrorCode.NOT_FOUND_ORDER);

		// 멤버십 할인 조회
		Discount membershipDiscount = discountCalcService.getMembershipDiscount(userId);

		// 상품별 할인 조회
		Map<Long, List<Discount>> productDiscount = discountCalcService.getProductsDiscount(
			order.getOrderProducts().stream()
				.map(op -> op.getProduct().getId())
				.distinct()
				.toList()
		);

		List<OrderProductResponse> products = orderProductRepository.findByOrderId(orderId).stream()
			.map(orderProduct -> {
				var product = Objects.requireNonNull(orderProduct.getProduct(), ErrorCode.NOT_FOUND_PRODUCT.getMessage());

				DiscountResult discountResult = discountCalcService.calculate(
					product.getPrice() * orderProduct.getCount(),
					membershipDiscount,
					productDiscount.getOrDefault(product.getId(), List.of())
				);

				return OrderProductResponse.from(
					orderProduct,
					product,
					discountResult.discountPrice(),
					discountResult.discountedPrice()
				);
			}
		).toList();

		return OrderDetailResponse.from(order, products);
	}

	public void update(OrderUpdateRequest request, Long orderId, Long userId) {
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		Preconditions.validate(order.getUser().getId().equals(userId), ErrorCode.NOT_ORDER_OWNER);

		Preconditions.validate(order.getOrderStatus() == OrderStatus.ORDERED ||
			order.getOrderStatus() == OrderStatus.PAID, ErrorCode.CANNOT_UPDATE_ORDER_INFO);

		String updatedAddress = request.receiverAddressId() != null
			? shoppingAddressRepository.findByIdAndUserIdOrThrow(request.receiverAddressId(), userId, ErrorCode.NOT_SHOPPING_ADDRESS_OWNER).getAddress()
			: order.getReceiverAddress();

		order.update(
			orElseIfEmpty(request.receiverName(), order.getReceiverName()),
			orElseIfEmpty(request.receiverPhone(), order.getReceiverPhone()),
			updatedAddress
		);
	}

	public void cancelByAdmin(Long orderId) {
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		// 주문 취소 가능 여부 검증
		Preconditions.validate(order.getOrderStatus() == OrderStatus.ORDERED ||
			order.getOrderStatus() == OrderStatus.PAID, ErrorCode.CANNOT_CANCEL_ORDER);

		order.cancel();
	}

	public void requestRefund(Long orderId, Long userId) {
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		Preconditions.validate(order.getUser().getId().equals(userId), ErrorCode.NOT_ORDER_OWNER);

		Preconditions.validate(order.getOrderStatus() == OrderStatus.PAID ||
			order.getOrderStatus() == OrderStatus.RETURNED, ErrorCode.CANNOT_REFUND_ORDER);

		order.requestRefund();
	}

	public void requestReturn(Long orderId, Long userId) {
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		Preconditions.validate(order.getUser().getId().equals(userId), ErrorCode.NOT_ORDER_OWNER);

		Preconditions.validate(order.getOrderStatus() == OrderStatus.DELIVERED, ErrorCode.CANNOT_RETURN_ORDER);

		order.requestReturn();
	}

	public void approveRefund(Long orderId) {
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		Preconditions.validate(order.getOrderStatus() == OrderStatus.REFUND_REQUESTED, ErrorCode.CANNOT_REFUND_ORDER);

		order.approveRefund();
	}

	public void approveReturn(Long orderId) {
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		Preconditions.validate(order.getOrderStatus() == OrderStatus.RETURN_REQUESTED, ErrorCode.CANNOT_RETURN_ORDER);

		order.approveReturn();
	}

	public void updateStatus(OrderStatusUpdateRequest request, Long orderId) {
		var order = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		switch (request.orderStatus()) {
			case PROCESSING -> Preconditions.validate(order.getOrderStatus() == OrderStatus.PAID,
				ErrorCode.CANNOT_UPDATE_ORDER_STATUS);
			case SHIPPED -> Preconditions.validate(order.getOrderStatus() == OrderStatus.PROCESSING,
				ErrorCode.CANNOT_UPDATE_ORDER_STATUS);
			case DELIVERED -> Preconditions.validate(order.getOrderStatus() == OrderStatus.SHIPPED,
				ErrorCode.CANNOT_UPDATE_ORDER_STATUS);
			default -> throw new CustomException(ErrorCode.CANNOT_UPDATE_ORDER_STATUS);
		}
		order.updateStatus(request.orderStatus());
	}
}
