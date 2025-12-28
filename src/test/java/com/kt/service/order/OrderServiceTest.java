package com.kt.service.order;

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
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.common.request.Paging;
import com.kt.domain.category.Category;
import com.kt.domain.membership.Membership;
import com.kt.domain.order.OrderStatus;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.product.Product;
import com.kt.domain.shoppingaddress.ShoppingAddress;
import com.kt.domain.shoppingaddress.ShoppingAddressType;
import com.kt.domain.user.Gender;
import com.kt.domain.user.User;
import com.kt.domain.variant.Variant;
import com.kt.domain.variant.VariantType;
import com.kt.dto.order.OrderCreateRequest;
import com.kt.dto.order.OrderProductRequest;
import com.kt.dto.order.OrderStatusUpdateRequest;
import com.kt.dto.order.OrderUpdateRequest;
import com.kt.dto.order.response.OrderDetailResponse;
import com.kt.dto.order.response.OrderListResponse;
import com.kt.dto.order.response.OrderProductResponse;
import com.kt.repository.category.CategoryRepository;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.orderproduct.OrderProductRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.shoppingaddress.ShoppingAddressRepository;
import com.kt.repository.user.UserRepository;
import com.kt.repository.variant.VariantRepository;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class OrderServiceTest {
	@Autowired
	OrderService orderService;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	MembershipRepository membershipRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	VariantRepository variantRepository;

	@Autowired
	ShoppingAddressRepository shoppingAddressRepository;

	@Autowired
	OrderProductRepository orderProductRepository;

	private User user;
	private Product savedProduct;
	private Variant savedVariant;
	private ShoppingAddress savedAddress;
	private OrderProduct orderProduct;

	@BeforeEach
	void setUp() {
		initOrderTestData();
	}

	void initOrderTestData() {
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

		var category = categoryRepository.saveAndFlush(new Category("상의"));

		savedProduct = productRepository.saveAndFlush(new Product("오버핏 맨투맨", "맨투맨", 10000L, 10L, category));

		savedVariant = variantRepository.saveAndFlush(new Variant(VariantType.COLOR, "black", savedProduct));

		savedAddress = shoppingAddressRepository.saveAndFlush(
			new ShoppingAddress(
				user,
				user.getName(),
				"서울시 강남구 테헤란로 123",
				user.getMobile(),
				ShoppingAddressType.DOOR,
				"부재 시 경비실에 맡겨주세요",
				true
			)
		);
	}

	@Test
	void 주문_생성_성공() {
		//given
		OrderProductRequest productRequest = new OrderProductRequest(
			savedProduct.getId(),
			1L,
			savedVariant.getId()
		);

		OrderCreateRequest request = new OrderCreateRequest(user.getName(), user.getMobile(),
			savedAddress.getId(), List.of(productRequest));

		//when
		Long orderId = orderService.create(user.getId(), request);

		//then
		var savedOrder = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		var orderproducts = orderProductRepository.findByOrderId(orderId);

		assertThat(savedOrder.getReceiverName()).isEqualTo(request.receiverName());
		assertThat(savedOrder.getReceiverPhone()).isEqualTo(request.receiverPhone());
		assertThat(savedOrder.getUser().getId()).isEqualTo(user.getId());
		assertThat(savedOrder.getReceiverAddress()).isEqualTo(savedAddress.getAddress());
		assertThat(orderproducts.get(0).getCount()).isEqualTo(1);
		assertThat(orderproducts).hasSize(1);
	}

	@Test
	void 주문_생성_실패__옵션없음() {
		//given
		Long notExistVariantId = 999L;

		OrderProductRequest productRequest = new OrderProductRequest(
			savedProduct.getId(),
			1L,
			notExistVariantId
		);

		OrderCreateRequest request = new OrderCreateRequest(user.getName(), user.getMobile(),
			savedAddress.getId(), List.of(productRequest));

		//when & then
		assertThatThrownBy(() -> orderService.create(user.getId(), request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_VARIANT.getMessage());
	}

	@Test
	void 주문_생성_실패__상품_비활성화() {
		//given
		savedProduct.updateInActive();
		productRepository.saveAndFlush(savedProduct);

		OrderProductRequest productRequest = new OrderProductRequest(
			savedProduct.getId(),
			1L,
			savedVariant.getId()
		);

		OrderCreateRequest request = new OrderCreateRequest(user.getName(), user.getMobile(),
			savedAddress.getId(), List.of(productRequest));

		//when & then
		assertThatThrownBy(() -> orderService.create(user.getId(), request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.CANNOT_PURCHASE_PRODUCT.getMessage());
	}

	@Test
	void 주문_생성_실패__재고부족() {
		//given
		OrderProductRequest productRequest = new OrderProductRequest(
			savedProduct.getId(),
			11L,
			savedVariant.getId()
		);

		OrderCreateRequest request = new OrderCreateRequest(user.getName(), user.getMobile(),
			savedAddress.getId(), List.of(productRequest));

		//when & then
		assertThatThrownBy(() -> orderService.create(user.getId(), request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_ENOUGH_STOCK.getMessage());
	}

	@Test
	void 주문_목록_조회_성공() {
		//given
		OrderProductRequest productRequest = new OrderProductRequest(
			savedProduct.getId(),
			1L,
			savedVariant.getId()
		);
		OrderProductRequest anotherProductRequest = new OrderProductRequest(
			savedProduct.getId(),
			2L,
			savedVariant.getId()
		);

		OrderCreateRequest request = new OrderCreateRequest(user.getName(), user.getMobile(),
			savedAddress.getId(), List.of(productRequest));
		OrderCreateRequest anotherRequest = new OrderCreateRequest(user.getName(), user.getMobile(),
			savedAddress.getId(), List.of(anotherProductRequest));

		Long orderId = orderService.create(user.getId(), request);
		Long anotherOrderId = orderService.create(user.getId(), anotherRequest);

		Paging paging = new Paging(1, 10);

		//when
		Page<OrderListResponse> orders = orderService.getOrderList(user.getId(), paging);

		//then
		assertThat(orders.getContent()).hasSize(2);
		assertThat(orders.getContent())
			.extracting(OrderListResponse::id)
			.containsExactlyInAnyOrder(orderId, anotherOrderId);
	}

	@Test
	void 주문_목록_조회_빈_리스트_반환() {
		//given
		Paging paging = new Paging(1, 10);

		//when
		Page<OrderListResponse> orders = orderService.getOrderList(user.getId(), paging);

		//then
		assertThat(orders).isEmpty();
	}

	@Test
	void 주문_상세_조회_성공() {
		//given
		OrderProductRequest productRequest = new OrderProductRequest(
			savedProduct.getId(),
			1L,
			savedVariant.getId()
		);

		OrderCreateRequest request = new OrderCreateRequest(user.getName(), user.getMobile(),
			savedAddress.getId(), List.of(productRequest));

		//when
		Long orderId = orderService.create(user.getId(), request);
		OrderDetailResponse orderDetail = orderService.getOrderDetail(user.getId(), orderId);

		//then
		assertThat(orderDetail.orderStatus()).isEqualTo(OrderStatus.ORDERED);
		assertThat(orderDetail.receiverName()).isEqualTo(user.getName());
		assertThat(orderDetail.receiverPhone()).isEqualTo(user.getMobile());
		assertThat(orderDetail.receiverAddress()).isEqualTo(savedAddress.getAddress());
		assertThat(orderDetail.orderedAt()).isNotNull();
		assertThat(orderDetail.products()).hasSize(1);

		OrderProductResponse product = orderDetail.products().getFirst();

		assertThat(product.productCount()).isEqualTo(productRequest.productCount());
		assertThat(product.productId()).isEqualTo(savedProduct.getId());
		assertThat(product.productVariantId()).isEqualTo(savedVariant.getId());
	}

	@Test
	void 주문_상태_변경_성공() {
		//given
		OrderProductRequest productRequest = new OrderProductRequest(
			savedProduct.getId(),
			1L,
			savedVariant.getId()
		);

		OrderCreateRequest request = new OrderCreateRequest(user.getName(), user.getMobile(),
			savedAddress.getId(), List.of(productRequest));

		Long orderId = orderService.create(user.getId(), request);

		// 상태 변경 체크를 위해 임의로 DB에 PAID로 변경
		orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER)
			.updateStatus(OrderStatus.PAID);
		orderRepository.flush();

		//when
		orderService.updateStatus(new OrderStatusUpdateRequest(OrderStatus.PROCESSING), orderId);

		//then
		var savedOrder = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);

		assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PROCESSING);
	}

	@Test
	void 주문_상태_변경_실패__잘못된상태() {
		//given
		OrderProductRequest productRequest = new OrderProductRequest(
			savedProduct.getId(),
			1L,
			savedVariant.getId()
		);

		OrderCreateRequest request = new OrderCreateRequest(user.getName(), user.getMobile(),
			savedAddress.getId(), List.of(productRequest));

		Long orderId = orderService.create(user.getId(), request);

		//when & then
		assertThatThrownBy(() -> orderService.updateStatus(new OrderStatusUpdateRequest(OrderStatus.PROCESSING), orderId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.CANNOT_UPDATE_ORDER_STATUS.getMessage());
	}

	@Test
	void 주문_취소_성공() {
		//given
		Long orderId = orderService.create(user.getId(),
			new OrderCreateRequest(
				user.getName(), user.getMobile(),
				savedAddress.getId(),
				List.of(new OrderProductRequest(savedProduct.getId(), 1L, savedVariant.getId()))
			)
		);

		//when
		orderService.cancel(orderId, user.getId());

		//then
		var savedOrder = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);
		assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
	}

	@Test
	void 주문_취소_실패__본인아님() {
		//given
		Long orderId = orderService.create(user.getId(),
			new OrderCreateRequest(
				user.getName(), user.getMobile(),
				savedAddress.getId(),
				List.of(new OrderProductRequest(savedProduct.getId(), 1L, savedVariant.getId()))
			)
		);

		Long wrongUserId = 999L;

		//when & then
		assertThatThrownBy(() -> orderService.cancel(orderId, wrongUserId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_ORDER_OWNER.getMessage());
	}

	@Test
	void 주문_수정_성공() {
		//given
		Long orderId = orderService.create(user.getId(),
			new OrderCreateRequest(
				user.getName(), user.getMobile(),
				savedAddress.getId(),
				List.of(new OrderProductRequest(savedProduct.getId(), 1L, savedVariant.getId()))
			)
		);

		//when
		orderService.update(new OrderUpdateRequest("이테크", "010-1111-2222", savedAddress.getId()),
			orderId, user.getId());

		//then
		var savedOrder = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);
		assertThat(savedOrder.getReceiverName()).isEqualTo("이테크");
		assertThat(savedOrder.getReceiverPhone()).isEqualTo("010-1111-2222");
	}

	@Test
	void 주문_환불_요청_성공() {
		//given
		Long orderId = orderService.create(user.getId(),
			new OrderCreateRequest(
				user.getName(), user.getMobile(),
				savedAddress.getId(),
				List.of(new OrderProductRequest(savedProduct.getId(), 1L, savedVariant.getId()))
			)
		);

		// 상태 변경 체크를 위해 임의로 DB에 PAID로 변경
		orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER)
			.updateStatus(OrderStatus.PAID);
		orderRepository.flush();

		//when
		orderService.requestRefund(orderId, user.getId());

		//then
		var savedOrder = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);
		assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.REFUND_REQUESTED);
	}

	@Test
	void 주문_환불_승인_성공() {
		//given
		Long orderId = orderService.create(user.getId(),
			new OrderCreateRequest(
				user.getName(), user.getMobile(),
				savedAddress.getId(),
				List.of(new OrderProductRequest(savedProduct.getId(), 1L, savedVariant.getId()))
			)
		);

		// 상태 변경 체크를 위해 임의로 DB에 REFUND_REQUESTED로 변경
		orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER)
			.updateStatus(OrderStatus.REFUND_REQUESTED);
		orderRepository.flush();

		//when
		orderService.approveRefund(orderId);

		//then
		var savedOrder = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);
		assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.REFUNDED);
	}

	@Test
	void 관리자_주문_취소_성공() {
		//given
		Long orderId = orderService.create(user.getId(),
			new OrderCreateRequest(
				user.getName(), user.getMobile(),
				savedAddress.getId(),
				List.of(new OrderProductRequest(savedProduct.getId(), 1L, savedVariant.getId()))
			)
		);

		//when
		orderService.cancelByAdmin(orderId);

		//then
		var savedOrder = orderRepository.findByIdOrThrow(orderId, ErrorCode.NOT_FOUND_ORDER);
		assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCELLED);
	}
}