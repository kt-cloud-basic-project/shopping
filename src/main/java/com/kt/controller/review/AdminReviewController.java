package com.kt.controller.review;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.common.support.SwaggerAssistance;
import com.kt.service.review.ReviewService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Review", description = "관리자 리뷰 기능 관리  API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminReviewController extends SwaggerAssistance {

	private final ReviewService reviewService;

	@DeleteMapping("/reviews/{reviewId}/hide")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> hide(
		@PathVariable Long reviewId
	) {

		reviewService.hide(reviewId);

		return ApiResult.ok();
	}
}
