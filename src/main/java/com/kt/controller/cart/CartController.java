package com.kt.controller.cart;

import com.kt.common.request.Paging;
import com.kt.common.response.ApiResult;
import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.cart.CartCreateRequest;
import com.kt.dto.cart.CartUpdateQuantityRequest;
import com.kt.dto.cart.response.CartResponse;
import com.kt.security.CustomUserDetails;
import com.kt.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart", description = "장바구니 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController extends SwaggerAssistance {
    private final CartService cartService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "장바구니 생성")
	public ApiResult<Long> create(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@Valid @RequestBody CartCreateRequest request) {
		Long cartId = cartService.create(currentUser.getId(), request);

		return ApiResult.of("장바구니 생성 완료", cartId);
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "장바구니 조회")
	public ApiResult<Page<CartResponse>> getCart(
		@Valid Paging paging,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		Page<CartResponse> carts = cartService.getCartList(currentUser.getId(), paging);
		return ApiResult.ok(carts);
	}

	@PatchMapping("/{cartId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "장바구니 수량 변경")
	public ApiResult<Long> updateQuantity(
		@Valid @RequestBody CartUpdateQuantityRequest request,
		@PathVariable Long cartId,
		@AuthenticationPrincipal CustomUserDetails currentUser) {
		cartService.updateQuantity(cartId, currentUser.getId(), request.productCount());

		return ApiResult.of("장바구니 상품 수량 변경 완료", cartId);
	}

	@DeleteMapping("/{cartId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "장바구니에서 특정 상품 삭제")
	public ApiResult<Long> deleteCartItem(@PathVariable Long cartId) {
		cartService.deleteCartItem(cartId);

		return ApiResult.of("장바구니 삭제 완료", cartId);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "장바구니 전체 삭제")
	public ApiResult<Void> clearCart(@AuthenticationPrincipal CustomUserDetails currentUser) {
		cartService.clearCart(currentUser.getId());

		return ApiResult.ok();
	}
}
