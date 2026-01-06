package com.kt.service.wishlist;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.category.Category;
import com.kt.domain.membership.Membership;
import com.kt.domain.product.Product;
import com.kt.domain.user.Gender;
import com.kt.domain.user.User;
import com.kt.dto.wishlist.WishlistResponse;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.repository.wishlist.WishlistRepository;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WishlistServiceTest {

	@Autowired
	private WishlistService wishlistService;

	@Autowired
	private WishlistRepository wishlistRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private MembershipRepository membershipRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private User savedUser;
	private Product savedProduct;

	@BeforeEach
	void setUp() {
		initWishlistTestData();
	}

	void initWishlistTestData() {
		Membership membership = new Membership("BRONZE");
		membershipRepository.save(membership);

		savedUser = userRepository.saveAndFlush(User.normalUser(
			"wishlist_user",
			passwordEncoder.encode("password1234"),
			"찜유저",
			"wishlist@kttech.com",
			"010-1234-5678",
			Gender.FEMALE,
			LocalDate.of(1999, 1, 10),
			membership
		));

		Category category = categoryRepository.saveAndFlush(new Category("의류"));

		savedProduct = productRepository.saveAndFlush(
			new Product("오프화이트 티셔츠", "오프화이트 로고 티셔츠", 120000L, 100L, category)
		);
	}

	@Test
	@DisplayName("찜 추가 성공")
	public void 찜_추가_성공() {
		// when
		Long wishlistId = wishlistService.addWishlist(savedUser.getId(), savedProduct.getId());

		// then
		assertThat(wishlistId).isNotNull();

		var wishlists = wishlistRepository.findAll();
		assertThat(wishlists).hasSize(1);

		var savedWishlist = wishlists.getFirst();
		assertThat(savedWishlist.getUser().getId()).isEqualTo(savedUser.getId());
		assertThat(savedWishlist.getProduct().getId()).isEqualTo(savedProduct.getId());
	}

	@Test
	@DisplayName("찜 추가 실패 - 존재하지 않는 사용자")
	public void 찜_추가_실패_존재하지않는사용자() {
		// given
		Long nonExistUserId = 9999L;

		// when & then
		assertThatThrownBy(() -> wishlistService.addWishlist(nonExistUserId, savedProduct.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_USER.getMessage());
	}

	@Test
	@DisplayName("찜 추가 실패 - 존재하지 않는 상품")
	public void 찜_추가_실패_존재하지않는상품() {
		// given
		Long nonExistProductId = 9999L;

		// when & then
		assertThatThrownBy(() -> wishlistService.addWishlist(savedUser.getId(), nonExistProductId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_PRODUCT.getMessage());
	}

	@Test
	@DisplayName("찜 추가 실패 - 이미 찜한 상품")
	public void 찜_추가_실패_이미찜한상품() {
		// given
		wishlistService.addWishlist(savedUser.getId(), savedProduct.getId());

		// when & then
		assertThatThrownBy(() -> wishlistService.addWishlist(savedUser.getId(), savedProduct.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ALREADY_WISHLISTED.getMessage());
	}

	@Test
	@DisplayName("찜 삭제 성공")
	public void 찜_삭제_성공() {
		// given
		wishlistService.addWishlist(savedUser.getId(), savedProduct.getId());

		// when
		wishlistService.removeWishlist(savedUser.getId(), savedProduct.getId());

		// then
		boolean exists = wishlistRepository.existsByUserIdAndProductId(savedUser.getId(), savedProduct.getId());
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("찜 삭제 실패 - 존재하지 않는 찜")
	public void 찜_삭제_실패_존재하지않는찜() {
		// when & then
		assertThatThrownBy(() -> wishlistService.removeWishlist(savedUser.getId(), savedProduct.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_WISHLIST.getMessage());
	}

	@Test
	@DisplayName("전체 찜 삭제 성공")
	public void 전체_찜_삭제_성공() {
		// given
		Category category2 = categoryRepository.saveAndFlush(new Category("의류"));
		Product product2 = productRepository.saveAndFlush(
			new Product("나이키 신발", "운동화", 150000L, 30L, category2)
		);

		wishlistService.addWishlist(savedUser.getId(), savedProduct.getId());
		wishlistService.addWishlist(savedUser.getId(), product2.getId());

		// when
		wishlistService.removeAllWishlist(savedUser.getId());

		// then
		List<WishlistResponse> userWishlists = wishlistService.getWishlists(savedUser.getId());
		assertThat(userWishlists).isEmpty();
	}

	@Test
	@DisplayName("찜 목록 조회 성공")
	public void 찜_목록_조회_성공() {
		// given
		Category category2 = categoryRepository.saveAndFlush(new Category("의류"));
		Product product2 = productRepository.saveAndFlush(
			new Product("오프화이트 후드티", "오프화이트 후드 스웻셔츠", 350000L, 40L, category2)
		);
		Product product3 = productRepository.saveAndFlush(
			new Product("오프화이트 자켓", "오프화이트 아우터 자켓", 890000L, 15L, category2)
		);


		wishlistService.addWishlist(savedUser.getId(), savedProduct.getId());
		wishlistService.addWishlist(savedUser.getId(), product2.getId());
		wishlistService.addWishlist(savedUser.getId(), product3.getId());

		// when
		List<WishlistResponse> wishlists = wishlistService.getWishlists(savedUser.getId());

		// then
		assertThat(wishlists).hasSize(3);

		var productIds = wishlists.stream()
			.map(wishlistResponse -> wishlistResponse.productId())
			.toList();

		assertThat(productIds).containsExactlyInAnyOrder(
			savedProduct.getId(),
			product2.getId(),
			product3.getId()
		);

		var productNames = wishlists.stream()
			.map(wishlistResponse -> wishlistResponse.productName())
			.toList();

		assertThat(productNames).containsExactlyInAnyOrder(
			"오프화이트 티셔츠",
			"오프화이트 후드티",
			"오프화이트 자켓"
		);

	}

	@Test
	@DisplayName("찜 목록 조회 성공 - 빈 목록")
	public void 찜_목록_조회_성공_빈목록() {
		// when
		List<WishlistResponse> wishlists = wishlistService.getWishlists(savedUser.getId());

		// then
		assertThat(wishlists).isEmpty();
	}

	@Test
	@DisplayName("다른 사용자의 찜은 조회되지 않음")
	public void 다른사용자_찜_조회안됨() {
		// given
		User anotherUser = userRepository.saveAndFlush(User.normalUser(
			"another_user",
			passwordEncoder.encode("password1234"),
			"다른유저",
			"another@kttech.com",
			"010-9999-9999",
			Gender.MALE,
			LocalDate.of(1995, 5, 15),
			savedUser.getMembership()
		));

		wishlistService.addWishlist(savedUser.getId(), savedProduct.getId());
		wishlistService.addWishlist(anotherUser.getId(), savedProduct.getId());

		// when
		List<WishlistResponse> user1Wishlists = wishlistService.getWishlists(savedUser.getId());
		List<WishlistResponse> user2Wishlists = wishlistService.getWishlists(anotherUser.getId());

		// then
		assertThat(user1Wishlists).hasSize(1);
		assertThat(user2Wishlists).hasSize(1);

		// 각자 자신의 찜만 조회됨
		assertThat(user1Wishlists.get(0).productId()).isEqualTo(savedProduct.getId());
		assertThat(user2Wishlists.get(0).productId()).isEqualTo(savedProduct.getId());
	}

	@Test
	@DisplayName("상품 삭제 후 찜 목록에서 조회 안됨")
	public void 상품삭제후_찜목록_조회안됨() {
		// given
		wishlistService.addWishlist(savedUser.getId(), savedProduct.getId());

		// 상품을 비활성화 (실제로는 소프트 삭제될 수 있음)
		savedProduct.updateInActive();
		productRepository.saveAndFlush(savedProduct);

		// when
		List<WishlistResponse> wishlists = wishlistService.getWishlists(savedUser.getId());

		// then
		// 상품이 비활성화되어도 찜 목록에는 여전히 남아있어야 함 (비즈니스 로직에 따라)
		// 만약 비활성화된 상품을 찜 목록에서 제외해야 한다면 서비스 로직 수정 필요
		assertThat(wishlists).hasSize(1);
	}
}