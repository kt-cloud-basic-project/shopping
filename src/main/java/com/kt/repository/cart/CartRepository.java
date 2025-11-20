package com.kt.repository.cart;

import com.kt.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
