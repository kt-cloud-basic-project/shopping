package com.kt.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductStatus {

	ACTIVATED("판매 중"),
	SOLD_OUT("품절"),
	IN_ACTIVATED("판매 중지");

	private final String description;
}
