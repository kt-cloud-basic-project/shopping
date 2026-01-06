package com.kt.repository.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.domain.discount.QDiscount;
import com.kt.domain.membership.QMembership;
import com.kt.domain.order.QOrder;
import com.kt.domain.orderproduct.QOrderProduct;
import com.kt.domain.payment.QPayment;
import com.kt.domain.paymenttype.QPaymentType;
import com.kt.domain.user.QUser;
import com.kt.dto.payment.PaymentListResponse;
import com.kt.dto.payment.PaymentOrderInfoResponse;
import com.kt.dto.payment.QPaymentListResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryCustomImpl implements  PaymentRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QPayment payment = QPayment.payment;
	private final QOrder order = QOrder.order;
	private final QPaymentType paymentType = QPaymentType.paymentType;
	private final QUser user = QUser.user;
	private final QOrderProduct orderProduct = QOrderProduct.orderProduct;
	private final QMembership membership = QMembership.membership;
	private final QDiscount discount = QDiscount.discount;

	@Override
	public Page<PaymentListResponse> getMyAllPayment(Long userId, Pageable pageable) {

		var content = queryFactory
			.select(new QPaymentListResponse(
				payment.id,
				order.id,
				user.loginId,
				user.name,
				order.orderStatus,
				paymentType.name,
				payment.totalPrice,
				payment.deliveryFee,
				payment.finalPrice,
				payment.createdAt
				))
			.from(payment)
			.join(payment.order, order)
			.join(order.user, user)
			.join(payment.paymentType, paymentType)
			.where(
				order.user.id.eq(userId),
				payment.isDeleted.eq(false)
			)
			.orderBy(payment.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		var total = queryFactory
			.select(payment.id)
			.from(payment)
			.join(payment.order, order)
			.join(order.user, user)
			.join(payment.paymentType, paymentType)
			.where(
				order.user.id.eq(userId),
				payment.isDeleted.eq(false)
			)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public PaymentOrderInfoResponse getPaymentDetailByOrderId(Long orderId) {
		return null;
	}

}
