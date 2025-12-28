package com.kt.repository.discountproduct;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.discountProduct.QDiscountProduct;
import com.kt.dto.discount.response.DiscountProductDetailResponse;
import com.kt.dto.discount.response.QDiscountProductDetailResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DiscountProductCustomImpl implements DiscountProductCustom {
	private final JPAQueryFactory queryFactory;
	private final QDiscountProduct discountProduct = QDiscountProduct.discountProduct;


	@Override
	public DiscountProductDetailResponse findDiscountDetailByDiscountId(Long discountId) {
		return Optional.ofNullable(
			queryFactory
				.select(new QDiscountProductDetailResponse(
					discountProduct.discount.id,
					discountProduct.discount.name,
					discountProduct.discount.targetType,
					discountProduct.discount.type,
					discountProduct.discount.isCombinable,
					discountProduct.discount.value,
					discountProduct.product.id,
					discountProduct.product.name
				))
				.from(discountProduct)
				.join(discountProduct.discount)
				.join(discountProduct.product)
				.where(discountProduct.discount.id.eq(discountId))
				.fetchOne()
		).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_DISCOUNT_PRODUCT));
	}

}
