package com.kt.dto.shoppingaddress;

import com.kt.domain.shoppingaddress.ShoppingAddress;
import com.kt.domain.shoppingaddress.ShoppingAddressType;

public record ShoppingAddressListResponse(
	Long id,
	String name,
	String address,
	String mobile,
	ShoppingAddressType infoType,
	String infoDesc,
	Boolean isDefault
) {
	public static ShoppingAddressListResponse fromList(ShoppingAddress shoppingAddress) {
		return new ShoppingAddressListResponse(
			shoppingAddress.getId(),
			shoppingAddress.getName(),
			shoppingAddress.getAddress(),
			shoppingAddress.getMobile(),
			shoppingAddress.getInfoType(),
			shoppingAddress.getInfoType().equals(ShoppingAddressType.ETC) ? shoppingAddress.getInfoDesc() : "",
			shoppingAddress.isDefault()
		);
	}
}
