package com.kt.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest(

	@Schema(description = "주문 ID", example = "1")
	@NotNull(message = "주문 ID는 필수입니다")
	Long orderId,

	@Schema(description = "결제 수단 아이디", example = "1")
	@NotNull(message = "결제 수단 ID은 필수입니다")
	Long paymentTypeId
) {

}
