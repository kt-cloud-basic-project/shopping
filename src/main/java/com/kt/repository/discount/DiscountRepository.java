package com.kt.repository.discount;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.discount.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
