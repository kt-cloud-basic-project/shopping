package com.kt.service.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.support.ObjectUtils;
import com.kt.domain.product.Product;
import com.kt.dto.product.ProductCreateRequest;
import com.kt.dto.product.ProductListResponse;
import com.kt.dto.product.ProductResponse;
import com.kt.dto.product.ProductUpdateCategoryRequest;
import com.kt.dto.product.ProductUpdateRequest;
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
}
