package com.kt.repository.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.dto.review.ReviewListResponse;

public interface ReviewRepositoryCustom {
	Page<ReviewListResponse> myReviewList(Long userId, Pageable pageable);
}
