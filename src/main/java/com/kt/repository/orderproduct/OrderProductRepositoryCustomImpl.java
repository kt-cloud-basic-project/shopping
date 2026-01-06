package com.kt.repository.orderproduct;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.kt.domain.order.OrderStatus;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.orderproduct.QOrderProduct;
import com.kt.domain.product.QProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryCustomImpl implements OrderProductRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QOrderProduct orderProduct =  QOrderProduct.orderProduct;
	private final QProduct product =  QProduct.product;

	@Override
	public boolean hasInvalidStatusWithVariantId(Long variantId) {

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

	@Override
	public boolean hasInvalidStatusWithProductId(Long productId) {

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
				orderProduct.product.id.eq(productId)
					.and(orderProduct.order.orderStatus.notIn(validStatuses))
			)
			.fetchFirst() != null;   //존재하는 상품이 있으면 true
	}

	@Override
	public List<OrderProduct> getOrderProductByOrderId(Long orderId) {
		return queryFactory
			.selectFrom(orderProduct)
			.join(orderProduct.product, product).fetchJoin()
			.where(orderProduct.order.id.eq(orderId))
			.fetch();
	}

}
