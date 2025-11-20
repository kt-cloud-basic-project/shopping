package com.kt.controller.cart;

import com.kt.common.ApiResult;
import com.kt.dto.cart.CartCreateRequest;
import com.kt.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<Void> create(@Valid @RequestBody CartCreateRequest request) {
        cartService.create(request);

        return ApiResult.ok();
    }






    // 상품 추가
    // 수량 변경
    // 단일 삭제
    // 전체 삭제
    // 장바구니 조회 + 페이징
}
