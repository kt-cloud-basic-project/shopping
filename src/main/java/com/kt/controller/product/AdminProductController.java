package com.kt.controller.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.request.Paging;
import com.kt.common.response.ApiResult;
import com.kt.dto.product.request.ProductCreateRequest;
import com.kt.dto.product.request.ProductUpdateSoldOutReqeust;
import com.kt.dto.product.response.AdminProductListResponse;
import com.kt.dto.product.response.AdminProductDetailResponse;
import com.kt.dto.product.request.ProductUpdateCategoryRequest;
import com.kt.dto.product.request.ProductUpdateRequest;
import com.kt.service.product.ProductService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
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
	public ApiResult<Page<AdminProductListResponse>> getProductList(Paging paging) {
		Page<AdminProductListResponse> productList = productService.getProductList(paging.toPageable());
		return ApiResult.ok(productList);
	}

	@GetMapping("/{productId}")
	public ApiResult<AdminProductDetailResponse> getProductDetail(@PathVariable("productId") Long productId) {
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
	public ApiResult<Void> soldOutWithToggle(@PathVariable("productId") Long productId) {
		productService.updateProductSoldOutWithToggle(productId);
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

	@PatchMapping("/sold-out")
	public ApiResult<Void> soldOut(@RequestBody ProductUpdateSoldOutReqeust request) {
		productService.updateProductsSoldOut(request);
		return ApiResult.ok();
	}

	@DeleteMapping("/{productId}")
	public ApiResult<Void> deleteProduct(@PathVariable("productId") Long productId) {
		productService.deleteProduct(productId);
		return ApiResult.ok();
	}
}
