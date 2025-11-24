package com.kt.service.cart;

import com.kt.dto.cart.CartCreateRequest;
import com.kt.repository.cart.CartRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public void create(CartCreateRequest request) {
        //TODO: create 기능 구현
    }

	public void updateQuantity(Long cartId, Integer productCount) {
		//TODO: updateQuantity 기능 구현
	}

	public void deleteCartItem(Long cartId) {
		//TODO: deleteCartItem 기능 구현
	}

	public void clearCart() {
		//TODO: clearCart 기능 구현
	}



}
