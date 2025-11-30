package com.kt.dto.membership;

import jakarta.validation.constraints.NotBlank;

public record MembershipCreateRequest(
	@NotBlank(message = "멤버십 등급은 필수입니다")
	String level
) {
}
