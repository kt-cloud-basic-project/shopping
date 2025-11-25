package com.kt.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.CustomException;
import com.kt.common.ErrorCode;
import com.kt.domain.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	default Category findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}
}
