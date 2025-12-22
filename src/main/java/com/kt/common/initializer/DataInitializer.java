package com.kt.common.initializer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.kt.domain.category.Category;
import com.kt.domain.discount.Discount;
import com.kt.domain.discount.DiscountTargetType;
import com.kt.domain.discount.DiscountType;
import com.kt.domain.membership.Membership;
import com.kt.domain.paymenttype.PaymentType;
import com.kt.domain.product.Product;
import com.kt.domain.shoppingaddress.ShoppingAddress;
import com.kt.domain.shoppingaddress.ShoppingAddressType;
import com.kt.domain.user.Gender;
import com.kt.domain.user.User;
import com.kt.domain.variant.Variant;
import com.kt.domain.variant.VariantType;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.paymenttype.PaymentTypeRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.shoppingaddress.ShoppingAddressRepository;
import com.kt.repository.user.UserRepository;
import com.kt.repository.variant.VariantRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class DataInitializer {
	private final MembershipRepository membershipRepository;
	private final DiscountRepository discountRepository;
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final VariantRepository variantRepository;
	private final ShoppingAddressRepository shoppingAddressRepository;
	private final PaymentTypeRepository paymentTypeRepository;

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

		/*List<User> users = new ArrayList<>();
		users.add(User.admin("admin", "$2a$10$D8MoLLsDk1rP5H7m3hCMsOOEPr1m.nCQwvFXm80Kaa/xWqcmX5uVm", "김케클", "test@example.com", "010-1111-2222",
			Gender.MALE, LocalDate.of(1992,5,30), memberships.getFirst()));
		users.add(User.normalUser("testUser", "$2a$10$D8MoLLsDk1rP5H7m3hCMsOOEPr1m.nCQwvFXm80Kaa/xWqcmX5uVm", "김케클2", "test2@example.com", "010-3333-4444",
			Gender.MALE, LocalDate.of(1992,5,30), memberships.get(2)));
		users.add(User.normalUser("testUser2", "$2a$10$D8MoLLsDk1rP5H7m3hCMsOOEPr1m.nCQwvFXm80Kaa/xWqcmX5uVm", "김케클3", "test3@example.com", "010-5555-6666",
			Gender.FEMALE, LocalDate.of(1992,5,30), memberships.get(6)));
		users.add(User.normalUser("testUser3", "$2a$10$D8MoLLsDk1rP5H7m3hCMsOOEPr1m.nCQwvFXm80Kaa/xWqcmX5uVm", "김케클4", "test3@example.com", "010-7777-8888",
			Gender.FEMALE, LocalDate.of(1992,5,30), memberships.get(7)));

		userRepository.saveAll(users);

		List<Discount> discounts = new ArrayList<>();
		discounts.add(new Discount("신규회원 웰컴 5% 할인", DiscountTargetType.MEMBERSHIP, DiscountType.PERCENTAGE, false, 5L));
		discounts.add(new Discount("첫 구매 감사 1,000원 할인", DiscountTargetType.MEMBERSHIP, DiscountType.FIXED_AMOUNT, true, 1000L));
		discounts.add(new Discount("실버 회원 3,000원 할인", DiscountTargetType.MEMBERSHIP, DiscountType.FIXED_AMOUNT, true, 3000L));
		discounts.add(new Discount("골드 회원 5,000원 할인", DiscountTargetType.MEMBERSHIP, DiscountType.FIXED_AMOUNT, true, 5000L));
		discounts.add(new Discount("플래티넘 회원 10,000원 할인", DiscountTargetType.MEMBERSHIP, DiscountType.FIXED_AMOUNT, true, 10000L));
		discounts.add(new Discount("에메랄드 고객님 특가 10% 할인", DiscountTargetType.MEMBERSHIP, DiscountType.PERCENTAGE, true, 10L));
		discounts.add(new Discount("다이아몬드 고객님 특가 13% 할인", DiscountTargetType.MEMBERSHIP, DiscountType.PERCENTAGE, true, 13L));
		discounts.add(new Discount("마스터 고객님 특가 15% 할인", DiscountTargetType.MEMBERSHIP, DiscountType.PERCENTAGE, true, 15L));
		discounts.add(new Discount("그랜드 마스터 고객님 감사 20% 할인", DiscountTargetType.MEMBERSHIP, DiscountType.PERCENTAGE, true, 20L));
		discounts.add(new Discount("챌린저 고객님 감사 30% 할인", DiscountTargetType.MEMBERSHIP, DiscountType.PERCENTAGE, true, 30L));

		discountRepository.saveAll(discounts);

		List<Category> categories = new ArrayList<>();
		categories.add(new Category("TOP"));
		categories.add(new Category("BOTTOM"));

		categoryRepository.saveAll(categories);

		List<Product> products = new ArrayList<>();
		products.add(new Product("오버핏 맨투맨", "맨투맨", 58000L, 80L, categories.get(0)));
		products.add(new Product("와이드 슬랙스", "슬랙스", 65000L, 75L, categories.get(1)));

		productRepository.saveAll(products);

		List<Variant> variants = new ArrayList<>();
		variants.add(new Variant(VariantType.COLOR, "BLACK", products.getFirst()));
		variants.add(new Variant(VariantType.SIZE, "L", products.getFirst()));
		variants.add(new Variant(VariantType.COLOR, "GRAY", products.get(1)));
		variants.add(new Variant(VariantType.SIZE, "M", products.get(1)));

		variantRepository.saveAll(variants);

		List<ShoppingAddress> shoppingAddresses = new ArrayList<>();
		shoppingAddresses.add(new ShoppingAddress(users.getFirst(),"집", "경기도 광명시 ooo동 oooo호", "010-1111-2222",
			ShoppingAddressType.ETC, "감사합니다", true));
		shoppingAddresses.add(new ShoppingAddress(users.get(1),"집", "경기도 광명시 ooo동 oooo호", "010-2222-3333",
			ShoppingAddressType.ETC, "감사합니다", true));
		shoppingAddresses.add(new ShoppingAddress(users.get(2),"집", "경기도 광명시 ooo동 oooo호", "010-4444-5555",
			ShoppingAddressType.ETC, "감사합니다", true));
		shoppingAddresses.add(new ShoppingAddress(users.get(3),"집", "경기도 광명시 ooo동 oooo호", "010-6666-7777",
			ShoppingAddressType.ETC, "감사합니다", true));

		shoppingAddressRepository.saveAll(shoppingAddresses);

		List<PaymentType> paymentTypes = new ArrayList<>();
		paymentTypes.add(new PaymentType("CREDIT_CARD"));

		paymentTypeRepository.saveAll(paymentTypes);*/

	}
}
