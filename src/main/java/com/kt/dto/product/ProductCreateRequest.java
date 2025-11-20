package com.kt.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCreateRequest (
	@NotBlank
	String name,
	@NotBlank
	String description,
	@NotNull
	Long price,
	@NotNull
	Long stock,
	@NotNull
	Long categoryId
) {
}