package com.kt.domain.variant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VariantType {
	SIZE("사이즈"),
	COLOR("색상");

	private final String description;
}
