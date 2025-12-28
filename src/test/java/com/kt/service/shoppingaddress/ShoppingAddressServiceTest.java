package com.kt.service.shoppingaddress;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.membership.Membership;
import com.kt.domain.shoppingaddress.ShoppingAddress;
import com.kt.domain.shoppingaddress.ShoppingAddressType;
import com.kt.domain.user.Gender;
import com.kt.domain.user.User;
import com.kt.dto.shoppingaddress.ShoppingAddressCreateRequest;
import com.kt.dto.shoppingaddress.ShoppingAddressUpdateRequest;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.shoppingaddress.ShoppingAddressRepository;
import com.kt.repository.user.UserRepository;

import jakarta.transaction.Transactional;
import okhttp3.Address;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingAddressServiceTest {

	@Autowired
	private MembershipRepository membershipRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ShoppingAddressRepository shoppingAddressRepository;

	@Autowired
	private ShoppingAddressService shoppingAddressService;

	private User user;
	private User anotherUser;

	@BeforeEach
	void setUp() {
		Membership membership = membershipRepository.saveAndFlush(new Membership("SILVER"));

		user = userRepository.saveAndFlush(User.normalUser(
			"tester",
			"password123!",
			"테스트유저",
			"test@test.com",
			"010-1234-5679",
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			membership
		));

		anotherUser = userRepository.saveAndFlush(User.normalUser(
			"anotherUser",
			"password123!",
			"다른유저",
			"another@test.com",
			"010-1234-5679",
			Gender.MALE,
			LocalDate.of(1990, 1, 1),
			membership
		));

	}

	@Test
	void 배송지_등록_성공() {

		//given
		ShoppingAddressCreateRequest request = new ShoppingAddressCreateRequest(
			"이름",
			"서울특별시 강남구 테헤란로 123",
			"010-1234-5678",
			ShoppingAddressType.DOOR,
			null,
			false
		);

		//when
		shoppingAddressService.create(user.getId(), request);

		//then
		var savedAddress = shoppingAddressRepository.findByUserId(user.getId());
		assertThat(savedAddress).hasSize(1);

		var address = savedAddress.getFirst();
		assertThat(address.getName()).isEqualTo("이름");
		assertThat(address.getAddress()).isEqualTo("서울특별시 강남구 테헤란로 123");
		assertThat(address.getMobile()).isEqualTo("010-1234-5678");
		assertThat(address.getInfoType()).isEqualTo(ShoppingAddressType.DOOR);
		assertThat(address.getUser().getId()).isEqualTo(user.getId());
	}

	@Test
	void 첫번째_배송지는_자동으로_기본_배송지_설정() {
		//given
		ShoppingAddressCreateRequest request = new ShoppingAddressCreateRequest(
			"이름",
			"서울특별시 강남구 테헤란로 123",
			"010-1234-5678",
			ShoppingAddressType.DOOR,
			null,
			false
		);

		//when
		shoppingAddressService.create(user.getId(), request);

		//then
		var savedAddress = shoppingAddressRepository.findByUserId(user.getId());
		assertThat(savedAddress).hasSize(1);

		var address = savedAddress.getFirst();
		assertThat(address.isDefault()).isTrue();
	}

	@Test
	void 기타_배송정보_선택시_상세정보_필수() {
		//given
		ShoppingAddressCreateRequest request = new ShoppingAddressCreateRequest(
			"이름",
			"서울특별시 강남구 테헤란로 123",
			"010-1234-5678",
			ShoppingAddressType.ETC,
			null,
			false
		);

		//when & then
		assertThatThrownBy(() -> shoppingAddressService.create(user.getId(), request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.DELIVERY_INFO_REQUIRED_FOR_CUSTOM.getMessage());
	}

	@Test
	void 등록_배송지가_기본_배송지라면_기존_기본_배송지_해제() {
		//given
		ShoppingAddressCreateRequest firstRequest = new ShoppingAddressCreateRequest(
			"첫번째 주소",
			"서울특별시 강남구 테헤란로 123",
			"010-1234-5678",
			ShoppingAddressType.DOOR,
			null,
			true
		);

		ShoppingAddressCreateRequest secondRequest = new ShoppingAddressCreateRequest(
			"두번째 주소",
			"서울특별시 강남구 테헤란로 456",
			"010-8765-4321",
			ShoppingAddressType.DOOR,
			null,
			true
		);

		//when
		shoppingAddressService.create(user.getId(), firstRequest);
		shoppingAddressService.create(user.getId(), secondRequest);

		//then
		var savedAddresses = shoppingAddressRepository.findByUserId(user.getId());
		assertThat(savedAddresses).hasSize(2);

		var firstAddress = savedAddresses.getFirst();
		var secondAddress = savedAddresses.get(1);

		assertThat(firstAddress.isDefault()).isFalse();
		assertThat(secondAddress.isDefault()).isTrue();
	}

	@Test
	void 등록하려는_주소가_기본_배송지가_아닌데_기존_등록된_주소가_없다면_강제로_기본_배송지_설정() {
		//given
		ShoppingAddressCreateRequest request = new ShoppingAddressCreateRequest(
			"첫번째 주소",
			"서울특별시 강남구 테헤란로 123",
			"010-1234-5678",
			ShoppingAddressType.DOOR,
			null,
			false
		);

		//when
		shoppingAddressService.create(user.getId(), request);

		//then
		var savedAddresses = shoppingAddressRepository.findByUserId(user.getId());
		assertThat(savedAddresses).hasSize(1);

		var firstAddress = savedAddresses.getFirst();

		assertThat(firstAddress.isDefault()).isTrue();
	}

	@Test
	void 배송지_최대_5개() {
		//given
		for (int i = 0; i < 5; i++) {
			ShoppingAddressCreateRequest request = new ShoppingAddressCreateRequest(
				"이름 "+1,
				"서울특별시 강남구 테헤란로 " + i,
				"010-1234-567" + i,
				ShoppingAddressType.DOOR,
				null,
				false
			);

			shoppingAddressService.create(user.getId(), request);
		}

		ShoppingAddressCreateRequest sixthRequest = new ShoppingAddressCreateRequest(
			"이름 6",
			"서울특별시 강남구 테헤란로 6",
			"010-1234-5676",
			ShoppingAddressType.DOOR,
			null,
			false
		);

		//when & then
		assertThatThrownBy(() -> shoppingAddressService.create(user.getId(), sixthRequest))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.EXCEED_MAX_ADDRESS_COUNT.getMessage());
	}

	@Test
	void 내_쇼핑_목록() {
		//given
		for (int i = 0; i < 3; i++) {
			ShoppingAddressCreateRequest request = new ShoppingAddressCreateRequest(
				"이름 " + i,
				"서울특별시 강남구 테헤란로 " + i,
				"010-1234-567" + i,
				ShoppingAddressType.DOOR,
				null,
				false
			);

			shoppingAddressService.create(user.getId(), request);
		}

		//when
		var addressList = shoppingAddressService.getMyShoppingAddress(user.getId());

		//then
		assertThat(addressList).hasSize(3);
		assertThat(addressList)
			.extracting("name", "mobile", "infoType")
			.containsExactlyInAnyOrder(tuple("이름 0", "010-1234-5670", ShoppingAddressType.DOOR),
				tuple("이름 1", "010-1234-5671", ShoppingAddressType.DOOR),
				tuple("이름 2", "010-1234-5672", ShoppingAddressType.DOOR)
			);
	}

	@Test
	void 기본_배송지_변경_성공() {
		//given
		ShoppingAddress firstAddress = new ShoppingAddress(
			user,
			"첫번째 주소",
			"서울특별시 강남구 테헤란로 123",
			"010-1234-5678",
			ShoppingAddressType.DOOR,
			null,
			true
		);

		ShoppingAddress secondAddress = new ShoppingAddress(
			user,
			"두번째 주소",
			"서울특별시 강남구 테헤란로 456",
			"010-8765-4321",
			ShoppingAddressType.DOOR,
			null,
			false
		);

		//when
		shoppingAddressRepository.save(firstAddress);
		var savedSecondAddress = shoppingAddressRepository.save(secondAddress);

		shoppingAddressService.defaultAddress(user.getId(), savedSecondAddress.getId());

		var addressList = shoppingAddressRepository.findByUserId(user.getId());
		var updatedFirstAddress = addressList.getFirst();
		var updatedSecondAddress = addressList.get(1);

		//then
		assertThat(updatedFirstAddress.isDefault()).isFalse();
		assertThat(updatedSecondAddress.isDefault()).isTrue();
	}

	@Test
	void 배송지_수정_성공() {
		//given
		ShoppingAddress create = new ShoppingAddress(
			user,
			"이름",
			"서울특별시 강남구 테헤란로 123",
			"010-1234-5678",
			ShoppingAddressType.DOOR,
			null,
			false
		);

		ShoppingAddressUpdateRequest updateRequest = new ShoppingAddressUpdateRequest(
			"이름 수정",
			"서울특별시 강남구 테헤란로 123 수정",
			"010-1234-5679",
			ShoppingAddressType.ETC,
			"고가 물품입니다 호수 나오게 사진 한번 부탁드립니다",
			true
		);

		//when
		var createdAddress = shoppingAddressRepository.save(create);
		var addressList = shoppingAddressRepository.findByUserId(user.getId());
		shoppingAddressService.update(user.getId(), createdAddress.getId(), updateRequest);
		var updatedAddress = addressList.getFirst();

		//then
		assertThat(updatedAddress.getName()).isEqualTo("이름 수정");
		assertThat(updatedAddress.getAddress()).isEqualTo("서울특별시 강남구 테헤란로 123 수정");
		assertThat(updatedAddress.getMobile()).isEqualTo("010-1234-5679");
		assertThat(updatedAddress.getInfoType()).isEqualTo(ShoppingAddressType.ETC);
		assertThat(updatedAddress.getInfoDesc()).isEqualTo("고가 물품입니다 호수 나오게 사진 한번 부탁드립니다");
		assertThat(updatedAddress.isDefault()).isTrue();
	}

	@Test
	void 배송지_없는경우_수정_실패() {

		var addressId = 999999L;

		//given
		ShoppingAddressUpdateRequest updateRequest = new ShoppingAddressUpdateRequest(
			"이름 수정",
			"서울특별시 강남구 테헤란로 123 수정",
			"010-1234-5679",
			ShoppingAddressType.ETC,
			"고가 물품입니다 호수 나오게 사진 한번 부탁드립니다",
			true
		);

		//when & then
		assertThatThrownBy(() -> shoppingAddressService.update(user.getId(), addressId, updateRequest))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_SHOPPING_ADDRESS.getMessage());
	}

	@Test
	void 본인_배송지_아닌경우_수정_실패() {
		//given
		ShoppingAddress create = new ShoppingAddress(
			user,
			"이름",
			"서울특별시 강남구 테헤란로 123",
			"010-1234-5678",
			ShoppingAddressType.DOOR,
			null,
			false
		);

		ShoppingAddressUpdateRequest updateRequest = new ShoppingAddressUpdateRequest(
			"이름 수정",
			"서울특별시 강남구 테헤란로 123 수정",
			"010-1234-5679",
			ShoppingAddressType.ETC,
			"고가 물품입니다 호수 나오게 사진 한번 부탁드립니다",
			true
		);

		//when
		var createdAddress = shoppingAddressRepository.save(create);

		//then
		assertThatThrownBy(() -> shoppingAddressService.update(anotherUser.getId(), createdAddress.getId(), updateRequest))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_SHOPPING_ADDRESS_OWNER.getMessage());
	}

	@Test
	void 배송지_삭제_성공() {
		//given
		for (int i = 0; i < 2; i++) {
			ShoppingAddressCreateRequest request = new ShoppingAddressCreateRequest(
				"이름 " + i,
				"서울특별시 강남구 테헤란로 " + i,
				"010-1234-567" + i,
				ShoppingAddressType.DOOR,
				null,
				false
			);

			shoppingAddressService.create(user.getId(), request);
		}

		//when
		var createdAddressList = shoppingAddressRepository.findByUserId(user.getId());
		shoppingAddressService.delete(user.getId(), createdAddressList.getFirst().getId());

		//then
		var addressList = shoppingAddressRepository.findByUserId(user.getId());
		assertThat(addressList).hasSize(1);
		assertThat(addressList.getFirst().getName()).isEqualTo("이름 1");
	}

	@Test
	void 배송지_최소1개_이하로_삭제_실패() {
		//given
		ShoppingAddressCreateRequest request = new ShoppingAddressCreateRequest(
			"이름",
			"서울특별시 강남구 테헤란로 123",
			"010-1234-5678",
			ShoppingAddressType.DOOR,
			null,
			false
		);

		shoppingAddressService.create(user.getId(), request);

		var createdAddress = shoppingAddressRepository.findByUserId(user.getId());

		//when & then
		assertThatThrownBy(() -> shoppingAddressService.delete(user.getId(), createdAddress.getFirst().getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.CANNOT_DELETE_DEFAULT_ADDRESS.getMessage());
	}

	@Test
	void 본인_배송지_아닌경우_삭제_실패() {
		//given
		ShoppingAddressCreateRequest request = new ShoppingAddressCreateRequest(
			"이름",
			"서울특별시 강남구 테헤란로 123",
			"010-1234-5678",
			ShoppingAddressType.DOOR,
			null,
			false
		);

		shoppingAddressService.create(user.getId(), request);

		var createdAddress = shoppingAddressRepository.findByUserId(user.getId());

		//when & then
		assertThatThrownBy(() -> shoppingAddressService.delete(anotherUser.getId(), createdAddress.getFirst().getId()))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_SHOPPING_ADDRESS_OWNER.getMessage());
	}

	@Test
	void 배송지_없는경우_삭제_실패() {
		var addressId = 999999L;

		//when & then
		assertThatThrownBy(() -> shoppingAddressService.delete(user.getId(), addressId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_SHOPPING_ADDRESS.getMessage());
	}

	@Test
	void 삭제하려는_배송지가_기본_배송지였다면_나머지_주소_기본_배송지_설정() {
		//given
		for (int i = 0; i < 4; i++) {
			ShoppingAddressCreateRequest request = new ShoppingAddressCreateRequest(
				"이름 " + i,
				"서울특별시 강남구 테헤란로 " + i,
				"010-1234-567" + i,
				ShoppingAddressType.DOOR,
				null,
				false
			);

			shoppingAddressService.create(user.getId(), request);
		}
		var createdAddressList = shoppingAddressRepository.findByUserId(user.getId());
		for (int i = 0; i < 4; i++) {
			System.out.println("배송지 " + i + " :" + createdAddressList.get(i).getName() + ", isDefault: " + createdAddressList.get(i).isDefault());
		}

		var deleteAddress = createdAddressList.stream()
				.filter(ShoppingAddress::isDefault)
				.findFirst()
				.orElseThrow();

		//when
		shoppingAddressService.delete(user.getId(), deleteAddress.getId());

		//then
		var addressList = shoppingAddressRepository.findByUserId(user.getId());
		assertThat(addressList).hasSize(3);
		var newDefaultAddress = addressList.stream()
			.filter(ShoppingAddress::isDefault)
			.findFirst()
			.orElseThrow();
		assertThat(newDefaultAddress).isNotNull();
	}

}
