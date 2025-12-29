package com.kt.service.cart;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kt.common.exception.ErrorCode;
import com.kt.common.request.Paging;
import com.kt.common.support.Preconditions;
import com.kt.domain.cart.Cart;
import com.kt.domain.discount.Discount;
import com.kt.domain.discountMembership.DiscountMembership;
import com.kt.domain.discountProduct.DiscountProduct;
import com.kt.domain.product.Product;
import com.kt.dto.cart.CartCreateRequest;
import com.kt.dto.cart.response.CartResponse;
import com.kt.dto.discount.response.DiscountResult;
import com.kt.event.CartItemAddedEvent;
import com.kt.repository.cart.CartRepository;
import com.kt.repository.discountmembership.DiscountMembershipRepository;
import com.kt.repository.discountproduct.DiscountProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.repository.variant.VariantRepository;
import com.kt.service.discount.DiscountCalcService;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final VariantRepository variantRepository;
	private final DiscountCalcService discountCalcService;
	private final ApplicationEventPublisher eventPublisher;

    public Long create(Long userId, CartCreateRequest request) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		var product = productRepository.findByIdOrThrow(request.productId(), ErrorCode.NOT_FOUND_PRODUCT);

		// 장바구니에 담을 상품의 재고가 0보다 큰 지 검증
		Preconditions.validate(product.getStock() > 0, ErrorCode.NOT_ENOUGH_STOCK);

		// 상품의 재고보다 구매할 상품 수량이 적은지 검증
		Preconditions.validate(product.getStock() >= request.productCount(), ErrorCode.NOT_ENOUGH_STOCK);

		// 선택한 옵션이 존재하는지 검증
		Preconditions.validate(variantRepository.existsByIdAndDeletedFalse(request.variantId()), ErrorCode.DELETED_VARIANT);

		var newCart = new Cart(
			request.productCount(),
			request.variantId(),
			user,
			product
		);
		cartRepository.save(newCart);

		eventPublisher.publishEvent(
			new CartItemAddedEvent(userId, product.getId())
		);

		return newCart.getId();
    }

	public Page<CartResponse> getCartList(Long userId, Paging paging) {
		Page<Cart> carts = cartRepository.findByUserId(userId, paging.toPageable());

		if (carts.isEmpty()) {
			return Page.empty(paging.toPageable());
		}

		// 멤버십 할인 조회
		Discount membershipDiscount = discountCalcService.getMembershipDiscount(userId);

		// 상품별 할인 조회
		Map<Long, List<Discount>> productDiscount = discountCalcService.getProductsDiscount(
			carts.getContent().stream()
			.map(cart -> cart.getProduct().getId())
			.distinct()
			.toList());

		return carts.map(cart -> {
			DiscountResult result = discountCalcService.calculate(
				cart.getTotalPrice(),
				membershipDiscount,
				productDiscount.getOrDefault(cart.getProduct().getId(), List.of())
			);

			return CartResponse.from(
				cart,
				result.discountPrice(),
				result.discountedPrice()
			);
		});
	}

	public void updateQuantity(Long cartId, Long userId, Integer productCount) {
		Cart cart = cartRepository.findByIdOrThrow(cartId, ErrorCode.NOT_FOUND_CART);
		Product product = productRepository.findByIdOrThrow(cart.getProduct().getId(), ErrorCode.NOT_FOUND_PRODUCT);

		Preconditions.validate(variantRepository.existsByIdAndDeletedFalse(cart.getVariantId()), ErrorCode.DELETED_VARIANT);

        // 변경할 장바구니가 유저 본인 장바구니인지 확인
		Preconditions.validate(cart.getUser().getId().equals(userId), ErrorCode.NOT_CART_OWNER);

		// 변경할 수량이 상품의 재고보다 적은지 확인
		Preconditions.validate(productCount <= product.getStock(),ErrorCode.NOT_ENOUGH_STOCK);

		cart.updateQuantity(productCount);
	}

	public void deleteCartItem(Long cartId) {
		Cart cart = cartRepository.findByIdOrThrow(cartId, ErrorCode.NOT_FOUND_CART);

		cartRepository.deleteById(cartId);
	}

	public void clearCart(Long userId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		List<Cart> carts = cartRepository.findByUserId(userId);

		cartRepository.deleteAll(carts);
	}
}
