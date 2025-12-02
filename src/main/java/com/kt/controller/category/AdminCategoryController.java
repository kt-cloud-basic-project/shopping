package com.kt.controller.category;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.category.CategoryRequest;
import com.kt.dto.category.CategoryListResponse;
import com.kt.service.category.CategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Category", description = "Category 관리자용 API")
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController extends SwaggerAssistance {

	private final CategoryService categoryService;

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResult<Void> create(@Valid @RequestBody CategoryRequest request) {
		categoryService.create(request);
		return ApiResult.ok();
	}

	@GetMapping("")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<CategoryListResponse> getCategoryList() {
		return ApiResult.ok(categoryService.getCategoryList());
	}

	@PatchMapping("/{categoryId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> update(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequest request) {
		categoryService.updateCategory(categoryId, request);
		return ApiResult.ok();
	}

	@DeleteMapping("{categoryId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> delete(@PathVariable Long categoryId) {
		categoryService.deleteCategory(categoryId);
		return ApiResult.ok();
	}
}
