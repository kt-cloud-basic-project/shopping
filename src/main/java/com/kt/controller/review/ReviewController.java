package com.kt.controller.review;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.ApiResult;
import com.kt.common.SwaggerAssistance;
import com.kt.domain.review.Review;
import com.kt.dto.review.ReviewCreateReqeust;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Review", description = "리뷰 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController extends SwaggerAssistance {

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> create(@Valid @RequestBody ReviewCreateReqeust reqeust) {
		return ApiResult.ok();
	}
}
