package com.kt.dto.variant;

import java.util.List;

public record VariantListResponse(
	String type,
	List<String> detail
) {
}
