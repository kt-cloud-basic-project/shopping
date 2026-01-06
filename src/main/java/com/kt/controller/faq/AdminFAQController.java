package com.kt.controller.faq;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.dto.faq.FAQCreateRequest;
import com.kt.service.faq.FAQService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin FAQ", description = "FAQ 관리자용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/faqs")
public class AdminFAQController {
	private final FAQService fAQService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "FAQ 생성")
	public ApiResult<Void> create(@RequestBody @Valid FAQCreateRequest request) throws Exception {
		fAQService.create(request);
		return ApiResult.ok();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "FAQ 삭제")
	public ApiResult<Void> delete(@PathVariable Long id) {
		fAQService.delete(id);
		return ApiResult.ok();
	}

}
