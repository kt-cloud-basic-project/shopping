package com.kt.controller.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kt.common.response.ApiResult;
import com.kt.dto.payment.PaymentCreateRequest;
import com.kt.service.payment.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public ApiResult<Void> create(PaymentCreateRequest request) {
		paymentService.create(request);
	}
}
