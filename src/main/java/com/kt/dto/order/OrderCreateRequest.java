package com.kt.dto.order;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(

	@Schema(description = "주문자 이름", example = "홍길동")
	@NotBlank(message = "주문자 이름 입력은 필수입니다")
	String receiverName,

	@Schema(description = "주문자 연락처", example = "010-1234-5678")
	@NotBlank(message = "주문자 전화번호 입력은 필수입니다")
	String receiverPhone,

	@Schema(description = "주문자 배송지 아이디", example = "1")
	@NotNull(message = "배송지 정보 입력은 필수입니다")
	Long receiverAddressId,

	@ArraySchema(
		schema = @Schema(implementation = OrderProductRequest.class),
		minItems = 1
	)
	@NotNull(message = "상품 선택은 필수입니다")
	List<OrderProductRequest> products
) {
}