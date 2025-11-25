package com.kt.controller.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.dto.product.request.ProductCreateRequest;
import com.kt.dto.product.response.ProductListResponse;
import com.kt.dto.product.response.ProductResponse;
import com.kt.dto.product.request.ProductUpdateCategoryRequest;
import com.kt.dto.product.request.ProductUpdateRequest;
import com.kt.service.product.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product", description = "Product 관리자용 API")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
	private final ProductService productService;

	@PostMapping("")
	public ApiResult<Void> create(@Valid @RequestBody ProductCreateRequest request) {
		productService.create(request);
		return ApiResult.ok();
	}

	@GetMapping("")
	public ApiResult<Page<ProductListResponse>> getProductList(Pageable pageable) {
		Page<ProductListResponse> productList = productService.getProductList(pageable);
		return ApiResult.ok(productList);
	}

	@GetMapping("/{productId}")
	public ApiResult<ProductResponse> getProductDetail(@PathVariable("productId") Long productId) {
		var product = productService.getProductDetail(productId);
		return ApiResult.ok(product);
	}

	@PutMapping("/{productId}")
	public ApiResult<Void> update(@PathVariable("productId") Long productId, @Valid @RequestBody ProductUpdateRequest request) {
		productService.updateProduct(productId, request);
		return ApiResult.ok();
	}

	@PutMapping("/{productId}/category")
	public ApiResult<Void> updateCategory(@PathVariable("productId") Long productId, @Valid @RequestBody
		ProductUpdateCategoryRequest request) {
		productService.updateProductCategory(productId, request);
		return ApiResult.ok();
	}

	@PatchMapping("/{productId}/toggle-sold-out")
	public ApiResult<Void> soldOut(@PathVariable("productId") Long productId) {
		productService.updateProductSoldOut(productId);
		return ApiResult.ok();
	}

	@PatchMapping("/{productId}/in-activate")
	public ApiResult<Void> inActivate(@PathVariable("productId") Long productId) {
		productService.updateProductInActive(productId);
		return ApiResult.ok();
	}

	@PatchMapping("/{productId}/activate")
	public ApiResult<Void> activate(@PathVariable("productId") Long productId) {
		productService.updateProductActive(productId);
		return ApiResult.ok();
	}
}
