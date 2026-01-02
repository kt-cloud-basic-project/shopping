package com.kt.repository.cart;

import java.util.List;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.cart.Cart;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepository extends JpaRepository<Cart, Long> {
	default Cart findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	List<Cart> findByUserId(Long userId);

	@EntityGraph(attributePaths = {"user", "product", "user.membership"})
	Page<Cart> findByUserId(Long userId, Pageable pageable);

    //상품을 찜한 유저 이메일만 뽑는 쿼리, jpa는 비효율적이라 사용x
    @Query("""
        select distinct u.email
        from Wishlist w
        join w.user u
        where w.product.id = :productId
          and u.isDeleted = false
          and u.email is not null
    """)
    List<String> findDistinctEmailsByProductId(Long productId);
}
