package com.kt.controller.cart;

import com.kt.common.response.ApiResult;
import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.cart.CartCreateRequest;
import com.kt.dto.cart.CartUpdateQuantityRequest;
import com.kt.security.CustomUserDetails;
import com.kt.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Carts", description = "장바구니 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController extends SwaggerAssistance {
    private final CartService cartService;

    // CRUD: 장바구니 조회 (페이징), 수량 변경

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "장바구니 생성")
    public ApiResult<Void> create(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@Valid @RequestBody CartCreateRequest request) {
        cartService.create(currentUser.getId(), request);

        return ApiResult.ok();
    }

    @PatchMapping("/{cartId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "장바구니 수량 변경")
    public ApiResult<Void> updateQuantity(
            @Valid @RequestBody CartUpdateQuantityRequest request,
            @PathVariable Long cartId) {
        cartService.updateQuantity(cartId, request.productCount());

        return ApiResult.ok();
    }

    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "장바구니에서 특정 상품 삭제")
    public ApiResult<Void> deleteCartItem(@PathVariable Long cartId) {
        cartService.deleteCartItem(cartId);

        return ApiResult.ok();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "장바구니 전체 삭제")
    public ApiResult<Void> clearCart(@AuthenticationPrincipal CustomUserDetails currentUser) {
        cartService.clearCart(currentUser.getId());

        return ApiResult.ok();
    }

	//TODO: 조회 + 페이징 api 개발
}
