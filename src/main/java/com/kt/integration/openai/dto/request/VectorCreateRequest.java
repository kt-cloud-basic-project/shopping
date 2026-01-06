package com.kt.integration.openai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record VectorCreateRequest(
	@Schema(description = "벡터 스토어 이름", example = "FAQ_VectorStore")
	String name,

	@Schema(description = "벡터 스토어 설명", example = "FAQ 챗봇용 벡터 저장소")
	String description
) {}

