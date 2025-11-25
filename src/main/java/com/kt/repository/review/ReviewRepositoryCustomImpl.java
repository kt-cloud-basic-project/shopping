package com.kt.repository.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.domain.review.QReview;
import com.kt.dto.review.QReviewListResponse;
import com.kt.dto.review.ReviewListResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QReview review = QReview.review;
	//private QOrderProduct orderProduct= QOrderProduct.orderProduct;

	@Override
	public Page<ReviewListResponse> myReviewList(Long userId, Pageable pageable) {

		var content = queryFactory
			.select(new QReviewListResponse(
				review.id,
				review.title,
				review.description,
				review.star,
				review.createdAt,
				review.updatedAt
				))
			.from(review)
			.where(
				review.user.id.eq(userId),
				review.isDeleted.eq(false)
			)
			.orderBy(review.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		var total = queryFactory
			.select(review.id)
			.from(review)
			.where(
				review.user.id.eq(userId),
				review.isDeleted.eq(false)
			)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}


}
