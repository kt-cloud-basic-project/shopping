package com.kt.integration.openai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record VectorSearchRequest(
	@Schema(description = "검색 쿼리", example = "환불 정책")
	String query
) {}

