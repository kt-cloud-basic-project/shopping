package com.kt.service.cart;

import java.util.List;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.cart.Cart;
import com.kt.dto.cart.CartCreateRequest;
import com.kt.repository.cart.CartRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

		// 장바구니에 담을 상품의 재고가 0일 경우
		if (product.getStock() <= 0) {
			throw new CustomException(ErrorCode.NOT_ENOUGH_STOCK);
		}

		var newCart = new Cart(
			request.productCount(),
			request.productOption(),
			user,
			product
		);
		cartRepository.save(newCart);
    }

	public void updateQuantity(Long cartId, Integer productCount) {
		Cart cart = cartRepository.findByIdOrThrow(cartId, ErrorCode.NOT_FOUND_CART);

		cart.updateQuantity(productCount);
	}

	public void deleteCartItem(Long cartId) {
		Cart cart = cartRepository.findByIdOrThrow(cartId, ErrorCode.NOT_FOUND_CART);
	}

	public void clearCart(Long userId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);
		List<Cart> carts = cartRepository.findByUserId(userId);

		cartRepository.deleteAll(carts);
	}
}
