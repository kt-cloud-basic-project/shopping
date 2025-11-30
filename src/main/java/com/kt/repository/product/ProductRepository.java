package com.kt.repository.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	default Product findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	List<Product> findByCategoryId(Long categoryId);

	boolean existsByCategoryId(Long categoryId);
}
