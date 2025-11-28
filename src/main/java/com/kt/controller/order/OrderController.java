package com.kt.controller.order;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.dto.order.OrderCreateRequest;
import com.kt.security.CustomUserDetails;
import com.kt.service.order.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	@PostMapping("")
	public ApiResult<Void> create(@AuthenticationPrincipal CustomUserDetails currentUser,
		@Valid @RequestBody OrderCreateRequest orderCreateRequest) {
		orderService.create(currentUser.getId(), orderCreateRequest);
		return ApiResult.ok();
	}
}
