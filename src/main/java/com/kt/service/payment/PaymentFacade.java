package com.kt.service.payment;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.service.order.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
	private final OrderService orderService;
	private final PaymentService paymentService;

	@Transactional
	public Long completePayment(Map<String, Object> tossResponse, Long userId, Long orderId) {
		var paymentId = paymentService.create(tossResponse, userId);
		orderService.save(orderId);

		return paymentId;
	}
}
