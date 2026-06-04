package com.kt.controller.payment;

import java.util.Map;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.service.payment.PaymentFacade;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mock/payments")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mock.payment.enabled", havingValue = "true")
public class MockPaymentController {

	private final PaymentFacade paymentFacade;

	@PostMapping("/confirm")
	public ApiResult<Long> mockConfirm(@RequestBody MockPaymentRequest request) {
		Map<String, Object> fakeTossResponse = Map.of(
			"paymentKey", "mock_" + UUID.randomUUID(),
			"totalAmount", request.amount(),
			"method", request.method()
		);

		Long paymentId = paymentFacade.completePayment(
			fakeTossResponse, request.userId(), request.orderId()
		);

		return ApiResult.ok(paymentId);
	}

	public record MockPaymentRequest(
		Long userId,
		Long orderId,
		Long amount,
		String method
	) {
	}
}
