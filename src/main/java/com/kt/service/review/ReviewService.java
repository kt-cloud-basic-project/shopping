package com.kt.service.review;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.ErrorCode;
import com.kt.domain.review.Review;
import com.kt.dto.review.ReviewCreateReqeust;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;

	public void create(Long productId, ReviewCreateReqeust reqeust) {
		var user = userRepository.findByIdOrThrow(2L, ErrorCode.NOT_FOUND_USER);

		//TODO 프로덕트 검증
		//productId

		Review review = new Review(
			user,
			reqeust.title(),
			reqeust.description(),
			reqeust.star()
		);

		reviewRepository.save(review);

	}
}
