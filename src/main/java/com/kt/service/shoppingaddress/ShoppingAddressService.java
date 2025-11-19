package com.kt.service.shoppingaddress;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.repository.shoppingaddress.ShoppingAddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingAddressService {

	private final ShoppingAddressRepository shoppingAddressRepository;

	//TODO
	// isDefault 가 true이면
	// -> 기존 기본 배송지가 있다면 false 변경 로직
	// -> 기존 기본 배송지가 없다면 그대로 추가
	// isDefault 가 false일 때
	// -> 기본 배송지가 있는 경우 그대로 추가
	// -> 기본 배송지가 없는 경우 강제 true 또는 예외 처리
}
