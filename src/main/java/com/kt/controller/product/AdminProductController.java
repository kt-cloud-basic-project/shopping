package com.kt.controller.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.ApiResult;
import com.kt.dto.product.ProductCreateRequest;
import com.kt.dto.product.ProductListResponse;
import com.kt.service.product.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product", description = "Product 관리자용 API")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
	private final ProductService productService;

	@PostMapping("")
	public ApiResult<Void> create(@RequestBody ProductCreateRequest request) {
		productService.create(request);
		return ApiResult.ok();
	}

	@GetMapping("")
	public ApiResult<Page<ProductListResponse>> getProductList(Pageable pageable) {
		Page<ProductListResponse> productList = productService.getProductList(pageable);
		return ApiResult.ok(productList);
	}
}
