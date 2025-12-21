package com.kt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kt.domain.wishlist.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist,Long> {
		boolean existsByUserIdAndProductId(Long userId, Long productId);

	// 할인 알림용 (특정 상품 찜한 사용자들 조회)
	//List<Wishlist> findByProductId(Long productId);
}
