package com.kt.service.cart;

import java.util.List;

import com.kt.common.exception.ErrorCode;
import com.kt.common.request.Paging;
import com.kt.common.support.Preconditions;
import com.kt.domain.cart.Cart;
import com.kt.domain.product.Product;
import com.kt.dto.cart.CartCreateRequest;
import com.kt.dto.cart.response.CartResponse;
import com.kt.repository.cart.CartRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;

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

    public void create(Long userId, CartCreateRequest request) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		var product = productRepository.findByIdOrThrow(request.productId(), ErrorCode.NOT_FOUND_PRODUCT);

		// 장바구니에 담을 상품의 재고가 0보다 큰 지 검증
		Preconditions.validate(product.getStock() > 0, ErrorCode.NOT_ENOUGH_STOCK);

		// 상품의 재고보다 구매할 상품 수량이 적은지 검증
		Preconditions.validate(product.getStock() >= request.productCount(), ErrorCode.NOT_ENOUGH_STOCK);

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

		return carts.map(CartResponse::from);
	}
	//TODO: 유저가 장바구니 여러개 가지고 있을시 N+1 문제 발생 > 추후 수정

	public void updateQuantity(Long cartId, Integer productCount) {
		Cart cart = cartRepository.findByIdOrThrow(cartId, ErrorCode.NOT_FOUND_CART);
		Product product = productRepository.findByIdOrThrow(cart.getProduct().getId(), ErrorCode.NOT_FOUND_PRODUCT);

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
