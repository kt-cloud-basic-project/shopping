package com.kt.repository.paymenttype;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.paymenttype.PaymentType;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {

	Optional<PaymentType> findByName(String name);
}
