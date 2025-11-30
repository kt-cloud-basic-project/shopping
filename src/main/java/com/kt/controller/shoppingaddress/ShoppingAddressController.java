package com.kt.controller.shoppingaddress;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.shoppingaddress.ShoppingAddressCreateRequest;
import com.kt.dto.shoppingaddress.ShoppingAddressListResponse;
import com.kt.dto.shoppingaddress.ShoppingAddressUpdateRequest;
import com.kt.security.CustomUserDetails;
import com.kt.service.shoppingaddress.ShoppingAddressService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Shopping Address", description = "배송지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/addresses")
public class ShoppingAddressController extends SwaggerAssistance {

	private final ShoppingAddressService shoppingAddressService;

	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> create(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@Valid @RequestBody ShoppingAddressCreateRequest request
	) {

		shoppingAddressService.create(currentUser.getId(), request);

		return ApiResult.ok();
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<List<ShoppingAddressListResponse>> getMyShoppingAddress(
		@AuthenticationPrincipal CustomUserDetails currentUser
		) {

		List<ShoppingAddressListResponse> shoppingAddressList = shoppingAddressService.getMyShoppingAddress(currentUser.getId());

		return ApiResult.ok(shoppingAddressList);
	}

	@PutMapping("/{addressId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> update(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long addressId,
		@Valid @RequestBody ShoppingAddressUpdateRequest request
	) {

		shoppingAddressService.update(currentUser.getId(), addressId, request);

		return ApiResult.ok();
	}

	@PatchMapping("/{addressId}/default")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> defaultAddress(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long addressId
	) {

		shoppingAddressService.defaultAddress(currentUser.getId(), addressId);

		return ApiResult.ok();
	}

	@DeleteMapping("/{addressId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> delete(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		@PathVariable Long addressId
	) {

		shoppingAddressService.delete(currentUser.getId(), addressId);

		return ApiResult.ok();
	}

}
