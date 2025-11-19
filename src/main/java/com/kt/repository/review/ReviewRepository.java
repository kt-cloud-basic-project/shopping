package com.kt.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.review.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
