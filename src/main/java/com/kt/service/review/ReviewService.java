package com.kt.service.review;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.support.Preconditions;
import com.kt.domain.order.OrderStatus;
import com.kt.domain.review.Review;
import com.kt.dto.review.ReviewCreateRequest;
import com.kt.dto.review.ReviewListResponse;
import com.kt.dto.review.ReviewUpdateRequest;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.review.ReviewRepositoryCustom;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewRepositoryCustom reviewRepositoryCustom;
	private final UserRepository userRepository;
	private final OrderProductRepository orderProductRepository;
	private final ProductRepository productRepository;

	public void create(Long userId, Long orderProductId, ReviewCreateRequest request) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		// 주문상품인지 조회
		var orderProduct = orderProductRepository.findWithOrderAndProductByIdThrow(orderProductId, ErrorCode.NOT_FOUND_ORDER_PRODUCT);

		// 본인 주문 확인
		Preconditions.validate(orderProduct.getOrder().getUser().getId().equals(userId), ErrorCode.NOT_ORDER_USER);

		// 배송 상태 확인
		Preconditions.validate(orderProduct.getOrder().getOrderStatus().equals(OrderStatus.DELIVERED), ErrorCode.DELIVERY_NOT_COMPLETED_ORDER);

		// 리뷰 작성 기한 - 배송 완료 후 N일 이내만 작성 가능
		LocalDate deliveredAt = orderProduct.getOrder().getDeliveredAt();
		Preconditions.validate(deliveredAt != null, ErrorCode.DELIVERY_NOT_COMPLETED_ORDER);

		boolean isExpDate = LocalDate.now().isAfter(deliveredAt.plusDays(7));
		Preconditions.validate(!isExpDate, ErrorCode.REVIEW_WRITE_DATE_EXPIRED);

		// 중복 리뷰 방지
		var existingReview = reviewRepository.existsByUserIdAndOrderProductId(userId, orderProductId);
		Preconditions.validate(!existingReview, ErrorCode.DUPLICATE_REVIEW);

		Review review = new Review(
			user,
			orderProduct,
			request.title(),
			request.description(),
			request.star()
		);

		reviewRepository.save(review);

	}

	public Page<ReviewListResponse> getMyAllReview(Long userId, Pageable pageable) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		return reviewRepositoryCustom.getMyAllReview(user.getId(), pageable);
	}

	public void update(Long userId, Long reviewId, ReviewUpdateRequest request) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var review = reviewRepository.findByIdOrThrow(reviewId, ErrorCode.NOT_FOUND_REVIEW);

		Preconditions.validate(review.getUser().getId().equals(user.getId()), ErrorCode.NOT_REVIEW_AUTHOR);

		review.update(
			request.title(),
			request.description(),
			request.star()
		);
	}

	public void delete(Long userId, Long reviewId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var review = reviewRepository.findByIdOrThrow(reviewId, ErrorCode.NOT_FOUND_REVIEW);

		Preconditions.validate(review.getUser().getId().equals(user.getId()), ErrorCode.NOT_REVIEW_AUTHOR);

		review.delete();

	}

	public void hide(Long reviewId) {

		var review = reviewRepository.findByIdOrThrow(reviewId, ErrorCode.NOT_FOUND_REVIEW);

		review.delete();

	}
  
	public Page<ReviewListResponse> getProductAllReview(Long productId, Pageable pageable) {

		productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);

		return reviewRepositoryCustom.getProductAllReview(productId, pageable);
	}
}
