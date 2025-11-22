package com.kt.service.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.ErrorCode;
import com.kt.domain.product.Product;
import com.kt.dto.product.ProductCreateRequest;
import com.kt.dto.product.ProductListResponse;
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
}
