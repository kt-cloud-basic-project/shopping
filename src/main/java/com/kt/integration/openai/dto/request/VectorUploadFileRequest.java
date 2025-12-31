package com.kt.integration.openai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

public record VectorUploadFileRequest(
	@Schema(description = "OpenAI에 업로드 된 파일 ID")
	@JsonProperty("file_id")
	String id
) {
}
