package com.kt.service.category;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.support.Preconditions;
import com.kt.domain.category.Category;
import com.kt.dto.category.CategoryRequest;
import com.kt.dto.category.CategoryListResponse;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.product.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;

	public void create(CategoryRequest request) {
		//중복된 카테고리명 검증
		Preconditions.validate(!categoryRepository.existsByType(request.type()), ErrorCode.CANNOT_CREATE_CATEGORY);
		categoryRepository.save(new Category(request.type()));
	}


	public CategoryListResponse getCategoryList() {
		var types =  categoryRepository.findAll()
			.stream().map(Category::getType).collect(Collectors.toList());

		return CategoryListResponse.from(types);
	}


	public void updateCategory(Long categoryId, CategoryRequest request) {
		var updatedCategory = categoryRepository.findByIdOrThrow(categoryId, ErrorCode.NOT_FOUND_CATEGORY);

		//중복된 카테고리명 검증
		Preconditions.validate(!categoryRepository.existsByType(request.type()), ErrorCode.CANNOT_CREATE_CATEGORY);

		updatedCategory.update(request.type());
	}


	public void deleteCategory(Long categoryId) {
		categoryRepository.findByIdOrThrow(categoryId, ErrorCode.NOT_FOUND_CATEGORY);

		boolean exists = productRepository.existsByCategoryId(categoryId);
		Preconditions.validate(!exists, ErrorCode.CANNOT_DELETE_CATEGORY);

		categoryRepository.deleteById(categoryId);
	}
}
