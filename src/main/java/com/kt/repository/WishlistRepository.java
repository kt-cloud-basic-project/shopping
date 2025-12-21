package com.kt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.domain.wishlist.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist,Long> {
		boolean existsByUserIdAndProductId(Long userId, Long productId);
}
