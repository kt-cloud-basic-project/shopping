package com.kt.controller.category;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.category.CategoryListResponse;
import com.kt.service.category.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Category", description = "Category 유저용 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController extends SwaggerAssistance {
	private final CategoryService categoryService;

	@GetMapping("")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "카테고리 목록 조회")
	public ApiResult<CategoryListResponse> getCategoryList() {
		return ApiResult.ok(categoryService.getCategoryList());
	}
}
