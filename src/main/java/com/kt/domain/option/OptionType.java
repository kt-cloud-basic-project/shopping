package com.kt.domain.option;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OptionType {
	SIZE("사이즈"),
	COLOR("색");

	private final String description;
}
