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
import com.kt.dto.product.response.ProductListResponse;
import com.kt.dto.product.response.ProductResponse;
import com.kt.dto.product.request.ProductUpdateCategoryRequest;
import com.kt.dto.product.request.ProductUpdateRequest;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

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


	public Page<ProductListResponse> getProductList(Pageable pageable) {
		return productRepository.findAll(pageable)
			.map(ProductListResponse::from);
	}


	public ProductResponse getProductDetail(Long productId) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		return ProductResponse.from(product);
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


	public void updateProductSoldOut(Long productId) {
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
}
