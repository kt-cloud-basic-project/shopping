package com.kt.repository.cart;

import java.util.List;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.cart.Cart;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
	default Cart findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	List<Cart> findByUserId(Long userId);

	@EntityGraph(attributePaths = {"user", "product", "user.membership"})
	Page<Cart> findByUserId(Long userId, Pageable pageable);
}
