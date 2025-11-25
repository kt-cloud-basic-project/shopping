package com.kt.dto.shoppingaddress;

import com.kt.domain.shoppingaddress.ShoppingAddressType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ShoppingAddressCreateRequest(

	@NotBlank(message = "배송지 이름은 필수입니다")
	String name,
	
	@NotBlank(message = "배송지 주소는 필수입니다")
	String address,
	
	@NotBlank(message = "연락처는 필수입니다")
	@Pattern(regexp = "^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$", message = "연락처 형식이 올바르지 않습니다")
	String mobile,

	@NotNull(message = "배송 요청 정보 선택은 필수입니다")
	ShoppingAddressType infoType,

	String infoDesc,

	@NotNull(message = "기본 배송지 여부는 필수입니다")
	Boolean isDefault
) {
}
