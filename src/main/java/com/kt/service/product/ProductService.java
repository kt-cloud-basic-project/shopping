package com.kt.service.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.ErrorCode;
import com.kt.domain.product.Product;
import com.kt.dto.product.ProductCreateRequest;
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
}
