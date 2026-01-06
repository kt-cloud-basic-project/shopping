package com.kt.dto.payment;

import java.time.LocalDateTime;
import java.util.List;

import com.kt.domain.order.OrderStatus;
import com.kt.dto.order.response.OrderProductResponse;

public record PaymentOrderInfoResponse(
	Long orderId,
	OrderStatus orderStatus,
	String orderStatusDescription,
	LocalDateTime createdAt,
	String receiverName,
	String receiverPhone,
	String receiverAddress,
	UserPaymentInfo user,
	List<OrderProductInfo> orderProducts,
	PriceInfo priceInfo
) {
	public record UserPaymentInfo(
		String name,
		String email,
		String mobile,
		MembershipInfo membership,
		Long money
	) {}

	public record MembershipInfo(
		String level
	) {}

	public record AppliedDiscountInfo(
		String discountName,
		String discountType,
		Long discountAmount
	) {}

	public record PriceInfo(
		Long totalProductPrice,
		Long totalDiscountPrice,
		Long deliveryFee,
		Long finalPrice
	) {}

	public record OrderProductInfo(
		Long productId,
		String productName,
		Long productVariantId,
		Long productCount,
		Long price,
		Long totalPrice,
		Long discountPrice,
		Long discountedPrice,
		List<AppliedDiscountInfo> appliedDiscounts  // 할인 목록 포함
	) {}
}
