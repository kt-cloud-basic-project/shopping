package com.kt.dto.order;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderCreateRequest(
	@NotBlank(message = "주문자 이름 입력은 필수입니다")
	String receiverName,
	@NotBlank(message = "주문자 전화번호 입력은 필수입니다")
	String receiverPhone,
	@NotNull(message = "배송지 정보 입력은 필수입니다")
	Long receiverAddressId,
	@NotNull(message = "상품 선택은 필수입니다")
	List<OrderProductRequest> products
) {
}