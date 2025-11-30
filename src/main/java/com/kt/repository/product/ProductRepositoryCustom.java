package com.kt.repository.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.domain.product.Product;

public interface ProductRepositoryCustom {
	Page<Product> search(String keyword, Long categoryId, Pageable pageable);
}
