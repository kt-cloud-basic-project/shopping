package com.kt.controller.review;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.ApiResult;
import com.kt.common.SwaggerAssistance;
import com.kt.dto.review.ReviewCreateReqeust;
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
		@Valid @RequestBody ReviewCreateReqeust reqeust) {

		reviewService.create(productId, reqeust);

		return ApiResult.ok();
	}
}