package com.kt.repository.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.domain.order.Order;
import com.kt.dto.order.response.OrderListResponse;

public interface OrderRepositoryCustom {
	Page<Order> getOrders(Long userId, Pageable pageable);
}
