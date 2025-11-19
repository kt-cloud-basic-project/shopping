package com.kt.controller.discount;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.service.discount.DiscountService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin Discount", description = "관리자 할인 기능 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/discounts")
public class AdminDiscountController {

	private final DiscountService discountService;

}
