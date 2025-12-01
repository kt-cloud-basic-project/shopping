package com.kt.controller.payment;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.request.Paging;
import com.kt.common.response.ApiResult;
import com.kt.domain.payment.Payment;
import com.kt.dto.payment.PaymentCreateRequest;
import com.kt.dto.payment.PaymentDetailResponse;
import com.kt.dto.payment.PaymentListResponse;
import com.kt.security.CustomUserDetails;
import com.kt.service.payment.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<PaymentListResponse>> getMyAllPayment(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		Paging paging
	) {
		return ApiResult.ok(paymentService.getMyAllPayment(currentUser.getId(), paging.toPageable()));
	}

	@GetMapping("/{paymentId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<PaymentDetailResponse> getPayment(
		@PathVariable("paymentId") Long paymentId,
		@AuthenticationPrincipal CustomUserDetails currentUser
		) {
		return ApiResult.ok(paymentService.getPayment(currentUser.getId(), paymentId));
	}

	@PostMapping("")
	public ApiResult<Void> create(@Valid PaymentCreateRequest request, @AuthenticationPrincipal CustomUserDetails currentUser) {
		paymentService.create(request, currentUser.getId());
		return ApiResult.ok();
	}


}

