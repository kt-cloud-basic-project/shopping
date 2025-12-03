package com.kt.dto.membership;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MembershipCreateRequest(

	@Schema(description = "멤버십 등급", example = "SILVER")
	@NotBlank(message = "멤버십 등급은 필수입니다")
	String level
) {
}
