package com.kt.service.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.support.ObjectUtils;
import com.kt.common.support.Preconditions;
import com.kt.domain.product.Product;
import com.kt.domain.product.ProductStatus;
import com.kt.dto.product.request.ProductCreateRequest;
import com.kt.dto.product.request.ProductUpdateSoldOutReqeust;
import com.kt.dto.product.response.AdminProductListResponse;
import com.kt.dto.product.response.AdminProductDetailResponse;
import com.kt.dto.product.request.ProductUpdateCategoryRequest;
import com.kt.dto.product.request.ProductUpdateRequest;
import com.kt.dto.product.response.UserProductDetailResponse;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.service.variant.VariantService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final VariantService variantService;

	public void create(ProductCreateRequest request) {
		var category = categoryRepository.findByIdOrThrow(request.categoryId(), ErrorCode.NOT_FOUND_CATEGORY);

		var newProduct = new Product(
			request.name(),
			request.description(),
			request.price(),
			request.stock(),
			category
		);

		productRepository.save(newProduct);
	}


	public Page<AdminProductListResponse> getProductList(Pageable pageable) {
		return productRepository.findAll(pageable)
			.map(AdminProductListResponse::from);
	}


	public AdminProductDetailResponse getProductDetail(Long productId) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		return AdminProductDetailResponse.from(product);
	}


	public void updateProduct(Long productId,  ProductUpdateRequest request) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);

		product.update(
			ObjectUtils.orElse(request.name(), product.getName()),
			ObjectUtils.orElse(request.description(), product.getDescription()),
			ObjectUtils.orElse(request.price(), product.getPrice()),
			ObjectUtils.orElse(request.stock(), product.getStock())
		);
	}


	public void updateProductCategory(Long productId, ProductUpdateCategoryRequest request) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		var updateCategory = categoryRepository.findByIdOrThrow(request.categoryId(), ErrorCode.NOT_FOUND_CATEGORY);

		product.updateCategory(
			ObjectUtils.orElse(updateCategory, product.getCategory())
		);
	}


	public void updateProductSoldOutWithToggle(Long productId) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);

		if (product.getStatus().equals(ProductStatus.SOLD_OUT)) {
			Preconditions.validate(product.getStock() >= 1, ErrorCode.INVALID_PRODUCT_STOCK);
			product.updateActive();
		} else {
			product.updateSoldOut();
		}

		//TODO : cart 결제 가능 여부 비활성화 처리
	}


	public void updateProductInActive(Long productId) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);

		product.updateInActive();
	}


	public void updateProductActive(Long productId) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		Preconditions.validate(product.getStock() >= 1, ErrorCode.INVALID_PRODUCT_STOCK);

		product.updateActive();
	}


	public void updateProductsSoldOut(ProductUpdateSoldOutReqeust request) {

		request.productIds().forEach(productId -> {
			var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);

			product.updateSoldOut();
		});
	}


	public UserProductDetailResponse getProductDetailForUser(Long productId) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		Preconditions.validate(!product.isDeleted(), ErrorCode.DELETED_PRODUCT);

		var variants = variantService.getVariantList(productId);
		return UserProductDetailResponse.from(product, variants);
	}
}
