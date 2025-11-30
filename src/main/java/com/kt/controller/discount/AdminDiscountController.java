package com.kt.controller.discount;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.common.request.Paging;
import com.kt.common.support.SwaggerAssistance;
import com.kt.domain.user.User;
import com.kt.dto.discount.DiscountCreateRequest;
import com.kt.dto.discount.DiscountDetailResponse;
import com.kt.dto.discount.DiscountListResponse;
import com.kt.dto.discount.DiscountUpdateRequest;
import com.kt.security.CustomUserDetails;
import com.kt.service.discount.DiscountService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Discount", description = "관리자 할인 기능 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminDiscountController extends SwaggerAssistance {

	private final DiscountService discountService;

	// 역할을 어디에 두어야 하는가 -> 멤버십 페이지 -> 멤버십 선택 -> 할인 정책 생성 -> 멤버십에서 제어해야한다? 아니면 할인에서 제어한다?
	@PostMapping("/memberships/{membershipId}/discount")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> create(
		@PathVariable Long membershipId,
		@Valid @RequestBody DiscountCreateRequest request
	) {

		discountService.create(membershipId, request);

		return ApiResult.ok();
	}

	@GetMapping("discounts")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<DiscountListResponse>> getAllDiscount(
		Paging paging
	) {

		Page<DiscountListResponse> discounts = discountService.getAllDiscount(paging.toPageable());

		return ApiResult.ok(discounts);
	}

	@PutMapping("/discounts/{discountId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> update(
		@PathVariable Long discountId,
		@Valid @RequestBody DiscountUpdateRequest request
	) {

		discountService.update(discountId, request);

		return ApiResult.ok();
	}

	@DeleteMapping("/discounts/{discountId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> delete(
		@PathVariable Long discountId
	) {

		discountService.delete(discountId);

		return ApiResult.ok();
	}

	@GetMapping("/discounts/{discountId}/detail")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<DiscountDetailResponse> detail(
		@PathVariable Long discountId
	) {

		DiscountDetailResponse detail = discountService.detail(discountId);

		return ApiResult.ok(detail);
	}

}
