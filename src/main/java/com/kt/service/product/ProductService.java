package com.kt.service.product;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.support.ObjectUtils;
import com.kt.common.support.Preconditions;
import com.kt.domain.discount.Discount;
import com.kt.domain.membership.Membership;
import com.kt.domain.product.Product;
import com.kt.domain.product.ProductStatus;
import com.kt.dto.product.request.ProductCreateRequest;
import com.kt.dto.product.request.ProductUpdateSoldOutRequest;
import com.kt.dto.product.response.AdminProductListResponse;
import com.kt.dto.product.response.AdminProductDetailResponse;
import com.kt.dto.product.request.ProductUpdateCategoryRequest;
import com.kt.dto.product.request.ProductUpdateRequest;
import com.kt.dto.product.response.UserProductDetailResponse;
import com.kt.dto.product.response.UserProductListResponse;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.orderproduct.OrderProductRepositoryCustom;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.product.ProductRepositoryCustom;
import com.kt.repository.user.UserRepository;
import com.kt.security.CustomUserDetails;
import com.kt.service.variant.VariantService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final VariantService variantService;
	private final ProductRepositoryCustom productRepositoryCustom;
	private final OrderProductRepositoryCustom orderProductRepositoryCustom;
	private final UserRepository userRepository;
	private final DiscountRepository discountRepository;

	public Long create(ProductCreateRequest request) {
		var category = categoryRepository.findByIdOrThrow(request.categoryId(), ErrorCode.NOT_FOUND_CATEGORY);

		var newProduct = new Product(
			request.name(),
			request.description(),
			request.price(),
			request.stock(),
			category
		);

		return productRepository.save(newProduct).getId();
	}


	public Page<AdminProductListResponse> getProductList(Pageable pageable) {
		return productRepository.findAll(pageable)
			.map(AdminProductListResponse::from);
	}


	public AdminProductDetailResponse getProductDetail(Long productId) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		return AdminProductDetailResponse.from(product);
	}


	public Long updateProduct(Long productId,  ProductUpdateRequest request) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);

		product.update(
			ObjectUtils.orElse(request.name(), product.getName()),
			ObjectUtils.orElse(request.description(), product.getDescription()),
			ObjectUtils.orElse(request.price(), product.getPrice()),
			ObjectUtils.orElse(request.stock(), product.getStock())
		);

		return product.getId();
	}


	public Long updateProductCategory(Long productId, ProductUpdateCategoryRequest request) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		var updateCategory = categoryRepository.findByIdOrThrow(request.categoryId(), ErrorCode.NOT_FOUND_CATEGORY);

		product.updateCategory(
			ObjectUtils.orElse(updateCategory, product.getCategory())
		);

		return product.getId();
	}


	public Long updateProductSoldOutWithToggle(Long productId) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		Preconditions.validate(!product.getStatus().equals(ProductStatus.DELETED), ErrorCode.DELETED_PRODUCT);

		if (product.getStatus().equals(ProductStatus.SOLD_OUT)) {
			Preconditions.validate(product.getStock() >= 1, ErrorCode.INVALID_PRODUCT_STOCK);
			product.updateActive();
		} else {
			product.updateSoldOut();
		}

		return product.getId();
	}


	public Long updateProductInActive(Long productId) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		Preconditions.validate(!product.getStatus().equals(ProductStatus.DELETED), ErrorCode.DELETED_PRODUCT);

		product.updateInActive();

		return product.getId();
	}


	public Long updateProductActive(Long productId) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		Preconditions.validate(product.getStock() >= 1, ErrorCode.INVALID_PRODUCT_STOCK);
		Preconditions.validate(!product.getStatus().equals(ProductStatus.DELETED), ErrorCode.DELETED_PRODUCT);

		product.updateActive();

		return product.getId();
	}


	public List<Long> updateProductsSoldOut(ProductUpdateSoldOutRequest request) {

		request.productIds().forEach(productId -> {
			var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
			Preconditions.validate(!product.getStatus().equals(ProductStatus.DELETED), ErrorCode.DELETED_PRODUCT);

			product.updateSoldOut();
		});

		return request.productIds();
	}


	public Page<UserProductListResponse> getProductListForUser(CustomUserDetails currentUser, String keyword, Long categoryId, Pageable pageable) {
		Page<Product> products = productRepositoryCustom.search(keyword, categoryId, pageable);

		if(products.isEmpty()) {
			return Page.empty(pageable);
		}

		// 로그인 유저는 할인 적용
		Discount discount = getDiscountForUser(currentUser);

		return products.map(product -> UserProductListResponse.from(
			product,
			discount != null ? discount.calcDiscountAmount(product.getPrice()) : 0L,
			discount != null ? discount.calcDiscountFinalPrice(product.getPrice()) : product.getPrice()
		));
	}

	public UserProductDetailResponse getProductDetailForUser(CustomUserDetails currentUser, Long productId) {
		var product = productRepository.findByIdAndDeletedFalseOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		Preconditions.validate(!product.isDeleted(), ErrorCode.DELETED_PRODUCT);

		var variants = variantService.getVariantList(productId);

		// 로그인 유저는 할인 적용
		Discount discount = getDiscountForUser(currentUser);
		final Long discountAmount = discount != null ? discount.calcDiscountAmount(product.getPrice()) : 0L;
		final Long discountedPrice = discount != null ? discount.calcDiscountFinalPrice(product.getPrice()) : product.getPrice();

		return UserProductDetailResponse.from(product, variants, discountAmount, discountedPrice);

	}

	private Discount getDiscountForUser(CustomUserDetails currentUser) {
		if(currentUser == null) {
			return null;
		}

		var user = userRepository.findByIdOrThrow(currentUser.getId(), ErrorCode.NOT_FOUND_USER);

		return Optional.ofNullable(user.getMembership())
			.map(Membership::getId)
			.flatMap(discountRepository::findByMembershipId)
			.orElse(null);
	}


	public Long deleteProduct(Long productId) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		Preconditions.validate(!orderProductRepositoryCustom.hasInvalidStatusWithProductId(productId), ErrorCode.CANNOT_DELETE_PRODUCT);

		product.delete();
		product.getVariants().forEach(variant -> {
			variantService.deleteVariant(variant.getId());
		});

		return product.getId();
	}


}
