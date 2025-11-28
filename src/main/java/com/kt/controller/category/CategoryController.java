package com.kt.controller.category;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.dto.category.CategoryListResponse;
import com.kt.service.category.CategoryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Category", description = "Category 유저용 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	@GetMapping("")
	public ApiResult<CategoryListResponse> getCategoryList() {
		return ApiResult.ok(categoryService.getCategoryList());
	}
}
