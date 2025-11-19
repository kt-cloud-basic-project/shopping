package com.kt.domain.shoppingaddress;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShoppingAddressType {
	DOOR("문 앞에 놓아주세요"),
	SECURITY_OFFICE("경비실에 맡겨주세요"),
	DELIVERY_BOX("택배함에 넣어주세요"),
	DIRECT_RECEIVE("직접 받겠습니다"),
	DIRECT_OR_DOOR("직접 받고 부재 시 문 앞"),
	DIRECT_OR_SECURITY("직접 받고 부재 시 경비실"),
	DIRECT_OR_DELIVERY_BOX("직접 받고 부재 시 택배함"),
	CONTACT_BEFORE_DELIVERY("배송 전 연락 바랍니다"),
	CUSTOM("기타 (직접 입력)");

	private final String description;
}
