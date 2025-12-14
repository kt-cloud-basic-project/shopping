package com.kt.controller.product;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.request.Paging;
import com.kt.common.response.ApiResult;
import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.product.request.ProductCreateRequest;
import com.kt.dto.product.request.ProductUpdateSoldOutReqeust;
import com.kt.dto.product.response.AdminProductListResponse;
import com.kt.dto.product.response.AdminProductDetailResponse;
import com.kt.dto.product.request.ProductUpdateCategoryRequest;
import com.kt.dto.product.request.ProductUpdateRequest;
import com.kt.service.product.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Product", description = "Product 관리자용 API")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController extends SwaggerAssistance {
	private final ProductService productService;

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "상품 등록")
	public ApiResult<Void> create(@Valid @RequestBody ProductCreateRequest request) {
		productService.create(request);
		return ApiResult.ok();
	}

	@GetMapping("")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "상품 목록 조회")
	public ApiResult<Page<AdminProductListResponse>> getProductList(Paging paging) {
		Page<AdminProductListResponse> productList = productService.getProductList(paging.toPageable());
		return ApiResult.ok(productList);
	}

	@GetMapping("/{productId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "상품 상세 조회")
	public ApiResult<AdminProductDetailResponse> getProductDetail(@PathVariable("productId") Long productId) {
		var product = productService.getProductDetail(productId);
		return ApiResult.ok(product);
	}

	@PutMapping("/{productId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "상품 정보 수정")
	public ApiResult<Void> update(@PathVariable("productId") Long productId, @Valid @RequestBody ProductUpdateRequest request) {
		productService.updateProduct(productId, request);
		return ApiResult.ok();
	}

	@PutMapping("/{productId}/category")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "상품 카테고리 수정")
	public ApiResult<Void> updateCategory(@PathVariable("productId") Long productId, @Valid @RequestBody
		ProductUpdateCategoryRequest request) {
		productService.updateProductCategory(productId, request);
		return ApiResult.ok();
	}

	@PatchMapping("/{productId}/toggle-sold-out")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "상품 품절 (토글)")
	public ApiResult<Void> soldOutWithToggle(@PathVariable("productId") Long productId) {
		productService.updateProductSoldOutWithToggle(productId);
		return ApiResult.ok();
	}

	@PatchMapping("/{productId}/in-activate")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "상품 비활성화")
	public ApiResult<Void> inActivate(@PathVariable("productId") Long productId) {
		productService.updateProductInActive(productId);
		return ApiResult.ok();
	}

	@PatchMapping("/{productId}/activate")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "상품 활성화")
	public ApiResult<Void> activate(@PathVariable("productId") Long productId) {
		productService.updateProductActive(productId);
		return ApiResult.ok();
	}

	@PatchMapping("/sold-out")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "상품 다중 품절")
	public ApiResult<Void> soldOut(@RequestBody ProductUpdateSoldOutReqeust request) {
		productService.updateProductsSoldOut(request);
		return ApiResult.ok();
	}

	@DeleteMapping("/{productId}")
	@Operation(summary = "상품 삭제")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> deleteProduct(@PathVariable("productId") Long productId) {
		productService.deleteProduct(productId);
		return ApiResult.ok();
	}
}
