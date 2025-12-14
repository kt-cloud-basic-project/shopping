package com.kt.dto.order;

import com.kt.domain.order.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(

	@Schema(description = "주문 상태 값", example = "PAID")
	@NotNull(message = "주문 상태값은 필수입니다")
	OrderStatus orderStatus
) {
}
