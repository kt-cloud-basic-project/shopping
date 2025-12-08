package com.kt.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewUpdateRequest (

	@Schema(description = "리뷰 제목", example = "최고의 상품입니다")
	@NotBlank(message = "제목은 필수입니다")
	String title,

	@Schema(description = "리뷰 상세 내용", example = "정말 만족스러운 구매였습니다")
	@NotBlank(message = "상세 내용은 필수입니다")
	String description,

	@Schema(description = "리뷰 별점", example = "5")
	@NotNull(message = "별점은 필수입니다")
	@Min(value = 1, message = "별점은 1 이상이어야 합니다")
	@Max(value = 5, message = "별점은 5 이하여야 합니다")
	Integer star
){
}
