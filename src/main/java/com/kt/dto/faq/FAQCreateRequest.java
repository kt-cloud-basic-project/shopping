package com.kt.dto.faq;

import com.kt.domain.faq.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FAQCreateRequest(
	@Schema(description = "FAQ 제목 (질문 형태 권장)", example = "환불은 얼마나 걸리나요?")
	@NotBlank(message = "제목 입력은 필수입니다")
	String title,
	@Schema(description = "FAQ 답변 내용", example = "환불은 신청 후 영업일 2~3일 이내에 처리됩니다.")
	@NotBlank(message = "내용 입력은 필수입니다")
	String content,
	@Schema(description = "FAQ 카테고리", example = "PAYMENT")
	@NotNull(message = "카테고리 입력은 필수입니다")
	Category category
) {
}
