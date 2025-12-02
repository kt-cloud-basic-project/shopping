package com.kt.repository.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.kt.dto.payment.PaymentListResponse;

public interface PaymentRepositoryCustom {
	Page<PaymentListResponse> getMyAllPayment(Long userId, Pageable pageable);
}
