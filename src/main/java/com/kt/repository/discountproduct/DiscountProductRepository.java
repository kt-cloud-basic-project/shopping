package com.kt.repository.discountproduct;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.discountProduct.DiscountProduct;

public interface DiscountProductRepository extends JpaRepository<DiscountProduct, Long> {

	default DiscountProduct findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	boolean existsByProductId(Long id);

	void deleteByDiscountId(Long productId);
}
