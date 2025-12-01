package com.kt.service.cart;

import java.util.List;
import java.util.Optional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.request.Paging;
import com.kt.common.support.Preconditions;
import com.kt.domain.cart.Cart;
import com.kt.domain.discount.Discount;
import com.kt.domain.membership.Membership;
import com.kt.domain.product.Product;
import com.kt.domain.user.User;
import com.kt.dto.cart.CartCreateRequest;
import com.kt.dto.cart.response.CartResponse;
import com.kt.repository.cart.CartRepository;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.repository.variant.VariantRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final DiscountRepository discountRepository;
	private final VariantRepository variantRepository;

    public void create(Long userId, CartCreateRequest request) {
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
    }

	public Page<CartResponse> getCartList(Long userId, Paging paging) {
		Page<Cart> carts = cartRepository.findByUserId(userId, paging.toPageable());

		// 빈 카트 반환(빈 카트 일 시 User 정보를 가져올 수 없기 때문)
		if (carts.isEmpty()) {
			return Page.empty(paging.toPageable());
		}

		// carts안에 있는 user는 모두 동일한 user이므로 첫번째 것만 가져와서 membershipId를 얻음
		User user = carts.getContent().getFirst().getUser();

		// 할인은 있을 수도 없을 수도(optional)
		Discount discount = Optional.ofNullable(user.getMembership())
			.map(Membership::getId)
			.flatMap(discountRepository::findByMembershipId)
			.orElse(null);

		return carts.map(cart -> CartResponse.from(
			cart,
			discount != null ? discount.calcDiscountAmount(cart.getProduct().getPrice()) : 0L,
			discount != null ? discount.calcDiscountFinalPrice(cart.getProduct().getPrice()) : cart.getProduct().getPrice()
		));
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
