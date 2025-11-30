package com.kt.repository.orderproduct;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.orderproduct.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
	List<OrderProduct> findByOrderId(Long orderId);
}
