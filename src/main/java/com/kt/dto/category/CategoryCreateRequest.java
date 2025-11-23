package com.kt.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
	@NotBlank
	String type
) {
}
