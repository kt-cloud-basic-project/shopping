package com.kt.dto.order;

import com.kt.domain.order.OrderStatus;

import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(
	@NotNull(message = "주문 상태값은 필수입니다")
	OrderStatus orderStatus
) {
}
