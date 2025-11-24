package com.kt.repository.discount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.dto.discount.DiscountListResponse;

public interface DiscountRepositoryCustom {
	Page<DiscountListResponse> discountAllList(Pageable pageable);
}
