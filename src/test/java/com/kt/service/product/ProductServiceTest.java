package com.kt.service.product;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.kt.domain.category.Category;
import com.kt.domain.product.Product;
import com.kt.domain.product.ProductStatus;
import com.kt.domain.variant.Variant;
import com.kt.domain.variant.VariantType;
import com.kt.dto.product.request.ProductCreateRequest;
import com.kt.dto.product.request.ProductUpdateCategoryRequest;
import com.kt.dto.product.request.ProductUpdateRequest;
import com.kt.dto.product.request.ProductUpdateSoldOutRequest;
import com.kt.dto.product.response.AdminProductListResponse;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.variant.VariantRepository;

import static org.assertj.core.api.Assertions.*;

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

	private static final String TEST_PRODUCT_NAME = "후드집업";
	private static final String TEST_PRODUCT_NAME2 = "슬랙스";
	private static final String TEST_PRODUCT_CATEGORY = "아우터";
	private static final String TEST_PRODUCT_CATEGORY2 = "하의";


	@BeforeEach
	void setUp() {
		variantRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();

		initProducts();
	}

	void initProducts() {
		var outer = new Category(TEST_PRODUCT_CATEGORY);
		var pants = new Category(TEST_PRODUCT_CATEGORY2);
		categoryRepository.saveAllAndFlush(List.of(outer, pants));

		var hoodie = new Product(TEST_PRODUCT_NAME, "오버핏후드집업", 50000L, 100L, outer);
		var slacks = new Product(TEST_PRODUCT_NAME2, "와이드슬랙스", 60000L, 100L, pants);
		productRepository.saveAllAndFlush(List.of(hoodie, slacks));

		variantRepository.saveAllAndFlush(
			List.of(
				new Variant(VariantType.COLOR, "아이보리", hoodie),
				new Variant(VariantType.SIZE, "M", hoodie),
				new Variant(VariantType.COLOR, "블랙", slacks),
				new Variant(VariantType.SIZE, "L", slacks)
			)
		);
	}


	@Test
	void ProductCreateRequest로_상품을_등록할_수_있다() {
		//given
		var category = categoryRepository
			.findByType("아우터")
			.orElseThrow();

		var product = new ProductCreateRequest(
			"새상품",
			"새로운 상품입니다",
			20000L,
			100L,
			category.getId()
		);

		//when
		var productId = productService.create(product);

		//then
		var savedProduct = productRepository.findById(productId).orElseThrow();
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getName()).isEqualTo("새상품");
		assertThat(savedProduct.getCategory().getId()).isEqualTo(category.getId());
	}



	@Test
	void 관리자는_상품목록을_조회할_수_있다() {
		//given
		Pageable pageable =  PageRequest.of(0, 10);

		//when
		var productList = productService.getProductList(pageable);

		//then
		assertThat(productList.getContent()).hasSize(2);
		assertThat(productList.getContent())
			.extracting(
				AdminProductListResponse::name,
				AdminProductListResponse::category
			)
			.containsExactlyInAnyOrder(
				tuple(TEST_PRODUCT_NAME, TEST_PRODUCT_CATEGORY),
				tuple(TEST_PRODUCT_NAME2, TEST_PRODUCT_CATEGORY2)
			);
	}


	@Test
	void 관리자는_productId로_상품의_상세정보를_조회할_수_있다() {
		//given
		var product = productRepository
			.findByName(TEST_PRODUCT_NAME)
			.orElseThrow();

		//when
		var productDetail = productService.getProductDetail(product.getId());

		//then
		assertThat(productDetail).isNotNull();
		assertThat(productDetail.name()).isEqualTo(TEST_PRODUCT_NAME);
	}


	@Test
	void 관리자는_ProductUpdateRequest로_상품의_상세정보를_수정할_수_있다() {
		//given
		var product = productRepository
			.findByName(TEST_PRODUCT_NAME)
			.orElseThrow();

		var request =  new ProductUpdateRequest(
			"수정할이름",
			"수정할 내용",
			20000L,
			50L
		);

		//when
		var updatedProductId = productService.updateProduct(product.getId(), request);

		//then
		var updatedProduct = productRepository.findById(updatedProductId).orElseThrow();
		assertThat(updatedProduct.getName()).isEqualTo("수정할이름");
		assertThat(updatedProduct.getDescription()).isEqualTo("수정할 내용");
		assertThat(updatedProduct.getPrice()).isEqualTo(20000L);
		assertThat(updatedProduct.getStock()).isEqualTo(50L);
	}


	@Test
	void 관리자는_ProductUpdateCategoryRequest로_상품의_카테고리를_수정할_수_있다() {
		//given
		var product = productRepository
			.findByName(TEST_PRODUCT_NAME)
			.orElseThrow();

		var category = categoryRepository
			.findByType(TEST_PRODUCT_CATEGORY2)
			.orElseThrow();

		var request = new ProductUpdateCategoryRequest(
			category.getId()
		);

		//when
		var updatedProductId = productService.updateProductCategory(product.getId(), request);

		//then
		var updatedProduct = productRepository.findById(updatedProductId).orElseThrow();
		assertThat(updatedProduct.getCategory().getId()).isEqualTo(request.categoryId());
		assertThat(updatedProduct.getCategory().getType()).isEqualTo(TEST_PRODUCT_CATEGORY2);
	}


	@Test
	void 관리자는_productId로_상품의_품절상태를_업데이트할_수_있다() {
		//given
		var product = productRepository
			.findByName(TEST_PRODUCT_NAME)
			.orElseThrow();

		//when
		var updatedProductId = productService.updateProductSoldOutWithToggle(product.getId());

		//then
		var updatedProduct = productRepository.findById(updatedProductId).orElseThrow();
		assertThat(updatedProduct.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
	}


	@Test
	void 관리자는_productId로_상품의_상태를_비활성화할_수_있다() {
		//given
		var product = productRepository
			.findByName(TEST_PRODUCT_NAME)
			.orElseThrow();

		//when
		var updatedProductId = productService.updateProductInActive(product.getId());

		//then
		var updatedProduct = productRepository.findById(updatedProductId).orElseThrow();
		assertThat(updatedProduct.getStatus()).isEqualTo(ProductStatus.IN_ACTIVATED);
	}


	@Test
	void 관리자는_productId로_수량에_여유가_있을때_상품의_상태를_활성화할_수_있다() {
		//given
		var product = productRepository
			.findByName(TEST_PRODUCT_NAME)
			.orElseThrow();

		//when
		var updatedProductId = productService.updateProductActive(product.getId());

		//then
		var updatedProduct = productRepository.findById(updatedProductId).orElseThrow();
		assertThat(updatedProduct.getStatus()).isEqualTo(ProductStatus.ACTIVATED);
	}


	@Test
	void 관리자는_ProductUpdateSoldOutRequest로_여러_상품을_품절_처리할_수_있다() {
		//given
		var productIds = productRepository.findAll().stream()
			.map(Product::getId)
			.toList();

		var request = new ProductUpdateSoldOutRequest(productIds);

		//when
		var updatedProductIds = productService.updateProductsSoldOut(request);

		//then
		var updatedProducts = productRepository.findAllById(productIds);

		assertThat(updatedProducts)
			.extracting(Product::getStatus)
			.containsOnly(ProductStatus.SOLD_OUT);
	}
}
