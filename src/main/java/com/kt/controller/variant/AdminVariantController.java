package com.kt.controller.variant;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.dto.variant.VariantCreateRequest;
import com.kt.dto.variant.VariantListResponse;
import com.kt.dto.variant.VariantUpdateRequest;
import com.kt.service.variant.VariantService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Variant", description = "Variant 관리자용 API")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminVariantController {
	private final VariantService variantService;

	@PostMapping("/{productId}/variants")
	public ApiResult<Void> create(@PathVariable Long productId, @Valid @RequestBody List<VariantCreateRequest> requests) {
		variantService.create(productId, requests);
		return ApiResult.ok();
	}

	@GetMapping("/{productId}/variants")
	public ApiResult<List<VariantListResponse>> getVariantList(@PathVariable Long productId) {
		return ApiResult.ok(variantService.getVariantList(productId));
	}

	@PatchMapping("/variants/{variantId}")
	public ApiResult<Void> update(@PathVariable Long variantId, @Valid @RequestBody VariantUpdateRequest request) {
		variantService.updateVariant(variantId, request);
		return ApiResult.ok();
	}

	@DeleteMapping("/variants/{variantId}")
	public ApiResult<Void> delete(@PathVariable Long variantId) {
		variantService.deleteVariant(variantId);
		return ApiResult.ok();
	}
}
