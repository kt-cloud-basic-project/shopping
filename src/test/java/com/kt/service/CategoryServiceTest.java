package com.kt.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kt.common.exception.ErrorCode;
import com.kt.domain.category.Category;
import com.kt.dto.category.CategoryRequest;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.variant.VariantRepository;
import com.kt.service.category.CategoryService;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CategoryServiceTest {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private VariantRepository variantRepository;

	@BeforeEach
	void setUp() {
		variantRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();

		initCategories();
	}


	void initCategories() {
		var categoryList = List.of(
			new Category("아우터"),
			new Category("상의"),
			new Category("하의")
		);
		categoryRepository.saveAllAndFlush(categoryList);
	}


	@Test
	void type을_입력하여_카테고리를_생성할_수_있다() {

		//given
		CategoryRequest request = new CategoryRequest("신발");

		//when
		categoryService.create(request);

		//then
		var savedCategory = categoryRepository
			.findByType("신발")
			.orElseThrow();

		assertThat(savedCategory.getType()).isEqualTo("신발");
	}

	@Test
	void 카테고리_목록을_조회할_수_있다() {
		//when
		var types = categoryService.getCategoryList();

		//then
		assertThat(types.types()).hasSize(3);
		assertThat(types.types())
			.containsExactlyInAnyOrder("아우터", "상의", "하의");
	}

	@Test
	void type을_입력하여_카테고리_타입을_수정할_수_있다() {
		//given
		var category = categoryRepository
			.findByType("아우터")
			.orElseThrow();

		CategoryRequest request = new CategoryRequest("신발");

		//when
		categoryService.updateCategory(category.getId(), request);

		//then
		var updatedCategory = categoryRepository.findByIdOrThrow(category.getId(), ErrorCode.NOT_FOUND_CATEGORY);
		assertThat(updatedCategory.getType()).isEqualTo("신발");
		assertThat(categoryRepository.findByType("아우터")).isEmpty();
	}

	@Test
	void id로_카테고리를_삭제할_수_있다() {
		//given
		var category = categoryRepository
			.findByType("아우터")
			.orElse(null);

		//when
		categoryService.deleteCategory(category.getId());

		//then
		assertThat(categoryRepository.findById(category.getId())).isEmpty();
	}

}
