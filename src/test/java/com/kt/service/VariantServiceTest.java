package com.kt.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kt.domain.category.Category;
import com.kt.domain.product.Product;
import com.kt.domain.variant.VariantType;
import com.kt.dto.variant.VariantCreateRequest;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.orderproduct.OrderProductRepositoryCustom;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.variant.VariantRepository;
import com.kt.service.variant.VariantService;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VariantServiceTest {

	@Autowired
	private VariantService variantService;

	@Autowired
	private VariantRepository variantRepository;

	@Autowired
	private ProductRepository productRepository;

	// @Autowired
	// private OrderProductRepository orderProductRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	// @Autowired
	// private OrderProductRepositoryCustom orderProductRepositoryCustom;

	@BeforeEach
	void setup() {
		variantRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();
		// orderProductRepository.deleteAll();

		initVariant();
	}

	void initVariant() {
		var category = new Category(
			"새로운 카테고리"
		);

		categoryRepository.saveAndFlush(category);

		var product = new Product(
			"상품 이름",
			"상품 설명",
			20000L,
			100L,
			category
		);

		productRepository.saveAndFlush(product);
	}


	@Test
	void VariantCreateRequest를_입력하여_상품옵션을_생성_할_수_있다() {
		//given
		var request = List.of(
			new VariantCreateRequest(VariantType.COLOR, "블랙"),
			new VariantCreateRequest(VariantType.SIZE, "L")
		);

		var product = productRepository
			.findByName("상품 이름")
			.orElseThrow();

		//when
		List<Long> variantIds = variantService.create(product.getId(), request);

		//then
		assertThat(variantIds).hasSize(2);
		variantIds.forEach(variantId -> {
			var savedVariant = variantRepository.findById(variantId).orElseThrow();
			assertThat(savedVariant.getProduct().getId()).isEqualTo(product.getId());
		});
	}
}
