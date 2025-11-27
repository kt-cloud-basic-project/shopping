package com.kt.repository.orderproduct;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.orderproduct.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
	default OrderProduct findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	default OrderProduct findWithOrderAndProductByIdThrow(Long id, ErrorCode errorCode) {
		return findWithOrderAndProductById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	@EntityGraph(attributePaths = {"order", "product", "order.user"})
	Optional<OrderProduct> findWithOrderAndProductById(Long id);
}
