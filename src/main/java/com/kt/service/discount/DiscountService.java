package com.kt.service.discount;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.repository.discount.DiscountRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DiscountService {

	private final DiscountRepository discountRepository;

}
