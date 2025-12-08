package com.kt.dto.category;

import java.util.List;

public record CategoryListResponse(
	List<String> types
) {
	public static CategoryListResponse from(List<String> categoryTypes) {
		return new CategoryListResponse(categoryTypes);
	}
}
