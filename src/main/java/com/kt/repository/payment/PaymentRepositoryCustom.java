package com.kt.repository.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.dto.payment.PaymentListResponse;
import com.kt.dto.payment.PaymentOrderInfoResponse;

public interface PaymentRepositoryCustom {
	Page<PaymentListResponse> getMyAllPayment(Long userId, Pageable pageable);

	PaymentOrderInfoResponse getPaymentDetailByOrderId(Long orderId);
}
