package com.kt.service.payment;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.discount.Discount;
import com.kt.domain.discount.DiscountTargetType;
import com.kt.domain.discount.DiscountType;
import com.kt.domain.discountMembership.DiscountMembership;
import com.kt.domain.membership.Membership;
import com.kt.domain.order.Order;
import com.kt.domain.order.OrderStatus;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.payment.Payment;
import com.kt.domain.paymenttype.PaymentType;
import com.kt.domain.product.Product;
import com.kt.domain.user.Gender;
import com.kt.domain.user.User;
import com.kt.dto.discount.request.DiscountCreateRequest;
import com.kt.dto.payment.PaymentCreateRequest;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.discountmembership.DiscountMembershipRepository;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.payment.PaymentRepository;
import com.kt.repository.paymenttype.PaymentTypeRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;

import jakarta.persistence.EntityManager;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class PaymentServiceTest {

	@Autowired
	PaymentService paymentService;
	@Autowired
	PaymentRepository paymentRepository;
	@Autowired
	OrderRepository orderRepository;
	@Autowired
	OrderProductRepository orderProductRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	PaymentTypeRepository paymentTypeRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	MembershipRepository membershipRepository;
	@Autowired
	DiscountRepository discountRepository;
	@Autowired
	DiscountMembershipRepository discountMembershipRepository;
	@Autowired
	EntityManager entityManager;

	User user;
	Order order;
	PaymentType paymentType;
	Membership membership;

	@BeforeEach
	void setUp() {
		membership = membershipRepository.saveAndFlush(new Membership("SILVER"));

		user = userRepository.saveAndFlush(
			User.normalUser(
				"payuser",
				"password123!",
				"결제유저",
				"pay@test.com",
				"01012341234",
				Gender.MALE,
				LocalDate.of(1990, 1, 1),
				membership
			)
		);

		Product product = productRepository.saveAndFlush(
			new Product("테스트 상품", "설명", 10000L, 100L, null)
		);

		order = orderRepository.saveAndFlush(
			new Order("수령인", "01099998888", "서울시 강남구", user)
		);

		orderProductRepository.saveAndFlush(
			new OrderProduct(2L, 1L, product, order)
		);

		paymentType = paymentTypeRepository.saveAndFlush(new PaymentType("CARD"));

		/*영속성 문제로 임시적으로 오류 해결*/
		entityManager.flush();
		entityManager.clear();
	}

	@Test
	void 주문에_대한_결제_생성_가능() {
		paymentService.create(결제요청(), user.getId());

		Payment payment = paymentRepository.findAll().get(0);

		assertThat(payment.getTotalPrice()).isEqualTo(20000);
		assertThat(payment.getDeliveryFee()).isEqualTo(3000);
		assertThat(payment.getFinalPrice()).isEqualTo(23000);

		Order updatedOrder = orderRepository.findById(order.getId()).get();
		assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.SHIPPED);
	}

	@Test
	void 멤버십_할인_적용_결제_가능() {
		Discount membershipDiscount = discountRepository.saveAndFlush(
			new Discount(
				"멤버십 할인",
				DiscountTargetType.MEMBERSHIP,
				DiscountType.PERCENTAGE,
				true,
				10L
			)
		);
		discountMembershipRepository.saveAndFlush(
			new DiscountMembership(membershipDiscount, membership)
		);

		paymentService.create(결제요청(), user.getId());

		Payment payment = paymentRepository.findAll().get(0);

		assertThat(payment.getTotalPrice()).isEqualTo(18000);
		assertThat(payment.getFinalPrice()).isEqualTo(21000);
	}

	private PaymentCreateRequest 결제요청() {
		return new PaymentCreateRequest(order.getId(), paymentType.getId());
	}
}
