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
}
