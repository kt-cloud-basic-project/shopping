package com.kt.repository.order;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.order.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	default Order findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	Optional<Order> findByIdAndUserId(Long id, Long userId);

	default Order findByIdAndUserIdOrThrow(Long id, Long userId, ErrorCode errorCode) {
		return findByIdAndUserId(id, userId).orElseThrow(() -> new CustomException(errorCode));
	}
}
