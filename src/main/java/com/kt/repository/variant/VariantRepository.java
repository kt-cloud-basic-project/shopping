package com.kt.repository.variant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.variant.Variant;

public interface VariantRepository extends JpaRepository<Variant, Long> {

	default Variant findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orEalseThrow(() -> new CustomException(errorCode));
	}

	List<Variant> findByProductId(Long productId);
}
