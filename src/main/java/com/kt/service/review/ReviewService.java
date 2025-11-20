package com.kt.service.review;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.ErrorCode;
import com.kt.common.Preconditions;
import com.kt.domain.review.Review;
import com.kt.dto.review.ReviewCreateRequest;
import com.kt.dto.review.ReviewListResponse;
import com.kt.dto.review.ReviewUpdateRequest;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	//private final OrderProductRepository orderProductRepository;

	public void create(Long productId, ReviewReqeust.Create reqeust) {
		var user = userRepository.findByIdOrThrow(2L, ErrorCode.NOT_FOUND_USER);

		//var orderProduct = productRepository.findById(productId);

		//Preconditions.validate(product.isPresent(), ErrorCode.NOT_FOUND_PRODUCT);

		Review review = new Review(
			user,
			reqeust.title(),
			reqeust.description(),
			reqeust.star()
		);

		reviewRepository.save(review);

	}

	public Page<ReviewResponse.ReviewList> myReviewList(Long userId, Pageable pageable) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		Page<Review> reviewList = reviewRepository.findReviewsByUserId(user.getId(), pageable);

		return ReviewResponse.ReviewList.fromList(reviewList);
	}

	public void update(Long reviewId, ReviewReqeust.Update request) {
		var user = userRepository.findByIdOrThrow(2L, ErrorCode.NOT_FOUND_USER);

		var review = reviewRepository.findByIdOrThrow(reviewId, ErrorCode.NOT_FOUND_REVIEW);

		review.update(
			user,
			request.description(),
			request.star()
		);
	}

	public void delete(Long reviewId) {
		var user = userRepository.findByIdOrThrow(2L, ErrorCode.NOT_FOUND_USER);

		var review = reviewRepository.findByIdOrThrow(reviewId, ErrorCode.NOT_FOUND_REVIEW);

		review.delete(user);

	}

	/*public Page<ReviewResponse.ReviewList> productReviewList(Long productId, Pageable pageable) {
		//TODO: findByIdOrThrow 추가되면 수정
		var product = productRepository.findById(productId);
		Preconditions.validate(product.isPresent(), ErrorCode.NOT_FOUND_PRODUCT);

		Page<Review> reviewList = reviewRepository.findReviewsByProductId(productId, pageable);

		return ReviewResponse.ReviewList.fromList(reviewList);
	}*/
}
