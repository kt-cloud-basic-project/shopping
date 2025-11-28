package com.kt.controller.review;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.common.request.Paging;
import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.review.ReviewCreateRequest;
import com.kt.dto.review.ReviewListResponse;
import com.kt.dto.review.ReviewUpdateRequest;
import com.kt.security.CustomUserDetails;
import com.kt.service.review.ReviewService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Review", description = "리뷰 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController extends SwaggerAssistance {

	private final ReviewService reviewService;

	@PostMapping("/products/{productId}/reviews")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> create(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long productId,
		@Valid @RequestBody ReviewCreateRequest request
	) {

		reviewService.create(currentUser.getId(), productId, request);

		return ApiResult.ok();
	}

	@GetMapping("/reviews/me")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<ReviewListResponse>> myReviewList(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		Paging paging
	) {

		Page<ReviewListResponse> reviews = reviewService.myReviewList(currentUser.getId(), paging.toPageable());

		return ApiResult.ok(reviews);
	}

	@PutMapping("/reviews/{reviewId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> update(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long reviewId,
		@Valid @RequestBody ReviewUpdateRequest request
	) {

		reviewService.update(currentUser.getId(), reviewId, request);

		return ApiResult.ok();
	}

	@DeleteMapping("/reviews/{reviewId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> delete(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long reviewId
	) {

		reviewService.delete(currentUser.getId(), reviewId);

		return ApiResult.ok();
	}

	/*@GetMapping("/products/{productId}/reviews")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<ReviewResponse.ReviewList>> productReviewList(
		@PathVariable Long productId,
		Paging paging
	) {

		Page<ReviewResponse.ReviewList> reviews = reviewService.productReviewList(productId, paging.toPageable());

		return ApiResult.ok(reviews);
	}*/

}