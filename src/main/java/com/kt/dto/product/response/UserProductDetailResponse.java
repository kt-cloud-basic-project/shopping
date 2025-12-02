package com.kt.dto.product.response;

import java.util.List;

import com.kt.domain.product.Product;
import com.kt.dto.variant.VariantListResponse;

public record UserProductDetailResponse (
	String name,
	String description,
	Long price,
	Long discountAmount, // 할인 금액
	Long discountedPrice, // 할인된 최종 금액
	String category,
	String status,
	List<VariantListResponse> variants
) {
	public static UserProductDetailResponse from(Product product, List<VariantListResponse> variants, Long discountAmount, Long discountedPrice) {
		return new UserProductDetailResponse(
			product.getName(),
			product.getDescription(),
			product.getPrice(),
			discountAmount,
			discountedPrice,
			product.getCategory().getType(),
			product.getStatus().getDescription(),
			variants
		);
	}
}

