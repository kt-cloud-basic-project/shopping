package com.kt.dto.payment;

import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequest(
	@NotNull(message = "주문 ID는 필수입니다")
	Long orderId,

	@NotNull(message = "결제수단 ID은 필수입니다")
	Long paymentTypeId
) {

}
