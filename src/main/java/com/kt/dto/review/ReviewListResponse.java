package com.kt.dto.review;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;

import com.kt.domain.review.Review;
import com.querydsl.core.annotations.QueryProjection;

public record ReviewListResponse(
	Long id,
	String userId,
	String title,
	String description,
	Integer star,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
){
	public static Page<ReviewListResponse> fromList(Page<Review> page) {
		return page.map(review -> new ReviewListResponse(
			review.getId(),
			review.getUser().getLoginId(),
			review.getTitle(),
			review.getDescription(),
			review.getStar(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		));
	}

	@QueryProjection
	public ReviewListResponse {
	}

}
