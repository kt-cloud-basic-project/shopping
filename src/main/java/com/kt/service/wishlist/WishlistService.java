package com.kt.service.wishlist;

import org.springframework.stereotype.Service;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.product.Product;
import com.kt.domain.user.User;
import com.kt.domain.wishlist.Wishlist;
import com.kt.repository.WishlistRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistService {

	private final WishlistRepository wishlistRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	public void addWishlist(Long userId, Long productId) {  // loginId → userId
		//1. 사용자 조회
		User user = userRepository.findById(userId)  // findById로 변경
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

		// 2. 상품 조회
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

		// ✨ 이 부분이 빠져있었어요!
		if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
			throw new CustomException(ErrorCode.ALREADY_WISHLISTED);
		}

		// 3. 찜 저장
		Wishlist wishlist = new Wishlist(user, product);
		wishlistRepository.save(wishlist);
	}
}
