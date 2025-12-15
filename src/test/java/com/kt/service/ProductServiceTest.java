package com.kt.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kt.domain.category.Category;
import com.kt.domain.product.Product;
import com.kt.domain.variant.Variant;
import com.kt.domain.variant.VariantType;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.variant.VariantRepository;
import com.kt.service.product.ProductService;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductServiceTest {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private VariantRepository variantRepository;

	@BeforeEach
	void setUp() {
		variantRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();
	}

	void initProducts() {
		var outer = new Category("아우터");
		var pants = new Category("하의");
		categoryRepository.saveAllAndFlush(List.of(outer, pants));

		var products = productRepository.saveAllAndFlush(
			List.of(
				new Product("후드집업", "오버핏후드집업", 50000L, 100L, outer),
				new Product("슬랙스", "와이드슬랙스", 70000L, 100L, pants)
			)
		);
	}
}
