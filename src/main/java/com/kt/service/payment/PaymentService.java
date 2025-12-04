package com.kt.service.payment;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.support.Preconditions;
import com.kt.domain.discount.Discount;
import com.kt.domain.membership.Membership;
import com.kt.domain.order.OrderStatus;
import com.kt.domain.payment.Payment;
import com.kt.dto.payment.PaymentCreateRequest;
import com.kt.dto.payment.PaymentDetailResponse;
import com.kt.dto.payment.PaymentListResponse;
import com.kt.dto.review.ReviewListResponse;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.payment.PaymentRepository;
import com.kt.repository.payment.PaymentRepositoryCustom;
import com.kt.repository.paymenttype.PaymentTypeRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final PaymentTypeRepository paymentTypeRepository;
	private final UserRepository userRepository;
	private final PaymentRepositoryCustom paymentRepositoryCustom;
	private final DiscountRepository discountRepository;

	@Transactional
	public void create(PaymentCreateRequest request, Long userId) {
		var order = orderRepository.findByIdAndUserIdOrThrow(request.orderId(), userId, ErrorCode.NOT_FOUND_ORDER);

		var paymentType = paymentTypeRepository.findById(request.paymentTypeId())
			.orElseThrow();

		// 할인은 있을 수도 없을 수도(optional)
		Discount discount = Optional.ofNullable(order.getUser().getMembership())
			.map(Membership::getId)
			.flatMap(discountRepository::findByMembershipId)
			.orElse(null);

		int price = order.getOrderProducts().stream()
			.mapToInt(orderproduct -> (int) (orderproduct.getProduct().getPrice() * orderproduct.getCount()))
			.sum();

		long totalPrice = discount != null ? discount.calcDiscountFinalPrice((long) price) : (long) price;

		int delivery = 3000;

		int finalPrice = (int)totalPrice + delivery;

		var payment = new Payment(order, paymentType, (int)totalPrice, delivery, finalPrice);

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
