package com.kt.service.review;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.category.Category;
import com.kt.domain.membership.Membership;
import com.kt.domain.order.Order;
import com.kt.domain.order.OrderStatus;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.product.Product;
import com.kt.domain.user.Gender;
import com.kt.domain.user.User;
import com.kt.dto.review.ReviewCreateRequest;
import com.kt.dto.review.ReviewUpdateRequest;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.review.ReviewRepository;
import com.kt.repository.user.UserRepository;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReviewServiceTest {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderProductRepository orderProductRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private MembershipRepository membershipRepository;

	@Autowired
	private DiscountRepository discountRepository;

	private User user;
	private User anotherUser;
	private Order order;
	private OrderProduct orderProduct;

	@BeforeEach
	void setUp() {
		reviewRepository.deleteAll();
		orderProductRepository.deleteAll();
		orderRepository.deleteAll();
		productRepository.deleteAll();
		categoryRepository.deleteAll();
		userRepository.deleteAll();
		discountRepository.deleteAll();
		membershipRepository.deleteAll();

		Membership membership = membershipRepository.saveAndFlush(new Membership("SILVER"));

		user = userRepository.saveAndFlush(User.normalUser(
			"testuser",
			"password123!",
			"테스트유저",
			"test@test.com",
			"01012345678",
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			membership
		));

		anotherUser = userRepository.saveAndFlush(User.normalUser(
			"anotheruser",
			"password123!",
			"다른유저",
			"another@test.com",
			"010-1234-5678",
			Gender.FEMALE,
			LocalDate.of(1995, 5, 5),
			membership
		));


		Category category = categoryRepository.saveAndFlush(new Category("TOP"));

		Product product = productRepository.saveAndFlush(new Product("테스트 상품",
			"상품 설명",
			10000L,
			100L,
			category));

		order = orderRepository.saveAndFlush(new Order(
			"수령인",
			"01087654321",
			"서울시 강남구",
			user
		));

		orderProduct = orderProductRepository.saveAndFlush(new OrderProduct(
			2L,
			1L,
			product,
			order
		));
	}

	@Test
	void 주문_상품_리뷰_작성_가능() {

		//given
		order.updateStatus(OrderStatus.DELIVERED);
		orderRepository.saveAndFlush(order);  // DB에 반영

		ReviewCreateRequest request = new ReviewCreateRequest(
			"최고의 상품입니다",
			"정말 만족스러운 구매였습니다",
			5
		);

		//when
		reviewService.create(user.getId(), orderProduct.getId(), request);

		//then
		var reviews = reviewRepository.findAll();
		assertThat(reviews).hasSize(1);

		var createReview = reviews.getFirst();
		assertThat(createReview.getTitle()).isEqualTo("최고의 상품입니다");
		assertThat(createReview.getDescription()).isEqualTo("정말 만족스러운 구매였습니다");
		assertThat(createReview.getStar()).isEqualTo(5);
		assertThat(createReview.getUser().getId()).isEqualTo(user.getId());
		assertThat(createReview.getOrderProduct().getId()).isEqualTo(orderProduct.getId());
	}

	@Test
	void 주문상품_없으면_리뷰_작성_불가() {
		//given
		Long orderProductId = 999999999L;

		ReviewCreateRequest request = new ReviewCreateRequest(
			"최고의 상품입니다",
			"정말 만족스러운 구매였습니다",
			5
		);

		assertThatThrownBy(() -> {
			reviewService.create(user.getId(), orderProductId, request);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_ORDER_PRODUCT.getMessage());

	}

	@Test
	void 본인_주문_아닐경우_리뷰_작성_불가() {

		order.updateStatus(OrderStatus.DELIVERED);
		orderRepository.save(order);

		ReviewCreateRequest request = new ReviewCreateRequest(
			"최고의 상품입니다",
			"정말 만족스러운 구매였습니다",
			5
		);

		//when
		assertThatThrownBy(() -> {
			reviewService.create(anotherUser.getId(), orderProduct.getId(), request);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_ORDER_USER.getMessage());

		//then
		var reviews = reviewRepository.findAll();
		assertThat(reviews).isEmpty();
	}

	@Test
	void 배송_완료되지_않은_주문은_리뷰_작성_불가() {

		//given
		orderRepository.saveAndFlush(order);

		ReviewCreateRequest request = new ReviewCreateRequest(
			"최고의 상품입니다",
			"정말 만족스러운 구매였습니다",
			5
		);

		//when
		assertThatThrownBy(() -> {
			reviewService.create(user.getId(), orderProduct.getId(), request);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.DELIVERY_NOT_COMPLETED_ORDER.getMessage());

		//then
		var reviews = reviewRepository.findAll();
		assertThat(reviews).isEmpty();
	}

	@Test
	void 리뷰_작성_기한이_지난_경우_리뷰_작성_불가() {

		//given
		order.updateStatus(OrderStatus.DELIVERED);
		orderRepository.saveAndFlush(order);

		ReflectionTestUtils.setField(order, "deliveredAt", LocalDate.now().minusDays(8));
		orderRepository.saveAndFlush(order);

		ReviewCreateRequest request = new ReviewCreateRequest(
			"최고의 상품입니다",
			"정말 만족스러운 구매였습니다",
			5
		);

		//when
		assertThatThrownBy(() -> {
			reviewService.create(user.getId(), orderProduct.getId(), request);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.REVIEW_WRITE_DATE_EXPIRED.getMessage());

		//then
		var reviews = reviewRepository.findAll();
		assertThat(reviews).isEmpty();
	}

	@Test
	void 중복_리뷰_작성_불가() {

		//given
		order.updateStatus(OrderStatus.DELIVERED);
		orderRepository.saveAndFlush(order);

		ReviewCreateRequest request = new ReviewCreateRequest(
			"최고의 상품입니다",
			"정말 만족스러운 구매였습니다",
			5
		);

		reviewService.create(user.getId(), orderProduct.getId(), request);

		//when
		assertThatThrownBy(() -> {
			reviewService.create(user.getId(), orderProduct.getId(), request);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.DUPLICATE_REVIEW.getMessage());

		//then
		var reviews = reviewRepository.findAll();
		assertThat(reviews).hasSize(1);
	}

	@Test
	void 주문_상품_리뷰_수정_가능() {
		//given
		order.updateStatus(OrderStatus.DELIVERED);
		orderRepository.saveAndFlush(order);

		ReviewCreateRequest createRequest = new ReviewCreateRequest(
			"최고의 상품입니다",
			"정말 만족스러운 구매였습니다",
			5
		);

		reviewService.create(user.getId(), orderProduct.getId(), createRequest);

		var reviews = reviewRepository.findAll();
		var review = reviews.getFirst();

		//when
		reviewService.update(user.getId(), review.getId(), new ReviewUpdateRequest(
			"수정된 리뷰 제목",
			"수정된 리뷰 내용",
			4
		));

		//then
		var updatedReview = reviewRepository.findById(review.getId()).get();
		assertThat(updatedReview.getTitle()).isEqualTo("수정된 리뷰 제목");
		assertThat(updatedReview.getDescription()).isEqualTo("수정된 리뷰 내용");
		assertThat(updatedReview.getStar()).isEqualTo(4);
	}

	@Test
	void 리뷰_없으면_수정_불가() {
		//given
		Long reviewId = 999999999L;

		//when
		assertThatThrownBy(() -> {
			reviewService.update(user.getId(), reviewId, new ReviewUpdateRequest(
				"수정된 리뷰 제목",
				"수정된 리뷰 내용",
				4
			));
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_REVIEW.getMessage());
	}

	@Test
	void 본인_리뷰_아닐경우_수정_불가() {
		//given
		order.updateStatus(OrderStatus.DELIVERED);
		orderRepository.saveAndFlush(order);

		ReviewCreateRequest createRequest = new ReviewCreateRequest(
			"최고의 상품입니다",
			"정말 만족스러운 구매였습니다",
			5
		);

		reviewService.create(user.getId(), orderProduct.getId(), createRequest);

		var reviews = reviewRepository.findAll();
		var review = reviews.getFirst();

		//when
		assertThatThrownBy(() -> {
			reviewService.update(anotherUser.getId(), review.getId(), new ReviewUpdateRequest(
				"다른사람이 수정한 제목",
				"다른사람이 수정한 내용",
				3
			));
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_REVIEW_AUTHOR.getMessage());
	}

	@Test
	void 주문_상품_리뷰_삭제_가능() {
		//given
		order.updateStatus(OrderStatus.DELIVERED);
		orderRepository.saveAndFlush(order);

		ReviewCreateRequest createRequest = new ReviewCreateRequest(
			"최고의 상품입니다",
			"정말 만족스러운 구매였습니다",
			5
		);

		reviewService.create(user.getId(), orderProduct.getId(), createRequest);

		var reviews = reviewRepository.findAll();
		var review = reviews.getFirst();

		//when
		reviewService.delete(user.getId(), review.getId());

		//then
		var deletedReview = reviewRepository.findById(review.getId()).get();
		assertThat(deletedReview.isDeleted()).isTrue();
	}

	@Test
	void 리뷰_없으면_삭제_불가() {
		//given
		Long reviewId = 999999999L;

		//when
		assertThatThrownBy(() -> {
			reviewService.delete(user.getId(), reviewId);
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_REVIEW.getMessage());

	}

	@Test
	void 본인_리뷰_아닐경우_삭제_불가() {
		//given
		order.updateStatus(OrderStatus.DELIVERED);
		orderRepository.saveAndFlush(order);

		ReviewCreateRequest createRequest = new ReviewCreateRequest(
			"최고의 상품입니다",
			"정말 만족스러운 구매였습니다",
			5
		);

		reviewService.create(user.getId(), orderProduct.getId(), createRequest);

		var reviews = reviewRepository.findAll();
		var review = reviews.getFirst();

		//when
		assertThatThrownBy(() -> {
			reviewService.delete(anotherUser.getId(), review.getId());
		}).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_REVIEW_AUTHOR.getMessage());
	}

}
