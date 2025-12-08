package com.kt.dto.paymenttype;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PaymentTypeCreateRequest(

	@Schema(description = "결제 수단 이름", example = "CREDIT_CARD")
	@NotBlank(message = "결제 수단 이름은 필수입니다")
	String name
) {
}
