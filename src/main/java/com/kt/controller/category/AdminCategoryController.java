package com.kt.controller.category;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.dto.category.CategoryCreateRequest;
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
public class AdminCategoryController {

	private final CategoryService categoryService;

	@PostMapping("")
	public ApiResult<Void> create(@Valid @RequestBody CategoryCreateRequest request) {
		categoryService.create(request);
		return ApiResult.ok();
	}

	@GetMapping("")
	public ApiResult<CategoryListResponse> getCategoryList() {
		return ApiResult.ok(categoryService.getCategoryList());
	}
}
