package com.kt.repository.product;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	default Product findByIdOrThrow(Long productId, ErrorCode errorCode) {
		return findById(productId).orElseThrow(() -> new CustomException(errorCode));
	}

	boolean existsByCategoryId(Long categoryId);

	Optional<Product> findByIdAndDeletedFalse(Long id);

	default Product findByIdAndDeletedFalseOrThrow(Long productId, ErrorCode errorCode) {
		return findByIdAndDeletedFalse(productId).orElseThrow(() -> new CustomException(errorCode));
	}

}
