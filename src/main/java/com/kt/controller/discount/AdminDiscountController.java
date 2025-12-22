package com.kt.controller.discount;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.kt.dto.discount.request.DiscountCreateRequest;
import com.kt.dto.discount.response.DiscountMembershipDetailResponse;
import com.kt.dto.discount.response.DiscountListResponse;
import com.kt.dto.discount.request.DiscountUpdateRequest;
import com.kt.dto.discount.response.DiscountProductDetailResponse;
import com.kt.service.discount.DiscountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Discount", description = "Discount 관리자용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/discounts")
public class AdminDiscountController extends SwaggerAssistance {

	private final DiscountService discountService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "할인 정책 등록")
	public ApiResult<Long> create(
		@Valid @RequestBody DiscountCreateRequest request
	) {
		return ApiResult.ok(discountService.create(request));
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "할인 정책 목록 조회")
	public ApiResult<Page<DiscountListResponse>> getAllDiscount(
		@Valid Paging paging
	) {

		Page<DiscountListResponse> discounts = discountService.getAllDiscount(paging.toPageable());

		return ApiResult.ok(discounts);
	}

	@PutMapping("/{discountId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "할인 정책 수정")
	public ApiResult<Long> update(
		@PathVariable Long discountId,
		@Valid @RequestBody DiscountUpdateRequest request
	) {
		return ApiResult.ok(discountService.update(discountId, request));
	}

	@DeleteMapping("/{discountId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "할인 정책 삭제")
	public ApiResult<Long> delete(
		@PathVariable Long discountId
	) {
		return ApiResult.ok(discountService.delete(discountId));
	}

	@GetMapping("/{discountId}/detail/membership")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "멤버십 할인 정책 상세 조회")
	public ApiResult<DiscountMembershipDetailResponse> getDetailMembership(
		@PathVariable Long discountId
	) {

		DiscountMembershipDetailResponse detail = discountService.getDetailMembership(discountId);

		return ApiResult.ok(detail);
	}

	@GetMapping("/{discountId}/detail/product")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "상품 할인 정책 상세 조회")
	public ApiResult<DiscountProductDetailResponse> getDetailProduct(
		@PathVariable Long discountId
	) {

		DiscountProductDetailResponse detail = discountService.getDetailProduct(discountId);

		return ApiResult.ok(detail);
	}

}
