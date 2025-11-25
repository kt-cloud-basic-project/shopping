package com.kt.controller.category;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.dto.category.CategoryCreateRequest;
import com.kt.service.category.CategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Category", description = "Category 관리자용 API")
@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping("")
	public ApiResult<Void> create(@Valid @RequestBody CategoryCreateRequest request) {
		categoryService.create(request);
		return ApiResult.ok();
	}
}
