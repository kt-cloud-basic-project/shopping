package com.kt.repository.discountmembership;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.discountMembership.QDiscountMembership;
import com.kt.dto.discount.response.DiscountMembershipDetailResponse;
import com.kt.dto.discount.response.QDiscountMembershipDetailResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiscountMembershipCustomImpl implements DiscountMembershipCustom {
	private final JPAQueryFactory queryFactory;
	private final QDiscountMembership discountMembership = QDiscountMembership.discountMembership;

	@Override
	public DiscountMembershipDetailResponse findDiscountDetailByDiscountId(Long discountId) {
		return Optional.ofNullable(
			queryFactory
				.select(new QDiscountMembershipDetailResponse(
					discountMembership.discount.id,
					discountMembership.discount.name,
					discountMembership.discount.targetType,
					discountMembership.discount.type,
					discountMembership.discount.isCombinable,
					discountMembership.discount.value,
					discountMembership.membership.id,
					discountMembership.membership.level
				))
				.from(discountMembership)
				.join(discountMembership.discount)
				.join(discountMembership.membership)
				.where(discountMembership.discount.id.eq(discountId))
				.fetchOne()
		).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_DISCOUNT_MEMBERSHIP));
	}
}
