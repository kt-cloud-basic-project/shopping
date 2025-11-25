package com.kt.controller.shoppingaddress;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.ApiResult;
import com.kt.dto.shoppingaddress.ShoppingAddressCreateRequest;
import com.kt.dto.shoppingaddress.ShoppingAddressListResponse;
import com.kt.dto.shoppingaddress.ShoppingAddressUpdateRequest;
import com.kt.service.shoppingaddress.ShoppingAddressService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Shopping Address", description = "배송지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/addresses")
public class ShoppingAddressController {

	private final ShoppingAddressService shoppingAddressService;

	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> create(
		@Valid @RequestBody ShoppingAddressCreateRequest request
	) {

		shoppingAddressService.create(2L, request);

		return ApiResult.ok();
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<List<ShoppingAddressListResponse>> myShoppingAddressList() {

		List<ShoppingAddressListResponse> shoppingAddressList = shoppingAddressService.myShoppingAddressList(2L);

		return ApiResult.ok(shoppingAddressList);
	}

	@PutMapping("/{addressId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> update(
		@PathVariable Long addressId,
		@Valid @RequestBody ShoppingAddressUpdateRequest request
	) {

		shoppingAddressService.update(2L, addressId, request);

		return ApiResult.ok();
	}

	@PutMapping("/{addressId}/default")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> defaultAddress(
		@PathVariable Long addressId
	) {

		shoppingAddressService.defaultAddress(2L, addressId);

		return ApiResult.ok();
	}

	@DeleteMapping("/{addressId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> delete(
		@PathVariable Long addressId
	) {

		shoppingAddressService.delete(2L, addressId);

		return ApiResult.ok();
	}

}
