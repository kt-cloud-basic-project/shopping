package com.kt.repository.paymenttype;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.paymenttype.PaymentType;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Long> {

}
