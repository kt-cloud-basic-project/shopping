package com.kt.repository.wishlist;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kt.domain.wishlist.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
	boolean existsByUserIdAndProductId(Long userId, Long productId);

	List<Wishlist> findByUserId(Long userId);

	List<Wishlist> findByProductId(Long productId);

	void deleteByUserIdAndProductId(Long userId, Long productId);

	void deleteByUserId(Long userId);

}
