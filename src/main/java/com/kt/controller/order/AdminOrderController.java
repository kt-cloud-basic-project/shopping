package com.kt.controller.order;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.request.Paging;
import com.kt.common.response.ApiResult;
import com.kt.dto.order.OrderDetailResponse;
import com.kt.dto.order.response.OrderListResponse;
import com.kt.security.CustomUserDetails;
import com.kt.service.order.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Order", description = "Order 관리자용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
public class AdminOrderController {
	private final OrderService orderService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "주문 목록 조회")
	public ApiResult<Page<OrderListResponse>> getOrderList(
		Paging paging,
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
	@Operation(summary = "주문 상세 조회")
	public ApiResult<OrderDetailResponse> getOrderDetail(@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable("orderId") Long orderId) {
		return ApiResult.ok(orderService.getOrderDetail(currentUser.getId(), orderId));
	}
}
