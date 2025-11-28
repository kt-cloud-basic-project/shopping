package com.kt.service.category;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.category.Category;
import com.kt.dto.category.CategoryCreateRequest;
import com.kt.dto.category.CategoryListResponse;
import com.kt.repository.category.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;

	public void create(CategoryCreateRequest request) {
		categoryRepository.save(new Category(request.type()));
	}

	public CategoryListResponse getCategoryList() {
		var types =  categoryRepository.findAll()
			.stream().map(Category::getType).collect(Collectors.toList());

		return CategoryListResponse.from(types);
	}
}
