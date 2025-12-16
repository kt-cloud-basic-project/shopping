package com.kt.service.discount;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.category.Category;
import com.kt.domain.discount.Discount;
import com.kt.domain.discount.DiscountType;
import com.kt.domain.membership.Membership;
import com.kt.domain.product.Product;
import com.kt.domain.user.Gender;
import com.kt.domain.user.User;
import com.kt.domain.variant.Variant;
import com.kt.dto.discount.DiscountCreateRequest;
import com.kt.dto.discount.DiscountUpdateRequest;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;

import jakarta.transaction.Transactional;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiscountServiceTest {

	@Autowired
	private DiscountService discountService;

	@Autowired
	private DiscountRepository discountRepository;

	@Autowired
	private MembershipRepository membershipRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	private Membership bronzeMembership;
	private Membership silverMembership;
	private Membership goldMembership;
	private User user;

	private Category category;
	private Product product;
	private Variant variant;

	@BeforeEach
	void setUp() {
		bronzeMembership = membershipRepository.saveAndFlush(new Membership("BRONZE"));
		silverMembership = membershipRepository.saveAndFlush(new Membership("SILVER"));
		goldMembership = membershipRepository.saveAndFlush(new Membership("GOLD"));

		user = userRepository.saveAndFlush(User.normalUser(
			"testUser",
			"password123!",
			"테스트유저",
			"test@test.com",
			"010-1234-5679",
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			silverMembership
		));

		category = categoryRepository.saveAndFlush(new Category("TOP"));

		product = productRepository.saveAndFlush(new Product(
			"베이직 티셔츠",
			"심플하고 편안한 베이직 티셔츠",
			29900L,
			100L,
			category
		));

	}

	@Test
	void 할인_등록_멤버십_존재여부_검증() {
		var membershipId = 9999999L;

		//given
		DiscountCreateRequest request = new DiscountCreateRequest(
			"테스트 퍼센트 할인",
			DiscountType.PERCENTAGE,
			10
		);

		//when then
		assertThatThrownBy(() -> {
			discountService.create(membershipId, request);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_MEMBERSHIP.getMessage());

	}

	@Test
	void 멤버십_할인_중복_검증() {

		//given
		DiscountCreateRequest request = new DiscountCreateRequest(
			"테스트 퍼센트 할인",
			DiscountType.PERCENTAGE,
			10
		);

		discountService.create(silverMembership.getId(), request);

		DiscountCreateRequest request2 = new DiscountCreateRequest(
			"테스트 퍼센트 할인",
			DiscountType.PERCENTAGE,
			10
		);

		//when then
		assertThatThrownBy(() -> {
			discountService.create(silverMembership.getId(), request2);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.DISCOUNT_ALREADY_EXISTS.getMessage());
	}

	@Test
	void 할인_등록시_퍼센트_할인_값_검증() {

		//given
		DiscountCreateRequest request = new DiscountCreateRequest(
			"테스트 퍼센트 할인",
			DiscountType.PERCENTAGE,
			150
		);

		//when & then
		assertThatThrownBy(() -> {
			discountService.create(silverMembership.getId(), request);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.INVALID_PERCENTAGE_DISCOUNT_VALUE.getMessage());
	}

	@Test
	void 할인_등록_성공() {

		//given
		DiscountCreateRequest request = new DiscountCreateRequest(
			"테스트 퍼센트 할인",
			com.kt.domain.discount.DiscountType.PERCENTAGE,
			10
		);

		//when
		discountService.create(silverMembership.getId(), request);

		//then
		var savedDiscount = discountRepository.findAll().getFirst();
		assertThat(savedDiscount.getName()).isEqualTo("테스트 퍼센트 할인");
		assertThat(savedDiscount.getType()).isEqualTo(DiscountType.PERCENTAGE);
		assertThat(savedDiscount.getValue()).isEqualTo(10);
	}

	@Test
	void 등록된_할인_목록() {
		//given
		List<Long> membershipIds = List.of(
			bronzeMembership.getId(),
			silverMembership.getId(),
			goldMembership.getId()
		);

		for (int i = 0; i < 3; i++) {
			DiscountCreateRequest request = new DiscountCreateRequest(
				"테스트 퍼센트 할인" + i,
				DiscountType.PERCENTAGE,
				10 + i
			);

			discountService.create(membershipIds.get(i), request);
		}

		//when
		Pageable pageable = PageRequest.of(0, 10);
		var discounts = discountService.getAllDiscount(pageable);

		//then
		assertThat(discounts).hasSize(3);
		assertThat(discounts)
			.extracting("name", "type", "value")
			.containsExactlyInAnyOrder(
				tuple("테스트 퍼센트 할인0", DiscountType.PERCENTAGE, 10),
				tuple("테스트 퍼센트 할인1", DiscountType.PERCENTAGE, 11),
				tuple("테스트 퍼센트 할인2", DiscountType.PERCENTAGE, 12)
			);
	}

	@Test
	void 할인_수정_할인_존재_검증() {
		var discountId = 9999999L;

		//given
		DiscountUpdateRequest request = new DiscountUpdateRequest(
			"테스트 퍼센트 할인 수정",
			com.kt.domain.discount.DiscountType.PERCENTAGE,
			15
		);

		// then
		assertThatThrownBy(() -> {
			discountService.update(discountId, request);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_DISCOUNT.getMessage());
	}

	@Test
	void 할인_수정시_퍼센트_할인_값_검증() {

		//given
		DiscountCreateRequest request = new DiscountCreateRequest(
			"테스트 퍼센트 할인",
			DiscountType.PERCENTAGE,
			10
		);

		discountService.create(silverMembership.getId(), request);

		var savedDiscount = discountRepository.findAll().getFirst();

		DiscountUpdateRequest request2 = new DiscountUpdateRequest(
			"테스트 퍼센트 할인 수정",
			DiscountType.PERCENTAGE,
			150
		);

		//when & then
		assertThatThrownBy(() -> {
			discountService.update(savedDiscount.getId(), request2);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.INVALID_PERCENTAGE_DISCOUNT_VALUE.getMessage());
	}

	@Test
	void 할인_수정_성공() {
		//given
		DiscountCreateRequest request = new DiscountCreateRequest(
			"테스트 퍼센트 할인",
			DiscountType.PERCENTAGE,
			10
		);

		discountService.create(silverMembership.getId(), request);
		var savedDiscount = discountRepository.findAll().getFirst();

		DiscountUpdateRequest updateRequest = new DiscountUpdateRequest(
			"테스트 퍼센트 할인 수정",
			DiscountType.PERCENTAGE,
			15
		);

		//when
		discountService.update(savedDiscount.getId(), updateRequest);
		var updatedDiscount = discountRepository.findById(savedDiscount.getId()).get();

		//then
		assertThat(updatedDiscount.getMembership().getId()).isEqualTo(savedDiscount.getMembership().getId());
		assertThat(updatedDiscount.getName()).isEqualTo("테스트 퍼센트 할인 수정");
		assertThat(updatedDiscount.getType()).isEqualTo(DiscountType.PERCENTAGE);
		assertThat(updatedDiscount.getValue()).isEqualTo(15);
	}

	@Test
	void 할인_삭제시_존재여부_검증() {
		var discountId = 9999999L;

		// when then
		assertThatThrownBy(() -> {
			discountService.delete(discountId);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_DISCOUNT.getMessage());
	}

	@Test
	void 할인_삭제_성공() {
		//given
		DiscountCreateRequest request = new DiscountCreateRequest(
			"테스트 퍼센트 할인",
			DiscountType.PERCENTAGE,
			10
		);

		discountService.create(silverMembership.getId(), request);
		var savedDiscount = discountRepository.findAll().getFirst();

		//when
		discountService.delete(savedDiscount.getId());

		//then
		assertThat(discountRepository.findById(savedDiscount.getId())).isEmpty();
	}

	@Test
	void 할인_상세조회_존재여부_검증() {
		var discountId = 9999999L;

		// when then
		assertThatThrownBy(() -> {
			discountService.detail(discountId);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_DISCOUNT.getMessage());
	}

	@Test
	void 할인_상세조회_성공() {
		//given
		DiscountCreateRequest request = new DiscountCreateRequest(
			"테스트 퍼센트 할인",
			DiscountType.PERCENTAGE,
			10
		);

		discountService.create(silverMembership.getId(), request);
		var savedDiscount = discountRepository.findAll().getFirst();

		//when
		var discountDetail = discountService.detail(savedDiscount.getId());

		//then
		assertThat(discountDetail.membershipId()).isEqualTo(silverMembership.getId());
		assertThat(discountDetail.membershipLevel()).isEqualTo(silverMembership.getLevel());
		assertThat(discountDetail.id()).isEqualTo(savedDiscount.getId());
		assertThat(discountDetail.name()).isEqualTo("테스트 퍼센트 할인");
		assertThat(discountDetail.type()).isEqualTo(DiscountType.PERCENTAGE);
		assertThat(discountDetail.value()).isEqualTo(10);
	}

	@Test
	void 나에게_적용된_할인_상세정보() {
		//given
		DiscountCreateRequest request = new DiscountCreateRequest(
			"테스트 퍼센트 할인",
			DiscountType.PERCENTAGE,
			10
		);
		discountService.create(silverMembership.getId(), request);
		var savedDiscount = discountRepository.findAll().getFirst();

		//when
		var discountDetail = discountService.getMyDiscount(user.getId());

		//then
		assertThat(discountDetail.userLoginId()).isEqualTo(user.getLoginId());
		assertThat(discountDetail.userName()).isEqualTo(user.getName());
		assertThat(discountDetail.membershipLevel()).isEqualTo(user.getMembership().getLevel());
		assertThat(discountDetail.discountType()).isEqualTo(DiscountType.PERCENTAGE);
		assertThat(discountDetail.discountName()).isEqualTo(savedDiscount.getName());
	}

	@Test
	void 상품_구매시_퍼센트_할인_적용() {
		//given
		DiscountCreateRequest request = new DiscountCreateRequest(
			"테스트 퍼센트 할인",
			DiscountType.PERCENTAGE,
			10
		);
		discountService.create(silverMembership.getId(), request);

		Discount savedDiscount = discountRepository.findByMembershipId(user.getMembership().getId())
			.orElseThrow();

		Long ProductPrice = product.getPrice();

		Long finalPrice = savedDiscount.calcDiscountFinalPrice(ProductPrice);
		Long discountAmount = savedDiscount.calcDiscountAmount(ProductPrice);
		System.out.println("finalPrice = " + finalPrice);
		System.out.println("discountAmount = " + discountAmount);

		//when then
		assertThat(finalPrice).isEqualTo(26910L);
		assertThat(discountAmount).isEqualTo(2990L);
	}

}
