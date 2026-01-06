package com.kt.repository.order;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.domain.order.Order;
import com.kt.dto.order.response.OrderListResponse;

public interface OrderRepositoryCustom {
	Page<Order> getOrders(Long userId, Pageable pageable);

	Order getOrderDetailById(Long userId, Long orderId);
}
