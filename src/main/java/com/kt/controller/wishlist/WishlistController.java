package com.kt.controller.wishlist;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.security.CustomUserDetails;
import com.kt.service.wishlist.WishlistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Wishlist", description = "찜 관련 API")
@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {
	private final WishlistService wishlistService;

	@PostMapping("/{productId}")
	@Operation(summary = "찜 추가")
	public ApiResult<Void> addWishlist(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long productId
	) {
		wishlistService.addWishlist(currentUser.getId(), productId);
		return ApiResult.ok();
	}

	@DeleteMapping("/{productId}")
	@Operation(summary="찜 삭제")
	public ApiResult<Void> removeWishlist(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long productId
	) {
		wishlistService.removeWishlist(currentUser.getId(),productId);
		return ApiResult.ok();
	}

	@DeleteMapping
	@Operation(summary="찜 전체 삭제")
	public ApiResult<Void> removeAllWishlist(
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		wishlistService.removeAllWishlist(currentUser.getId());
		return ApiResult.ok();
	}



}