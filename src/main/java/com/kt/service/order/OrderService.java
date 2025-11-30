package com.kt.service.order;

import static com.kt.common.support.ObjectUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.request.Paging;
import com.kt.common.support.Preconditions;
import com.kt.domain.order.Order;
import com.kt.domain.order.OrderStatus;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.product.ProductStatus;
import com.kt.dto.order.OrderCreateRequest;
import com.kt.dto.order.OrderUpdateRequest;
import com.kt.dto.order.response.OrderListResponse;
import com.kt.dto.order.OrderDetailResponse;
import com.kt.dto.order.OrderProductResponse;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.shoppingaddress.ShoppingAddressRepository;
import com.kt.repository.user.UserRepository;
import com.kt.repository.variant.VariantRepository;

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

	public void create(Long userId, OrderCreateRequest request) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		var address = shoppingAddressRepository.findByIdOrThrow(request.receiverAddressId(), ErrorCode.NOT_FOUND_SHOPPING_ADDRESS);

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
			var targetProduct = productRepository.findByIdOrThrow(product.productId(),  ErrorCode.NOT_FOUND_PRODUCT);

			Preconditions.validate(targetProduct.getStatus().equals(ProductStatus.ACTIVATED), ErrorCode.CAN_NOT_PURCHASE_PRODUCT);
			Preconditions.validate(targetProduct.getStock() >= product.productCount(), ErrorCode.NOT_ENOUGH_STOCK);
			//선택한 상품의 옵션이 맞는지 검증
			var variant = variantRepository.findByIdOrThrow(product.productVariantId(),  ErrorCode.NOT_FOUND_VARIANT);
			Preconditions.validate(variant.getProduct().getId().equals(targetProduct.getId()), ErrorCode.INVALID_VARIANT);

			// OrderProduct 생성
			var newOrderProduct = new OrderProduct(
				product.productCount(),
				product.productVariantId(),
				targetProduct,
				newOrder
			);

			orderProducts.add(newOrderProduct);

			//TODO: payment 생성
		});

		// 3. stock 차감
		orderProducts.forEach(newProduct -> {
			var product = newProduct.getProduct();
			product.updateStock(product.getStock() - newProduct.getCount());
		});

		// 4. 저장
		orderRepository.save(newOrder);
		orderProductRepository.saveAll(orderProducts);

	}

	public Page<OrderListResponse> getOrderList(Long userId, Paging paging) {
		Page<Order> orderList = orderRepository.findByUserId(userId, paging.toPageable());

		return orderList.map(OrderListResponse::from);
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

		List<OrderProductResponse> products = orderProductRepository.findByOrderId(orderId).stream()
			.map(
			orderProduct -> {
				var product = Objects.requireNonNull(orderProduct.getProduct(), ErrorCode.NOT_FOUND_PRODUCT.getMessage());

				return OrderProductResponse.from(
					orderProduct,
					product
				);
			}
		).toList();

		//TODO: payment 정보 반환

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
}
