package com.kt.domain.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryType {
	TOP("상의"),
	OUTER("아우터"),
	PANTS("바지"),
	SHOES("신발"),
	BAG("가방"),
	ACCESSORY("패션소품");

	private final String description;
}
