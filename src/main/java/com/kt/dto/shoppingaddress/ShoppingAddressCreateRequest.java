package com.kt.dto.shoppingaddress;

import com.kt.domain.shoppingaddress.ShoppingAddressType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ShoppingAddressCreateRequest(

	@Schema(description = "이름", example = "집")
	@NotBlank(message = "배송지 이름은 필수입니다")
	String name,

	@Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
	@NotBlank(message = "배송지 주소는 필수입니다")
	String address,

	@Schema(description = "연락처", example = "010-1234-5678")
	@NotBlank(message = "연락처는 필수입니다")
	@Pattern(regexp = "^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$", message = "연락처 형식이 올바르지 않습니다")
	String mobile,

	@Schema(description = "배송 요청 정보", example = "ETC")
	@NotNull(message = "배송 요청 정보 선택은 필수입니다")
	ShoppingAddressType infoType,

	@Schema(description = "배송 요청 정보 설명", example = "부재 시 지하1층 포스트 박스에 보관해주세요")
	String infoDesc,

	@Schema(description = "배송 요청 정보", example = "false")
	@NotNull(message = "기본 배송지 여부는 필수입니다")
	Boolean isDefault

) {
}
