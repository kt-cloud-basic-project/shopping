package com.kt.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kt.domain.category.Category;
import com.kt.domain.product.Product;
import com.kt.domain.variant.Variant;
import com.kt.domain.variant.VariantType;
import com.kt.dto.variant.VariantCreateRequest;
import com.kt.dto.variant.VariantListResponse;
import com.kt.dto.variant.VariantUpdateRequest;
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
			"새로운카테고리"
		);
		categoryRepository.saveAndFlush(category);

		var product = new Product(
			"상품이름",
			"상품설명",
			20000L,
			100L,
			category
		);
		productRepository.saveAndFlush(product);

		var variants = List.of(
			new Variant(VariantType.COLOR, "아이보리", product),
			new Variant(VariantType.SIZE, "M", product)
		);
		variantRepository.saveAllAndFlush(variants);
	}


	@Test
	void VariantCreateRequest를_입력하여_상품옵션을_생성_할_수_있다() {
		//given
		var request = List.of(
			new VariantCreateRequest(VariantType.COLOR, "블랙"),
			new VariantCreateRequest(VariantType.SIZE, "L")
		);

		var product = productRepository
			.findByName("상품이름")
			.orElseThrow();

		//when
		List<Long> variantIds = variantService.create(product.getId(), request);

		//then
		assertThat(variantIds).hasSize(request.size());
		variantIds.forEach(variantId -> {
			var savedVariant = variantRepository.findById(variantId).orElseThrow();
			assertThat(savedVariant.getProduct().getId()).isEqualTo(product.getId());
		});
	}


	@Test
	void productId를_입력하여_variant목록을_조회할_수_있다() {
		//given
		var product = productRepository
			.findByName("상품이름")
			.orElseThrow();

		//when
		var variants = variantService.getVariantList(product.getId());

		//then
		assertThat(variants).hasSize(2);
		assertThat(variants)
			.extracting(
				VariantListResponse::type,
				VariantListResponse::detail
			)
			.containsExactlyInAnyOrder(
				tuple(VariantType.COLOR.getDescription(), List.of("아이보리")),
				tuple(VariantType.SIZE.getDescription(), List.of("M"))
			);
	}


	@Test
	void VariantUpdateRequest를_입력하여_variant를_수정할_수_있다() {
		//given
		var product = productRepository
			.findByName("상품이름")
			.orElseThrow();

		var variant = variantRepository
			.findByProductIdAndDetail(product.getId(), "아이보리")
			.orElseThrow();

		var request = new VariantUpdateRequest("그린");

		//when
		variantService.updateVariant(variant.getId(), request);

		//then
		var updatedVariant = variantRepository.findById(variant.getId()).orElseThrow();
		assertThat(updatedVariant.getDetail()).isEqualTo(request.detail());
		assertThat(variantRepository.findByProductIdAndDetail(product.getId(), "아이보리")).isEmpty();
	}
}
