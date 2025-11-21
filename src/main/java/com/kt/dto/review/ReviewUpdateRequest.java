package com.kt.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewUpdateRequest (
	@NotBlank(message = "상세 내용은 필수입니다")
	String description,

	@NotNull(message = "별점은 필수입니다")
	@Min(value = 1, message = "별점은 1 이상이어야 합니다")
	@Max(value = 5, message = "별점은 5 이하여야 합니다")
	Integer star
){
}
