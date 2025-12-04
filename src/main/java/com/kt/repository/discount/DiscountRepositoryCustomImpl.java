package com.kt.repository.discount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.domain.discount.QDiscount;
import com.kt.domain.membership.QMembership;
import com.kt.domain.user.QUser;
import com.kt.dto.discount.DiscountListResponse;
import com.kt.dto.discount.DiscountUserResponse;
import com.kt.dto.discount.QDiscountListResponse;
import com.kt.dto.discount.QDiscountUserResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiscountRepositoryCustomImpl implements DiscountRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QDiscount discount = QDiscount.discount;
	private final QMembership membership = QMembership.membership;
	private final QUser user = QUser.user;

	@Override
	public Page<DiscountListResponse> getAllDiscount(Pageable pageable) {

		var content = queryFactory
			.select(new QDiscountListResponse(
				membership.id,
				membership.level,
				discount.id,
				discount.name,
				discount.type,
				discount.value
			))
			.from(discount)
			.join(membership).on(discount.membership.id.eq(membership.id))
			.orderBy(membership.id.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		var total = queryFactory
			.select(discount.id)
			.from(discount)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public DiscountUserResponse findDiscountUserById(Long userId) {

		return queryFactory
			.select(new QDiscountUserResponse(
				user.loginId,
				user.name,
				membership.level,
				discount.type,
				discount.name
			))
			.from(discount)
			.join(discount.membership, membership)
			.join(user).on(membership.id.eq(user.membership.id))
			.where(user.id.eq(userId))
			.fetchOne();
	}
}
