package com.kt.service.product;

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
import com.kt.dto.product.request.ProductCreateRequest;
import com.kt.dto.product.request.ProductUpdateCategoryRequest;
import com.kt.dto.product.request.ProductUpdateRequest;
import com.kt.dto.product.request.ProductUpdateSoldOutRequest;
import com.kt.dto.product.response.AdminProductListResponse;
import com.kt.dto.product.response.UserProductListResponse;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.util.UserTestSupport;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductServiceTest extends UserTestSupport {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	private Category category1;
	private Category category2;
	private Category category3;

	private Product product1;
	private Product product2;
	private Product product3;


	@BeforeEach
	void setUp() {
		productRepository.deleteAll();
		categoryRepository.deleteAll();

		initProducts();
	}

	void initProducts() {
		initCategories();

		product1 = productRepository.saveAndFlush(
			new Product("후드집업",
				"오버핏후드집업",
				50000L,
				100L,
				category1)
		);

		product2 = productRepository.saveAndFlush(
			new Product("슬랙스",
				"와이드슬랙스",
				60000L,
				100L,
				category2)
		);

		product3 = productRepository.saveAndFlush(
			new Product("후드티셔츠",
				"오버핏후드티셔츠",
				50000L,
				100L,
				category3)
		);
	}

	void initCategories() {
		category1 = categoryRepository.saveAndFlush(new Category("아우터"));
		category2 = categoryRepository.saveAndFlush(new Category("하의"));
		category3 = categoryRepository.saveAndFlush(new Category("상의"));
	}

	@Test
	void ProductCreateRequest로_상품을_등록할_수_있다() {
		//given
		var product = new ProductCreateRequest(
			"새상품",
			"새로운 상품입니다",
			20000L,
			100L,
			category1.getId()
		);

		//when
		var productId = productService.create(product);

		//then
		var savedProduct = productRepository.findById(productId).orElseThrow();
		assertThat(savedProduct).isNotNull();
		assertThat(savedProduct.getName()).isEqualTo("새상품");
		assertThat(savedProduct.getCategory().getId()).isEqualTo(category1.getId());
	}



	@Test
	void 관리자는_상품목록을_조회할_수_있다() {
		//given
		Pageable pageable =  PageRequest.of(0, 5);

		//when
		var productList = productService.getProductList(pageable);

		//then
		assertThat(productList.getContent()).hasSize(3);
		assertThat(productList.getContent())
			.extracting(
				AdminProductListResponse::name,
				AdminProductListResponse::category
			)
			.containsExactlyInAnyOrder(
				tuple(product1.getName(), category1.getType()),
				tuple(product2.getName(), category2.getType()),
				tuple(product3.getName(), category3.getType())
			);
	}


	@Test
	void 관리자는_productId로_상품의_상세정보를_조회할_수_있다() {
		//when
		var productDetail = productService.getProductDetail(product1.getId());

		//then
		assertThat(productDetail).isNotNull();
		assertThat(productDetail.name()).isEqualTo(product1.getName());
	}


	@Test
	void 관리자는_ProductUpdateRequest로_상품의_상세정보를_수정할_수_있다() {
		//given
		var request =  new ProductUpdateRequest(
			"수정할이름",
			"수정할 내용",
			20000L,
			50L
		);

		//when
		var updatedProductId = productService.updateProduct(product1.getId(), request);

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
		var request = new ProductUpdateCategoryRequest(
			category2.getId()
		);

		//when
		var updatedProductId = productService.updateProductCategory(product1.getId(), request);

		//then
		var updatedProduct = productRepository.findById(updatedProductId).orElseThrow();
		assertThat(updatedProduct.getCategory().getId()).isEqualTo(request.categoryId());
		assertThat(updatedProduct.getCategory().getType()).isEqualTo(category2.getType());
	}


	@Test
	void 관리자는_productId로_상품의_품절상태를_업데이트할_수_있다() {
		//when
		var updatedProductId = productService.updateProductSoldOutWithToggle(product1.getId());

		//then
		var updatedProduct = productRepository.findById(updatedProductId).orElseThrow();
		assertThat(updatedProduct.getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
	}


	@Test
	void 관리자는_productId로_상품의_상태를_비활성화할_수_있다() {
		//when
		var updatedProductId = productService.updateProductInActive(product1.getId());

		//then
		var updatedProduct = productRepository.findById(updatedProductId).orElseThrow();
		assertThat(updatedProduct.getStatus()).isEqualTo(ProductStatus.IN_ACTIVATED);
	}


	@Test
	void 관리자는_productId로_수량에_여유가_있을때_상품의_상태를_활성화할_수_있다() {
		//when
		var updatedProductId = productService.updateProductActive(product1.getId());

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
		var updatedProducts = productRepository.findAllById(updatedProductIds);

		assertThat(updatedProducts)
			.extracting(Product::getStatus)
			.containsOnly(ProductStatus.SOLD_OUT);
	}


	@Test
	void 관리자는_productId로_상품을_삭제할_수_있다() {
		//when
		var deletedProductId = productService.deleteProduct(product1.getId());

		//then
		var deletedProduct = productRepository.findById(deletedProductId).orElseThrow();
		assertThat(deletedProduct.getStatus()).isEqualTo(ProductStatus.DELETED);
	}

	@Test
	void 사용자는_상품목록을_조회할_수_있다() {
		//given
		Pageable pageable =  PageRequest.of(0, 5);

		//when
		var productList = productService.getProductListForUser(customUserDetails, null, null, pageable);

		//then
		assertThat(productList.getContent()).hasSize(3);
		assertThat(productList.getContent())
			.extracting(
				UserProductListResponse::id,
				UserProductListResponse::category
			)
			.containsExactlyInAnyOrder(
				tuple(product1.getId(), category1.getType()),
				tuple(product2.getId(), category2.getType()),
				tuple(product3.getId(), category3.getType())
			);
	}


	@Test
	void 사용자는_keyword로_상품목록을_조회할_수_있다() {
		//given
		var keyword = "후드";
		Pageable pageable =  PageRequest.of(0, 5);

		//when
		var productList = productService.getProductListForUser(customUserDetails, keyword, null, pageable);

		//then
		assertThat(productList.getContent()).hasSize(2);
		assertThat(productList.getContent())
			.extracting(
				UserProductListResponse::id,
				UserProductListResponse::category
			)
			.containsExactlyInAnyOrder(
				tuple(product1.getId(), category1.getType()),
				tuple(product3.getId(), category3.getType())
			);
	}


	@Test
	void 사용자는_categoryId로_상품목록을_조회할_수_있다() {
		//given
		var categoryId = category1.getId();
		Pageable pageable =  PageRequest.of(0, 5);

		//when
		var productList = productService.getProductListForUser(customUserDetails, null, categoryId, pageable);

		//then
		assertThat(productList.getContent()).hasSize(1);
		assertThat(productList.getContent())
			.extracting(
				UserProductListResponse::id,
				UserProductListResponse::category
			)
			.containsExactlyInAnyOrder(
				tuple(product1.getId(), category1.getType())
			);
	}


	@Test
	void 사용자는_keyword와_categoryId로_상품목록을_조회할_수_있다() {
		//given
		var keyword = "후드";
		var categoryId = category1.getId();
		Pageable pageable =  PageRequest.of(0, 5);

		//when
		var productList = productService.getProductListForUser(customUserDetails, keyword, categoryId, pageable);

		//then
		assertThat(productList.getContent()).hasSize(1);
		assertThat(productList.getContent())
			.extracting(
				UserProductListResponse::id,
				UserProductListResponse::category
			)
			.containsExactlyInAnyOrder(
				tuple(product1.getId(), category1.getType())
			);
	}


	@Test
	void 사용자는_productId로_상품의_상세정보를_조회할_수_있다() {
		//when
		var product = productService.getProductDetailForUser(customUserDetails, product1.getId());

		//then
		assertThat(product).isNotNull();
		assertThat(product.name()).isEqualTo(product1.getName());
		assertThat(product.category()).isEqualTo(product1.getCategory().getType());
		assertThat(product.status()).isEqualTo(ProductStatus.ACTIVATED.getDescription());
	}
}
