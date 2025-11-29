package com.kt.common.initializer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.kt.domain.discount.Discount;
import com.kt.domain.discount.DiscountType;
import com.kt.domain.membership.Membership;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.membership.MembershipRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer {
	private final MembershipRepository membershipRepository;
	private final DiscountRepository discountRepository;

	@PostConstruct
	@Transactional
	public void init() {
		if (membershipRepository.count() > 0) {
			return;
		}

		List<Membership> memberships = new ArrayList<>();
		memberships.add(new Membership("IRON"));
		memberships.add(new Membership("BRONZE"));
		memberships.add(new Membership("SILVER"));
		memberships.add(new Membership("GOLD"));
		memberships.add(new Membership("PLATINUM"));
		memberships.add(new Membership("EMERALD"));
		memberships.add(new Membership("DIAMOND"));
		memberships.add(new Membership("MASTER"));
		memberships.add(new Membership("GRAND_MASTER"));
		memberships.add(new Membership("CHALLENGER"));

		membershipRepository.saveAll(memberships);

		List<Discount> discounts = new ArrayList<>();
		discounts.add(new Discount("신규회원 웰컴 5% 할인", DiscountType.PERCENTAGE, 5, memberships.get(0)));
		/*discounts.add(new Discount("첫 구매 감사 1,000원 할인", DiscountType.FIXED_AMOUNT, 1000, memberships.get(1)));
		discounts.add(new Discount("실버 회원 3,000원 할인", DiscountType.FIXED_AMOUNT, 3000, memberships.get(2)));
		discounts.add(new Discount("골드 회원 5,000원 할인", DiscountType.FIXED_AMOUNT, 5000, memberships.get(3)));
		discounts.add(new Discount("플래티넘 회원 10,000원 할인", DiscountType.FIXED_AMOUNT, 10000, memberships.get(4)));
		discounts.add(new Discount("에메랄드 고객님 특가 10% 할인", DiscountType.PERCENTAGE, 10, memberships.get(5)));
		discounts.add(new Discount("다이아몬드 고객님 특가 13% 할인", DiscountType.PERCENTAGE, 13, memberships.get(6)));
		discounts.add(new Discount("마스터 고객님 특가 15% 할인", DiscountType.PERCENTAGE, 15, memberships.get(7)));
		discounts.add(new Discount("그랜드 마스터 고객님 감사 20% 할인", DiscountType.PERCENTAGE, 20, memberships.get(8)));
		discounts.add(new Discount("챌린저 고객님 감사 30% 할인", DiscountType.PERCENTAGE, 30, memberships.get(9)));*/


		discountRepository.saveAll(discounts);
	}
}
