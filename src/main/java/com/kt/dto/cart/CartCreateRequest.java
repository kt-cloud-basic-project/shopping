package com.kt.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartCreateRequest(
	@Schema(description = "상품 수량", example = "5")
	@NotNull(message = "상품 수량은 필수 값입니다")
	@Positive(message = "상품 수량은 최소 1개 이상이어야 합니다")
	Integer productCount,

	@Schema(description = "상품 옵션 아이디", example = "2")
	@NotNull(message = "상품 옵션은 필수 값입니다")
	Long variantId,

	@Schema(description = "상품 아이디", example = "1")
	@NotNull(message = "상품 아이디는 필수 값입니다")
	Long productId
){
}
