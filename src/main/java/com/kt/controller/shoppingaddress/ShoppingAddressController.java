package com.kt.controller.shoppingaddress;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.repository.shoppingaddress.ShoppingAddressRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Shopping Address", description = "배송지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me/addresses")
public class ShoppingAddressController {

	private final ShoppingAddressRepository shoppingAddressRepository;

}
