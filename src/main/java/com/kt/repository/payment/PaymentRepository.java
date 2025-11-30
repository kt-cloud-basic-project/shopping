package com.kt.repository.payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.payment.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	default Payment findByIdAndOrderUserIdAndIsDeletedFalseOrThorw(Long paymentId, Long userId, ErrorCode errorCode) {
		return findByIdAndOrderUserIdAndIsDeletedFalse(paymentId, userId).orElseThrow(() -> new CustomException(errorCode));
	}

	@EntityGraph(attributePaths = {"order", "paymentType", "order.user"})
	Optional<Payment> findByIdAndOrderUserIdAndIsDeletedFalse(Long paymentId, Long userId);
}

