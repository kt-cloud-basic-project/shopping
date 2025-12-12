package com.kt.repository.category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	default Category findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	boolean existsByType(String type);

	Optional<Category> findByType(String type);
}
