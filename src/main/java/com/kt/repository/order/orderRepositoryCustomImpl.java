package com.kt.repository.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.domain.membership.QMembership;
import com.kt.domain.order.Order;
import com.kt.domain.order.QOrder;
import com.kt.domain.orderproduct.QOrderProduct;
import com.kt.domain.product.QProduct;
import com.kt.domain.user.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class orderRepositoryCustomImpl implements OrderRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QOrder order = QOrder.order;
	private final QOrderProduct orderProduct = QOrderProduct.orderProduct;
	private final QProduct product = QProduct.product;
	private final QUser user = QUser.user;
	private final QMembership membership = QMembership.membership;

	@Override
	public Page<Order> getOrders(Long userId, Pageable pageable) {

		// order ID 목록 조회
		List<Long> orderIds = queryFactory
			.select(order.id)
			.from(order)
			.where(order.user.id.eq(userId))
			.orderBy(order.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// order 기본 정보 조회 (취소된 정보도 가져오는게 맞죠?) -> Entity로 조회 시 fetch join 활용
		var content = queryFactory
			.select(order)
			.distinct()
			.from(order)
			.join(order.orderProducts, orderProduct).fetchJoin() // order가 있으면 무조건 orderProduct도 있으니까 inner join
			.join(order.user, user).fetchJoin() // user가 있어야 order도 있는거니 inner join
			.join(orderProduct.product, product).fetchJoin() // product도 무조건 있어야 하니까 inner join -> 하드딜리트였으면 left join 해야함
			.join(user.membership, membership).fetchJoin()
			.where(order.id.in(orderIds))
			.orderBy(order.createdAt.desc())
			.fetch();

		var total = queryFactory
			.select(order.id)
			.from(order)
			.where(order.user.id.eq(userId))
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Order getOrderDetailById(Long userId, Long orderId) {

		return queryFactory
			.select(order)
			.from(order)
			.join(order.user, user).fetchJoin()
			.join(user.membership, membership).fetchJoin()
			.where(
				order.id.eq(orderId),
				order.user.id.eq(userId),
				order.isDeleted.eq(false)
			)
			.fetchOne();
	}
}
