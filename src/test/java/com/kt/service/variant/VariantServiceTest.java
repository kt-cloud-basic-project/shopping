package com.kt.service.variant;

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
import com.kt.repository.product.ProductRepository;
import com.kt.repository.variant.VariantRepository;

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

	@Autowired
	private CategoryRepository categoryRepository;

	private static final String TEST_PRODUCT_NAME = "상품이름";
	private static final String TEST_COLOR = "아이보리";
	private static final String TEST_SIZE = "M";


	@BeforeEach
	void setup() {
		variantRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();

		initVariant();
	}

	void initVariant() {
		var category = categoryRepository.saveAndFlush(
			new Category("새로운카테고리")
		);

		var product = productRepository.saveAndFlush(
			new Product(TEST_PRODUCT_NAME, "상품설명", 20000L, 100L, category)
		);

		variantRepository.saveAllAndFlush(
			List.of(
				new Variant(VariantType.COLOR, TEST_COLOR, product),
				new Variant(VariantType.SIZE, TEST_SIZE, product)
			)
		);
	}


	@Test
	void VariantCreateRequest를_입력하여_상품옵션을_생성_할_수_있다() {
		//given
		var request = List.of(
			new VariantCreateRequest(VariantType.COLOR, "블랙"),
			new VariantCreateRequest(VariantType.SIZE, "L")
		);

		var product = productRepository
			.findByName(TEST_PRODUCT_NAME)
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
			.findByName(TEST_PRODUCT_NAME)
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
				tuple(VariantType.COLOR.getDescription(), List.of(TEST_COLOR)),
				tuple(VariantType.SIZE.getDescription(), List.of(TEST_SIZE))
			);
	}


	@Test
	void VariantUpdateRequest를_입력하여_variant를_수정할_수_있다() {
		//given
		var product = productRepository
			.findByName(TEST_PRODUCT_NAME)
			.orElseThrow();

		var variant = variantRepository
			.findByProductIdAndDetail(product.getId(), TEST_COLOR)
			.orElseThrow();

		var request = new VariantUpdateRequest("그린");

		//when
		variantService.updateVariant(variant.getId(), request);

		//then
		var updatedVariant = variantRepository.findById(variant.getId()).orElseThrow();
		assertThat(updatedVariant.getDetail()).isEqualTo(request.detail());
		assertThat(variantRepository.findByProductIdAndDetail(product.getId(), TEST_COLOR)).isEmpty();
	}


	@Test
	void variantId를_입력하여_variant를_삭제할_수_있다() {
		//given
		var product = productRepository
			.findByName(TEST_PRODUCT_NAME)
			.orElseThrow();

		var variant = variantRepository
			.findByProductIdAndDetail(product.getId(), TEST_COLOR)
			.orElseThrow();

		//when
		variantService.deleteVariant(variant.getId());

		//then
		var deletedVariant = variantRepository.findById(variant.getId()).orElseThrow();
		assertThat(deletedVariant.isDeleted()).isTrue();
		assertThat(variantRepository.countVariantByDeletedFalse())
			.isEqualTo(variantRepository.findAll().size() - 1);
	}
}
