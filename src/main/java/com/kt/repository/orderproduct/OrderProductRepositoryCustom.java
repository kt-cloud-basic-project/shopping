package com.kt.repository.orderproduct;

import java.util.List;

import com.kt.domain.orderproduct.OrderProduct;

public interface OrderProductRepositoryCustom {
	boolean hasInvalidStatusWithVariantId(Long variantId);
	boolean hasInvalidStatusWithProductId(Long productId);
	List<OrderProduct> getOrderProductByOrderId(Long orderId);
}
