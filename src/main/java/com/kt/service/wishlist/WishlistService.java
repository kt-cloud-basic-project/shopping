package com.kt.service.wishlist;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.product.Product;
import com.kt.domain.user.User;
import com.kt.domain.wishlist.Wishlist;
import com.kt.dto.wishlist.WishlistResponse;
import com.kt.event.WishlistAddedEvent;
import com.kt.repository.wishlist.WishlistRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;

@Service
@Transactional
@RequiredArgsConstructor
public class WishlistService {

	private final WishlistRepository wishlistRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final ApplicationEventPublisher eventPublisher;

	public Long addWishlist(Long userId, Long productId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_PRODUCT));

		if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
			throw new CustomException(ErrorCode.ALREADY_WISHLISTED);
		}

		Wishlist wishlist = new Wishlist(user, product);
		Wishlist saveWishlist = wishlistRepository.save(wishlist);

		eventPublisher.publishEvent(
			new WishlistAddedEvent(userId, productId)
		);
		return saveWishlist.getId();
	}

	@Transactional
	public void removeWishlist(Long userId, Long productId) {
		if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
			throw new CustomException(ErrorCode.NOT_FOUND_WISHLIST);
		}

		wishlistRepository.deleteByUserIdAndProductId(userId, productId);
	}

	public void removeAllWishlist(Long userId) {
		wishlistRepository.deleteByUserId(userId);
	}

	@Transactional(readOnly = true)
	public List<WishlistResponse> getWishlists(Long userId) {
		var wishlists = wishlistRepository.findByUserId(userId);

		return wishlists.stream()
			.map(wishlist -> new WishlistResponse(
				wishlist.getId(),
				wishlist.getProduct().getId(),
				wishlist.getProduct().getName(),
				wishlist.getProduct().getPrice(),
				wishlist.getCreatedAt()
			)).toList();
	}
}


