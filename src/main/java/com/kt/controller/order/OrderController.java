package com.kt.controller.order;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.request.Paging;
import com.kt.common.response.ApiResult;
import com.kt.dto.order.OrderCreateRequest;
import com.kt.dto.order.OrderDetailResponse;
import com.kt.dto.order.response.OrderListResponse;
import com.kt.security.CustomUserDetails;
import com.kt.service.order.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Order", description = "주문 관련 API")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
	private final OrderService orderService;

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "주문 생성")
	public ApiResult<Void> create(@AuthenticationPrincipal CustomUserDetails currentUser,
		@Valid @RequestBody OrderCreateRequest orderCreateRequest) {
		orderService.create(currentUser.getId(), orderCreateRequest);
		return ApiResult.ok();
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "주문 목록 조회")
	public ApiResult<Page<OrderListResponse>> getOrderList(
		@Valid Paging paging,
		@AuthenticationPrincipal CustomUserDetails currentUser
		) {
		Page<OrderListResponse> orderList = orderService.getOrderList(currentUser.getId(), paging);

		return ApiResult.ok(orderList);
	}

	@PatchMapping("/{orderId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "주문 취소")
	public ApiResult<Void> cancel(@PathVariable Long orderId,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		orderService.cancel(orderId, currentUser.getId());

		return ApiResult.ok();
	}


	@GetMapping("/{orderId}")
	public ApiResult<OrderDetailResponse> getOrderDetail(@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable("orderId") Long orderId) {
		return ApiResult.ok(orderService.getOrderDetail(currentUser.getId(), orderId));
	}
}
