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

import com.kt.common.ratelimit.annotation.KeyType;
import com.kt.common.ratelimit.annotation.RateLimit;
import com.kt.common.response.ApiResult;
import com.kt.common.request.Paging;
import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.review.ReviewCreateRequest;
import com.kt.dto.review.ReviewListResponse;
import com.kt.dto.review.ReviewUpdateRequest;
import com.kt.security.CustomUserDetails;
import com.kt.service.review.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Review", description = "Review 유저용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController extends SwaggerAssistance {

	private final ReviewService reviewService;

	// review와 orderProduct는 종속관계가 아니다..
	@PostMapping("/order-product/{orderProductId}/reviews")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "리뷰 등록")
	public ApiResult<Long> create(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long orderProductId,
		@Valid @RequestBody ReviewCreateRequest request
	) {
		return ApiResult.ok(reviewService.create(currentUser.getId(), orderProductId, request));
	}


	@RateLimit(
		capacity = 1,
		refillTokens = 1,
		keyType = KeyType.USER_ID
	)
	@GetMapping("/reviews/me")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "내 리뷰 목록 조회")
	public ApiResult<Page<ReviewListResponse>> getMyAllReview(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@Valid Paging paging
	) {

		Page<ReviewListResponse> reviews = reviewService.getMyAllReview(currentUser.getId(), paging.toPageable());

		return ApiResult.ok(reviews);
	}

	@PutMapping("/reviews/{reviewId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "리뷰 수정")
	public ApiResult<Long> update(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long reviewId,
		@Valid @RequestBody ReviewUpdateRequest request
	) {
		return ApiResult.ok(reviewService.update(currentUser.getId(), reviewId, request));
	}

	@DeleteMapping("/reviews/{reviewId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "리뷰 삭제")
	public ApiResult<Long> delete(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long reviewId
	) {
		return ApiResult.ok(reviewService.delete(currentUser.getId(), reviewId));
	}

	@GetMapping("/products/{productId}/reviews")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "특정 상품 리뷰 목록 조회")
	public ApiResult<Page<ReviewListResponse>> getProductAllReview(
		@PathVariable Long productId,
		@Valid Paging paging
	) {

		Page<ReviewListResponse> reviews = reviewService.getProductAllReview(productId, paging.toPageable());

		return ApiResult.ok(reviews);
	}

}