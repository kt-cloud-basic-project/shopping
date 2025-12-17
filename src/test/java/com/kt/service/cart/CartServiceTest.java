package com.kt.service.cart;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.common.request.Paging;
import com.kt.domain.cart.Cart;
import com.kt.domain.category.Category;
import com.kt.domain.membership.Membership;
import com.kt.domain.product.Product;
import com.kt.domain.user.Gender;
import com.kt.domain.user.User;
import com.kt.domain.variant.Variant;
import com.kt.domain.variant.VariantType;
import com.kt.dto.cart.CartCreateRequest;
import com.kt.dto.cart.response.CartResponse;
import com.kt.repository.cart.CartRepository;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;
import com.kt.repository.variant.VariantRepository;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CartServiceTest {
	@Autowired
	CartRepository cartRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	VariantRepository variantRepository;

	@Autowired
	DiscountRepository discountRepository;

	@Autowired
	MembershipRepository membershipRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	CartService cartService;

	private User user;
	private User anotherUser;
	private Product savedProduct;
	private Variant savedVariant;

	@BeforeEach
	void setUp() {
		cartRepository.deleteAll();
		variantRepository.deleteAll();
		productRepository.deleteAll();
		userRepository.deleteAll();
		discountRepository.deleteAll();
		membershipRepository.deleteAll();
		categoryRepository.deleteAll();

		initCartTestData();
	}

	void initCartTestData() {
		Membership membership = new Membership("BRONZE");
		membershipRepository.save(membership);

		// 로그인 테스트용 유저 저장
		user = userRepository.saveAndFlush(User.normalUser(
			"login_user02",
			passwordEncoder.encode("password1234"),
			"김유저",
			"login@kttech.com",
			"010-1234-5678",
			Gender.FEMALE,
			LocalDate.of(1999, 1, 10),
			membership
		));

		anotherUser = userRepository.saveAndFlush(User.normalUser(
			"login_user03",
			passwordEncoder.encode("password1122"),
			"이테크",
			"loginid@kt.com",
			"010-1000-5588",
			Gender.MALE,
			LocalDate.of(2000, 5, 15),
			membership
		));

		var category = categoryRepository.saveAndFlush(new Category("상의"));

		savedProduct = productRepository.saveAndFlush(new Product("오버핏 맨투맨", "맨투맨", 10000L, 10L, category));

		savedVariant = variantRepository.saveAndFlush(new Variant(VariantType.COLOR, "black", savedProduct));
	}

	@Test
	void 장바구니_생성_성공() {
		//given
		CartCreateRequest request = new CartCreateRequest(
			1,
			savedVariant.getId(),
			savedProduct.getId()
		);

		//when
		var cartId = cartService.create(user.getId(), request);

		//then
		var savedCart = cartRepository.findByIdOrThrow(cartId, ErrorCode.NOT_FOUND_CART);

		assertThat(savedCart.getProductCount()).isEqualTo(1);
		assertThat(savedCart.getVariantId()).isEqualTo(savedVariant.getId());
		assertThat(savedCart.getProduct().getId()).isEqualTo(savedProduct.getId());
	}

	@Test
	void 장바구니_생성_실패__재고부족() {
		//given
		CartCreateRequest request = new CartCreateRequest(
			11,
			savedVariant.getId(),
			savedProduct.getId()
		);

		//when & then
		assertThatThrownBy(() -> cartService.create(user.getId(), request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_ENOUGH_STOCK.getMessage());
	}

	@Test
	void 장바구니_생성_실패__옵션없음() {
		//given
		Long notExistVariantId = 999L;

		CartCreateRequest request = new CartCreateRequest(
			1,
			notExistVariantId,
			savedProduct.getId()
		);

		//when & then
		assertThatThrownBy(() -> cartService.create(user.getId(), request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.DELETED_VARIANT.getMessage());
	}

	@Test
	void 장바구니_조회_성공() {
		//given
		cartService.create(user.getId(), new CartCreateRequest(2, savedVariant.getId(), savedProduct.getId()));
		cartService.create(user.getId(), new CartCreateRequest(3, savedVariant.getId(), savedProduct.getId()));

		//when
		List<Cart> carts = cartRepository.findByUserId(user.getId());

		//then
		assertThat(carts).hasSize(2);
		carts.forEach(cart ->
			assertThat(cart.getUser().getId()).isEqualTo(user.getId())
		);
	}

	@Test
	void 장바구니_조회_빈리스트_반환() {
		//given
		Paging paging = new Paging(1, 10);

		//when
		Page<CartResponse> cartList = cartService.getCartList(anotherUser.getId(), paging);

		//then
		assertThat(cartList).isEmpty();
	}

	@Test
	void 장바구니_수량_변경_성공() {
		//given
		CartCreateRequest request = new CartCreateRequest(1, savedVariant.getId(), savedProduct.getId());
		Long cartId = cartService.create(user.getId(), request);

		Integer updatedQuantity = 2;

		//when
		cartService.updateQuantity(cartId, user.getId(), updatedQuantity);

		//then
		var savedCart = cartRepository.findByIdOrThrow(cartId, ErrorCode.NOT_FOUND_CART);

		assertThat(savedCart.getProductCount()).isEqualTo(updatedQuantity);
	}

	@Test
	void 장바구니_수량_변경_실패__재고부족() {
		//given
		CartCreateRequest request = new CartCreateRequest(1, savedVariant.getId(), savedProduct.getId());
		Long cartId = cartService.create(user.getId(), request);

		Integer overStockQuantity = 11;

		//when & then
		assertThatThrownBy(() -> cartService.updateQuantity(cartId, user.getId(), overStockQuantity))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_ENOUGH_STOCK.getMessage());
	}

	@Test
	void 장바구니_수량_변경_실패__본인아님() {
		//given
		CartCreateRequest request = new CartCreateRequest(1, savedVariant.getId(), savedProduct.getId());
		Long cartId = cartService.create(user.getId(), request);

		Integer updatedQuantity = 2;
		
		//when & then
		assertThatThrownBy(() -> cartService.updateQuantity(cartId, anotherUser.getId(), updatedQuantity))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_CART_OWNER.getMessage());
	}

	@Test
	void 장바구니_단일_삭제_성공() {
		//given
		CartCreateRequest request = new CartCreateRequest(1, savedVariant.getId(), savedProduct.getId());
		Long cartId = cartService.create(user.getId(), request);

		//when
		cartService.deleteCartItem(cartId);

		//then
		assertThat(cartRepository.existsById(cartId)).isFalse();
	}

	@Test
	void 장바구니_단일_삭제_실패__존재하지않는_장바구니() {
		//given
		Long nonExistedCartId = 999L;

		//when & then
		assertThatThrownBy(() -> cartService.deleteCartItem(nonExistedCartId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_CART.getMessage());
	}

	@Test
	void 장바구니_전체_삭제_성공() {
		//given
		cartService.create(user.getId(), new CartCreateRequest(2, savedVariant.getId(), savedProduct.getId()));
		cartService.create(user.getId(), new CartCreateRequest(3, savedVariant.getId(), savedProduct.getId()));

		//when
		cartService.clearCart(user.getId());

		//then
		List<Cart> carts = cartRepository.findByUserId(user.getId());
		assertThat(carts).isEmpty();
	}
}