package com.kt.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentTossCancelRequest(
	@Schema(description = "취소 사유", example = "고객 변심")
	@NotBlank(message = "취소 사유는 필수입니다")
	String cancelReason,

	@Schema(description = "취소 금액", example = "50000")
	@NotNull(message = "취소 금액은 필수입니다")
	Long cancelAmount
) {
}
