package com.kt.service.payment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.common.support.Preconditions;
import com.kt.domain.discount.Discount;
import com.kt.domain.discount.policy.DiscountPolicy;
import com.kt.domain.discount.policy.DiscountPolicyFactory;
import com.kt.domain.order.Order;
import com.kt.domain.order.OrderStatus;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.payment.Payment;
import com.kt.dto.discount.response.DiscountInfo;
import com.kt.dto.discount.response.DiscountResult;
import com.kt.dto.order.response.OrderProductResponse;
import com.kt.dto.payment.PaymentCreateRequest;
import com.kt.dto.payment.PaymentDetailResponse;
import com.kt.dto.payment.PaymentListResponse;
import com.kt.dto.payment.PaymentOrderInfoResponse;
import com.kt.dto.payment.PaymentTossCancelRequest;
import com.kt.dto.payment.PaymentTossCancelResponse;
import com.kt.dto.payment.PaymentTossConfirmRequest;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.order.OrderRepositoryCustom;
import com.kt.repository.orderproduct.OrderProductRepositoryCustom;
import com.kt.repository.payment.PaymentRepository;
import com.kt.repository.payment.PaymentRepositoryCustom;
import com.kt.repository.paymenttype.PaymentTypeRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.discount.DiscountCalcService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
	private static final Long DELIVERY_FEE = 3000L;

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final PaymentTypeRepository paymentTypeRepository;
	private final UserRepository userRepository;
	private final PaymentRepositoryCustom paymentRepositoryCustom;
	private final DiscountCalcService discountCalcService;
	private final OrderRepositoryCustom orderRepositoryCustom;
	private final OrderProductRepositoryCustom orderProductRepositoryCustom;

	public Long create(Map<String, Object> tossResponse, Long userId) {
		String orderId = tossResponse.get("orderId").toString().split("-")[0];

		var order = orderRepository.findByIdAndUserIdOrThrow(Long.parseLong(orderId), userId, ErrorCode.NOT_FOUND_ORDER);

		var paymentType = paymentTypeRepository.findByName(tossResponse.get("method").toString()).orElseThrow();

		long amount = Long.parseLong(tossResponse.get("totalAmount").toString());

		var payment = new Payment(
			order,
			paymentType,
			amount,
			DELIVERY_FEE,
			amount - DELIVERY_FEE,
			tossResponse.get("paymentKey").toString()
		);

		Payment savedPayment = paymentRepository.save(payment);

		order.updateStatus(OrderStatus.PAID);

		return savedPayment.getId();
	}

	public Page<PaymentListResponse> getMyAllPayment(Long userId, Pageable pageable) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		return paymentRepositoryCustom.getMyAllPayment(user.getId(), pageable);
	}

	public PaymentDetailResponse getPayment(Long userId, Long paymentId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var payment = paymentRepository.findByIdAndOrderUserIdAndIsDeletedFalseOrThorw(paymentId, user.getId(), ErrorCode.NOT_FOUND_PAYMENT);

		return new PaymentDetailResponse(
			payment.getId(),
			payment.getOrder().getId(),
			payment.getPaymentKey(),
			payment.getOrder().getUser().getLoginId(),
			payment.getOrder().getUser().getName(),
			payment.getOrder().getOrderStatus(),
			payment.getPaymentType().getName(),
			payment.getTotalPrice(),
			payment.getDeliveryFee(),
			payment.getFinalPrice(),
			payment.getCreatedAt()
		);
	}

	public PaymentOrderInfoResponse getPaymentDetail(Long userId, Long orderId) {

		//1. 주문자 정보 등 조회
		Order order = orderRepositoryCustom.getOrderDetailById(userId, orderId);

		//2. 주문 상품 정보 조회
		List<OrderProduct> orderProducts = orderProductRepositoryCustom.getOrderProductByOrderId(orderId);

		//3. 멤버십 할인 및 계산
		Discount membershipDiscount = discountCalcService.getMembershipDiscount(userId);

		//4. 상품별 할인 및 계산
		Map<Long, List<Discount>> productDiscounts = discountCalcService.getProductsDiscount(
			orderProducts.stream()
			.map(op -> op.getProduct().getId())
			.distinct()
			.toList());

		long totalProductPrice = 0L;
		long totalDiscountPrice = 0L;
		List<PaymentOrderInfoResponse.OrderProductInfo> orderProductInfos = new ArrayList<>();


		for (OrderProduct op : orderProducts) {
			long originalPrice = op.getProduct().getPrice() * op.getCount();

			DiscountResult result = discountCalcService.calculate(
				originalPrice,
				membershipDiscount,
				productDiscounts.getOrDefault(op.getProduct().getId(), List.of())
			);

			List<DiscountInfo> discountInfos = discountCalcService.getDiscountsInfoList(
				originalPrice,
				membershipDiscount,
				productDiscounts.getOrDefault(op.getProduct().getId(), List.of())
			);

			List<PaymentOrderInfoResponse.AppliedDiscountInfo> appliedDiscounts = discountInfos.stream()
				.map(di -> new PaymentOrderInfoResponse.AppliedDiscountInfo(
					di.discountName(),
					di.discountType(),
					di.discountPrice()
				))
				.toList();

			orderProductInfos.add(new PaymentOrderInfoResponse.OrderProductInfo(
				op.getProduct().getId(),
				op.getProduct().getName(),
				op.getVariantId(),
				op.getCount(),
				op.getProduct().getPrice(),
				originalPrice,
				result.discountPrice(),
				result.discountedPrice(),
				appliedDiscounts
			));

			totalProductPrice += originalPrice;
			totalDiscountPrice += result.discountPrice();
		}

		Long finalPrice = totalProductPrice - totalDiscountPrice + DELIVERY_FEE;

		return new PaymentOrderInfoResponse(
			order.getId(),
			order.getOrderStatus(),
			order.getOrderStatus().name(),
			order.getCreatedAt(),
			order.getReceiverName(),
			order.getReceiverPhone(),
			order.getReceiverAddress(),
			new PaymentOrderInfoResponse.UserPaymentInfo(
				order.getUser().getName(),
				order.getUser().getEmail(),
				order.getUser().getMobile(),
				new PaymentOrderInfoResponse.MembershipInfo(
					order.getUser().getMembership().getLevel()
				),
				order.getUser().getMoney()
			),
			orderProductInfos,
			new PaymentOrderInfoResponse.PriceInfo(
				totalProductPrice,
				totalDiscountPrice,
				DELIVERY_FEE,
				finalPrice
			)
		);
	}

	public PaymentDetailResponse getPaymentByOrderId(Long userId, Long orderId) {
		var order = orderRepository.findByIdAndUserIdOrThrow(orderId, userId, ErrorCode.NOT_FOUND_ORDER);

		var payment = paymentRepository.findByOrderIdAndIsDeletedFalseOrThorw(order.getId(), ErrorCode.NOT_FOUND_PAYMENT);

		return new PaymentDetailResponse(
			payment.getId(),
			payment.getOrder().getId(),
			payment.getPaymentKey(),
			payment.getOrder().getUser().getLoginId(),
			payment.getOrder().getUser().getName(),
			payment.getOrder().getOrderStatus(),
			payment.getPaymentType().getName(),
			payment.getTotalPrice(),
			payment.getDeliveryFee(),
			payment.getFinalPrice(),
			payment.getCreatedAt()
		);
	}

	public PaymentTossCancelResponse cancelPayment(PaymentTossCancelRequest request, Long userId, Long paymentId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var payment = paymentRepository.findByIdAndOrderUserIdAndIsDeletedFalseOrThorw(paymentId, user.getId(), ErrorCode.NOT_FOUND_PAYMENT);
		Preconditions.validate(payment.getTotalPrice().equals(request.cancelAmount()), ErrorCode.CANNOT_CANCEL_PAYMENT_AMOUNT_MISMATCH);

		OrderStatus status = payment.getOrder().getOrderStatus();
		boolean canCancel = status == OrderStatus.PAID ||
			status == OrderStatus.PROCESSING ||
			status == OrderStatus.SHIPPED ||
			status == OrderStatus.DELIVERED;
		Preconditions.validate(canCancel, ErrorCode.CANNOT_CANCEL_ORDER);

		payment.delete();

		payment.getOrder().updateStatus(OrderStatus.CANCELLED);

		return new PaymentTossCancelResponse(
			payment.getFinalPrice(),
			request.cancelReason(),
			LocalDateTime.now().toString()
		);

	}

	public String getPaymentKey(Long paymentId, Long userId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var payment = paymentRepository.findByIdAndOrderUserIdAndIsDeletedFalseOrThorw(paymentId, user.getId(), ErrorCode.NOT_FOUND_PAYMENT);

		return payment.getPaymentKey();
	}

}
