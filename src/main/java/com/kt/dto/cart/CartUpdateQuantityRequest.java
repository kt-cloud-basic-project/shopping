package com.kt.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartUpdateQuantityRequest(
	@NotNull
	@Positive
	Integer productCount
) {
}
