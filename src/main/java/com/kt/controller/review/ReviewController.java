package com.kt.controller.review;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.ApiResult;
import com.kt.common.Paging;
import com.kt.common.SwaggerAssistance;
import com.kt.dto.review.ReviewReqeust;
import com.kt.dto.review.ReviewResponse;
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
		@PathVariable Long productId,
		@Valid @RequestBody ReviewReqeust.Create reqeust
	) {

		reviewService.create(productId, reqeust);

		return ApiResult.ok();
	}

	@GetMapping("/reviews/me")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<ReviewResponse.ReviewList>> myReviewList(
		Paging paging
	) {

		Page<ReviewResponse.ReviewList> reviews = reviewService.myReviewList(2L, paging.toPageable());

		return ApiResult.ok(reviews);
	}

	@PutMapping("/reviews/{reviewId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> update(
		@PathVariable Long reviewId,
		@Valid @RequestBody ReviewReqeust.Update request
	) {

		reviewService.update(reviewId, request);

		return ApiResult.ok();
	}

	@DeleteMapping("/reviews/{reviewId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> delete(
		@PathVariable Long reviewId
	) {

		reviewService.delete(reviewId);

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