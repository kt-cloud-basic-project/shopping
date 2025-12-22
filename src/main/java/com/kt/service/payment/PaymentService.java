package com.kt.service.payment;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.domain.discount.Discount;
import com.kt.domain.order.OrderStatus;
import com.kt.domain.payment.Payment;
import com.kt.dto.discount.response.DiscountResult;
import com.kt.dto.payment.PaymentCreateRequest;
import com.kt.dto.payment.PaymentDetailResponse;
import com.kt.dto.payment.PaymentListResponse;
import com.kt.repository.order.OrderRepository;
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

	public void create(PaymentCreateRequest request, Long userId) {
		var order = orderRepository.findByIdAndUserIdOrThrow(request.orderId(), userId, ErrorCode.NOT_FOUND_ORDER);

		var paymentType = paymentTypeRepository.findById(request.paymentTypeId()).orElseThrow();

		// 멤버십 할인 조회
		Discount membershipDiscount = discountCalcService.getMembershipDiscount(userId);

		// 상품별 할인 조회
		Map<Long, List<Discount>> productDiscount = discountCalcService.getProductsDiscount(
			order.getOrderProducts().stream()
				.map(op -> op.getProduct().getId())
				.distinct()
				.toList());

		Long totalPrice = 0L;

		for (var orderProduct : order.getOrderProducts()) {
			DiscountResult result = discountCalcService.calculate(
				orderProduct.getProduct().getPrice() * orderProduct.getCount(),
				membershipDiscount,
				productDiscount.getOrDefault(orderProduct.getProduct().getId(), List.of())
			);

			totalPrice += result.discountedPrice();
			System.out.println("총 가격: " + totalPrice);
		}

		Long finalPrice = totalPrice + DELIVERY_FEE;
		var payment = new Payment(
			order,
			paymentType,
			totalPrice,
			DELIVERY_FEE,
			finalPrice
		);

		paymentRepository.save(payment);

		order.updateStatus(OrderStatus.SHIPPED);
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

}
