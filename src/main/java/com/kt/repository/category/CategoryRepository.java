package com.kt.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
