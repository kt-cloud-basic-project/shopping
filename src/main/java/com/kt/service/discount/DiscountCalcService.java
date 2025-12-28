package com.kt.service.discount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kt.common.exception.ErrorCode;
import com.kt.domain.discount.Discount;
import com.kt.domain.discount.policy.DiscountPolicy;
import com.kt.domain.discount.policy.DiscountPolicyFactory;
import com.kt.domain.discountMembership.DiscountMembership;
import com.kt.domain.discountProduct.DiscountProduct;
import com.kt.dto.discount.response.DiscountResult;
import com.kt.repository.discountmembership.DiscountMembershipRepository;
import com.kt.repository.discountproduct.DiscountProductRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscountCalcService {

	private final UserRepository userRepository;
	private final DiscountMembershipRepository discountMembershipRepository;
	private final DiscountProductRepository discountProductRepository;

	public DiscountResult calculate(Long originalPrice, Discount membershipDiscount, List<Discount> productDiscounts) {

		long maxDiscountPrice = 0L;

		if (membershipDiscount != null) {
			long membershipOnly = calcDiscount(originalPrice, membershipDiscount);
			maxDiscountPrice = Math.max(maxDiscountPrice, membershipOnly);
		}

		if (productDiscounts != null && !productDiscounts.isEmpty()) {
			for (Discount productDiscount : productDiscounts) {
				long productOnly = calcDiscount(originalPrice, productDiscount);
				maxDiscountPrice = Math.max(maxDiscountPrice, productOnly);
			}
		}

		if (membershipDiscount != null && productDiscounts != null) {
			for (Discount productDiscount : productDiscounts) {
				if (membershipDiscount.isCombinable() && productDiscount.isCombinable()) {
					long combined = calcCombined(originalPrice, membershipDiscount, productDiscount);
					maxDiscountPrice = Math.max(maxDiscountPrice, combined);
				}
			}
		}

		return new DiscountResult(
			originalPrice,
			maxDiscountPrice,
			originalPrice - maxDiscountPrice
		);
	}

	private long calcDiscount(Long originalPrice, Discount discount) {
		DiscountPolicy policy = DiscountPolicyFactory.getDiscountPolicy(discount);
		return policy.calcDiscountPrice(originalPrice);
	}

	private long calcCombined(
		Long originalPrice,
		Discount membershipDiscount,
		Discount productDiscount
	) {
		long membershipAmount = calcDiscount(originalPrice, membershipDiscount);
		long productAmount = calcDiscount(originalPrice, productDiscount);

		long totalDiscount = membershipAmount + productAmount;

		return Math.min(totalDiscount, originalPrice);
	}

	public Discount getMembershipDiscount(Long userId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		return discountMembershipRepository
			.findByMembershipId(user.getMembership().getId())
			.map(DiscountMembership::getDiscount)
			.orElse(null);
	}

	public Map<Long, List<Discount>> getProductsDiscount(List<Long> productIds) {
		List<DiscountProduct> discountProducts = discountProductRepository.findByProductIdIn(productIds);

		Map<Long, List<Discount>> result = new HashMap<>();
		for (DiscountProduct dp : discountProducts) {
			result.computeIfAbsent(dp.getProduct().getId(), k -> new ArrayList<>())
				.add(dp.getDiscount());
		}

		return result;
	}

}
