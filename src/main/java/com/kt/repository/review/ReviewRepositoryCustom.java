package com.kt.repository.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.dto.review.ReviewListResponse;

public interface ReviewRepositoryCustom {
	Page<ReviewListResponse> getMyAllReview(Long userId, Pageable pageable);

	Page<ReviewListResponse> getProductAllReview(Long ProductId, Pageable pageable);
}
