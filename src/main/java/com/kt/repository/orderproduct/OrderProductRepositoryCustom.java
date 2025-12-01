package com.kt.repository.orderproduct;

public interface OrderProductRepositoryCustom {
	boolean hasInvalidStatusWithVariantId(Long variantId);
	boolean hasInvalidStatusWithProductId(Long productId);
}
