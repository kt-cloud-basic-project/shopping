package com.kt.dto.paymenttype;

import jakarta.validation.constraints.NotBlank;

public record PaymentTypeCreateRequest(
	@NotBlank(message = "결제방법 이름은 필수입니다")
	String name
) {
}
