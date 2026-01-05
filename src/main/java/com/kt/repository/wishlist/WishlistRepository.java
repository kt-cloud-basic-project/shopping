package com.kt.repository.wishlist;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.kt.domain.wishlist.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist,Long> {
	boolean existsByUserIdAndProductId(Long userId, Long productId);
	List<Wishlist> findByUserId(Long userId);
	List<Wishlist> findByProductId(Long productId);

	void deleteByUserIdAndProductId(Long userId, Long productId);

	void deleteByUserId(Long userId);

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