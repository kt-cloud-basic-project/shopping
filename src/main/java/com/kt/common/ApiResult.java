package com.kt.common;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResult<T> {
	private String code;
	private String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;
	private LocalDateTime timestamp;

	public ApiResult<Void> ok() {
		return ApiResult.of("ok", "성공", null, this.timestamp);
	}

	public <T> ApiResult<T> ok(T data) {
		return ApiResult.of("ok", "성공", data, this.timestamp);
	}

	private static <T> ApiResult<T> of(String code, String message, T data,  LocalDateTime timestamp) {
		return new ApiResult<>(code, message, data, timestamp);
	}
}
