package com.kt.controller.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.dto.product.response.UserProductDetailResponse;
import com.kt.service.product.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product", description = "Product 유저용 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class UserProductController {
	private final ProductService productService;

	@GetMapping("/{productId}")
	public ApiResult<UserProductDetailResponse> getProductDetail(@PathVariable Long productId) {
		return ApiResult.ok(productService.getProductDetailForUser(productId));
	}
}
