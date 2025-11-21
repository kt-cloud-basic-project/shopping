package com.kt.dto.membership;

import jakarta.validation.constraints.NotBlank;

public record MembershipCreateRequest(
	@NotBlank(message = "등급 이름은 필수입니다")
	String level
) {
}
