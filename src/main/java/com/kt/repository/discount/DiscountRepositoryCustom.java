package com.kt.repository.discount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.dto.discount.response.DiscountListResponse;
import com.kt.dto.discount.response.DiscountUserResponse;

public interface DiscountRepositoryCustom {
	Page<DiscountListResponse> getAllDiscount(Pageable pageable);

	DiscountUserResponse findDiscountUserById(Long userId);

}
