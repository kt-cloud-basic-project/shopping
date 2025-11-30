package com.kt.repository.variant;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.variant.Variant;

public interface VariantRepository extends JpaRepository<Variant, Long> {

	default Variant findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	List<Variant> findByProductIdAndDeletedFalse(Long productId);

	boolean existsByIdAndDeletedFalse(Long variantId);

	Optional<Variant> findByIdAndDeletedFalse(Long variantId);

	default Variant findByIdAndDeletedFalseOrThrow(Long variantId, ErrorCode errorCode) {
		return findByIdAndDeletedFalse(variantId).orElseThrow(() -> new CustomException(errorCode));
	}
}
