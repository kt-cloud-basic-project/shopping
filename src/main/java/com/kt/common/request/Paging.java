package com.kt.common.request;

import org.springframework.data.domain.PageRequest;

import jakarta.validation.constraints.Min;

public record Paging(
	@Min(value = 1, message = "페이지는 1 이상이어야 합니다")
	int page,
	int size
	//todo: 정렬기능도 추가 예정
) {
	public PageRequest toPageable() {
		return PageRequest.of(page - 1, size);
	}
}
