package com.kt.repository.orderproduct;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kt.domain.order.OrderStatus;
import com.kt.domain.orderproduct.QOrderProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryCustomImpl implements OrderProductRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QOrderProduct orderProduct =  QOrderProduct.orderProduct;

	@Override
	public boolean hasInvalidStatus(Long variantId) {

		List<OrderStatus> validStatuses = List.of(
			OrderStatus.DELIVERED,
			OrderStatus.CANCELLED,
			OrderStatus.RETURNED,
			OrderStatus.REFUNDED
		);

		return queryFactory
			.selectOne()
			.from(orderProduct)
			.where(
				orderProduct.variantId.eq(variantId)
						.and(orderProduct.order.orderStatus.notIn(validStatuses))
			)
			.fetchFirst() != null;   //존재하는 상품이 있으면 true
	}
}
