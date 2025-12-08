package com.kt.service.shoppingaddress;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.support.Preconditions;
import com.kt.domain.shoppingaddress.ShoppingAddress;
import com.kt.domain.shoppingaddress.ShoppingAddressType;
import com.kt.dto.shoppingaddress.ShoppingAddressCreateRequest;
import com.kt.dto.shoppingaddress.ShoppingAddressListResponse;
import com.kt.dto.shoppingaddress.ShoppingAddressUpdateRequest;
import com.kt.repository.shoppingaddress.ShoppingAddressRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingAddressService {

	private final UserRepository userRepository;
	private final ShoppingAddressRepository shoppingAddressRepository;

	private void validateInfo(ShoppingAddressType infoType, String infoDesc) {
		if(infoType == ShoppingAddressType.ETC) {
			Preconditions.validate(infoDesc != null && !infoDesc.isBlank(), ErrorCode.DELIVERY_INFO_REQUIRED_FOR_CUSTOM);
		}
	}

	private boolean processDefaultAddress(boolean requestIsDefault, List<ShoppingAddress> addressList, ShoppingAddress targetAddress) {
		if(requestIsDefault) {
			// 등록/수정하려는 주소가 기본 배송지라면 기존의 등록된 주소 중에서 기본 배송지로 설정된 주소를 모두 기본 배송지 해제
			addressList.forEach(ShoppingAddress::unsetDefault);

			return true;
		} else {
			// 등록하려는 주소가 기본 배송지가 아닌데 기존 등록된 주소가 없다면 강제로 기본 배송지로 설정
			if(targetAddress == null && addressList.isEmpty()) {
				return true;
			}

			// 수정하려는 주소
			if(targetAddress != null) {
				// 기존 등록된 주소 중에서 기본 배송지로 설정된 주소가 없다면 강제로 기본 배송지로 설정
				boolean hasDefault = addressList.stream().anyMatch(ShoppingAddress::isDefault);

				return !hasDefault;
			}

			return false;
		}
	}

	public void create(Long userId, ShoppingAddressCreateRequest request) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var addressList = shoppingAddressRepository.findByUserId(user.getId());
		Preconditions.validate(!(addressList.size() >= 5), ErrorCode.EXCEED_MAX_ADDRESS_COUNT);

		validateInfo(request.infoType(), request.infoDesc());

		boolean isDefault = processDefaultAddress(request.isDefault(), addressList, null);

		ShoppingAddress newAddress = new ShoppingAddress(
			user,
			request.name(),
			request.address(),
			request.mobile(),
			request.infoType(),
			request.infoDesc(),
			isDefault
		);

		shoppingAddressRepository.save(newAddress);
	}

	public List<ShoppingAddressListResponse> getMyShoppingAddress(Long userId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var addressList = shoppingAddressRepository.findByUserId(user.getId());

		return addressList.stream()
			.map(ShoppingAddressListResponse::fromList)
			.toList();
	}

	public void update(Long userId, Long addressId, ShoppingAddressUpdateRequest request) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var address = shoppingAddressRepository.findByIdOrThrow(addressId, ErrorCode.NOT_FOUND_SHOPPING_ADDRESS);
		Preconditions.validate(address.getUser().getId().equals(user.getId()), ErrorCode.NOT_SHOPPING_ADDRESS_OWNER);

		var addressList = shoppingAddressRepository.findByUserId(user.getId());

		validateInfo(request.infoType(), request.infoDesc());

		boolean isDefault = processDefaultAddress(request.isDefault(), addressList, address);

		address.update(
			user,
			request.name(),
			request.address(),
			request.mobile(),
			request.infoType(),
			request.infoDesc(),
			isDefault
		);
	}

	public void defaultAddress(Long userId, Long addressId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var address = shoppingAddressRepository.findByIdOrThrow(addressId, ErrorCode.NOT_FOUND_SHOPPING_ADDRESS);
		Preconditions.validate(address.getUser().getId().equals(user.getId()), ErrorCode.NOT_SHOPPING_ADDRESS_OWNER);

		var addressList = shoppingAddressRepository.findByUserId(user.getId());

		processDefaultAddress(true, addressList, address);

		address.setDefault();

	}

	public void delete(Long userId, Long addressId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		var address = shoppingAddressRepository.findByIdOrThrow(addressId, ErrorCode.NOT_FOUND_SHOPPING_ADDRESS);
		Preconditions.validate(address.getUser().getId().equals(user.getId()), ErrorCode.NOT_SHOPPING_ADDRESS_OWNER);

		var addressList = shoppingAddressRepository.findByUserId(user.getId());

		Preconditions.validate(addressList.size() > 1, ErrorCode.CANNOT_DELETE_DEFAULT_ADDRESS);

		// 삭제하는 주소가 기본 배송지였다면 나머지 주소 중에서 가장 최근에 등록된 주소를 기본 배송지로 설정
		if(address.isDefault()) {
			if(!addressList.isEmpty()) {
				ShoppingAddress otherAddress = addressList.stream()
					.filter(addresses -> !addresses.getId().equals(address.getId()))
					.max((address1, address2) -> address1.getId().compareTo(address2.getId()))
					.orElseThrow();

				otherAddress.setDefault();
			}
		}

		shoppingAddressRepository.delete(address);
	}
}
