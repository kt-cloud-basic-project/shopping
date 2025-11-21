package com.kt.dto.review;

import java.util.List;

import org.springframework.data.domain.Page;

import com.kt.domain.review.Review;

import io.swagger.v3.oas.annotations.media.Schema;

public class ReviewResponse {

	@Schema(name = "ReviewResponse.ReviewList")
	public record ReviewList(
		String title,
		String description,
		Integer star,
		String createdAt,
		String updatedAt
	) {

		// 단건 반환
		public static ReviewList from(Review review) {
			return new ReviewList(
				review.getTitle(),
				review.getDescription(),
				review.getStar(),
				review.getCreatedAt().toString(),
				review.getUpdatedAt().toString()
			);
		}

		// 리스트 반환
		public static Page<ReviewList> fromList(Page<Review> reviewPage) {
			return reviewPage.map(ReviewList::from);
		}
	}
}
