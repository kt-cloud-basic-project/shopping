package com.kt.controller.payment;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.request.Paging;
import com.kt.common.response.ApiResult;
import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.payment.PaymentCreateRequest;
import com.kt.dto.payment.PaymentDetailResponse;
import com.kt.dto.payment.PaymentListResponse;
import com.kt.security.CustomUserDetails;
import com.kt.service.payment.PaymentService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payment", description = "결제 기록 기능 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController extends SwaggerAssistance {

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

