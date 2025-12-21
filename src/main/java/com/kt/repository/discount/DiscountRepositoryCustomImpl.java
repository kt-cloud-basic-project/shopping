package com.kt.repository.discount;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.discount.QDiscount;
import com.kt.domain.discountMembership.QDiscountMembership;
import com.kt.domain.membership.QMembership;
import com.kt.domain.user.QUser;
import com.kt.dto.discount.response.DiscountListResponse;
import com.kt.dto.discount.response.DiscountUserResponse;
import com.kt.dto.discount.response.QDiscountListResponse;
import com.kt.dto.discount.response.QDiscountUserResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiscountRepositoryCustomImpl implements DiscountRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QDiscount discount = QDiscount.discount;
	private final QUser user = QUser.user;
	private final QMembership membership = QMembership.membership;
	private final QDiscountMembership discountMembership = QDiscountMembership.discountMembership;

	@Override
	public Page<DiscountListResponse> getAllDiscount(Pageable pageable) {

		var content = queryFactory
			.select(new QDiscountListResponse(
				discount.id,
				discount.name,
				discount.targetType,
				discount.type,
				discount.isCombinable,
				discount.value
			))
			.from(discount)
			.orderBy(discount.id.desc())
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
		return Optional.ofNullable(
			queryFactory
				.select(new QDiscountUserResponse(
					user.loginId,
					user.name,
					membership.level,
					discount.type,
					discount.name,
					discount.isCombinable,
					discount.value
				))
				.from(user)
				.join(user.membership, membership)
				.join(discountMembership).on(discountMembership.membership.eq(membership))
				.join(discountMembership.discount, discount)
				.where(user.id.eq(userId))
				.fetchOne()
		).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DISCOUNT_MEMBERSHIP));
	}
}
