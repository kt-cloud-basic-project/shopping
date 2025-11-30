package com.kt.controller.product;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.request.Paging;
import com.kt.common.response.ApiResult;
import com.kt.dto.product.response.UserProductDetailResponse;
import com.kt.dto.product.response.UserProductListResponse;
import com.kt.security.CustomUserDetails;
import com.kt.service.product.ProductService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product", description = "Product 유저용 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class UserProductController {
	private final ProductService productService;

	@GetMapping("")
	public ApiResult<Page<UserProductListResponse>> getProductList(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) Long categoryId,
		@Valid @Parameter(hidden = true) Paging paging
	) {
		return ApiResult.ok(productService.getProductListForUser(currentUser, keyword, categoryId, paging.toPageable()));
	}

	@GetMapping("/{productId}")
	public ApiResult<UserProductDetailResponse> getProductDetail(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long productId
	) {
		return ApiResult.ok(productService.getProductDetailForUser(currentUser, productId));
	}
}
