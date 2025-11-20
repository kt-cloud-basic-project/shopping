package com.kt.repository.review;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import com.kt.common.CustomException;
import com.kt.common.ErrorCode;
import com.kt.domain.review.Review;
import com.kt.domain.user.User;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	default Review findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	// 이게 맞을까요? 기본 CRUD외에는 Querydsl 또는 JPQL 선택해야 할 것 같습니다
	// Page<Review> findByUserIdAndIsDeletedFalseOrderByIdDesc(Long userId, Pageable pageable);

	// Page<Review> findByProductProductIdAndIsDeletedFalseOrderByCreatedAtDesc(Long productId);

	@Query("""
	SELECT r FROM Review r
	WHERE r.user.id = :userId AND r.isDeleted = false
	ORDER BY r.id DESC
""")
	Page<Review> findReviewsByUserId(@Param("userId") Long userId, Pageable pageable);

	/*@Query("""
	SELECT r FROM Review r
	JOIN r.orderProduct op
	WHERE op.product.id = :productId AND r.isDeleted = false
	ORDER BY r.id DESC
""")
	Page<Review> findReviewsByProductId(@Param("productId") Long productId, Pageable pageable);*/
}
