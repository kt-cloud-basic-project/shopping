package com.kt.controller.discount;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.dto.discount.DiscountUserResponse;
import com.kt.security.CustomUserDetails;
import com.kt.service.discount.DiscountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Discount", description = "Discount 사용자용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/discounts")
public class DiscountController {
	private final DiscountService discountService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "내 할인 정보 조회")
	public ApiResult<DiscountUserResponse> getMyDiscount(
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {

		DiscountUserResponse response = discountService.getMyDiscount(currentUser.getId());

		return ApiResult.ok(response);
	}
}
