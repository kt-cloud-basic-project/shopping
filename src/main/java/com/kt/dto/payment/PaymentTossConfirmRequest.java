package com.kt.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentTossConfirmRequest(

	@Schema(description = "주문 ID", example = "1")
	@NotNull(message = "주문 ID는 필수입니다")
	String orderId,

	@Schema(description = "결제 KEY", example = "tgen_20260104212559xFAo0")
	@NotBlank(message = "결제 KEY는 필수입니다")
	String paymentKey,

	@Schema(description = "결제 금액", example = "50000")
	@NotNull(message = "결제 금액은 필수입니다")
	Long amount,

	@Schema(description = "결제타입", example = "간편결제")
	@NotBlank(message = "결제타입은 필수입니다")
	String method

) {
}
