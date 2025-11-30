package com.kt.repository.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.domain.product.Product;
import com.kt.domain.product.ProductStatus;
import com.kt.domain.product.QProduct;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QProduct product = QProduct.product;

	@Override
	public Page<Product> search(String keyword, Long categoryId, Pageable pageable) {

		//status : ACTIVATED + stock >=1 + 삭제되지 않은 상품 중
		BooleanExpression booleanBuilder = product.status.eq(ProductStatus.ACTIVATED)
			.and(product.stock.goe(1))
			.and(product.isDeleted.isFalse());

		//입력 여부에 따라 keyword 또는 categoryId 또는 keyword&&categoryId 기반 검색 목록 조회
		if (keyword != null && !keyword.isBlank()) {
			booleanBuilder = booleanBuilder.and(product.name.containsIgnoreCase(keyword));
		}

		if (categoryId != null) {
			booleanBuilder = booleanBuilder.and(product.category.id.eq(categoryId));
		}

		var content = queryFactory
			.selectFrom(product)
			.where(booleanBuilder)
			.orderBy(product.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		var total = queryFactory
			.select(product.id)
			.from(product)
			.where(booleanBuilder)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}

}
