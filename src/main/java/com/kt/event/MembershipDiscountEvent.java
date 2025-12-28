package com.kt.event;

public record MembershipDiscountEvent(
	Long discountId,
	Long membershipId,
	String discountName,
	Long discountValue
) {
}
