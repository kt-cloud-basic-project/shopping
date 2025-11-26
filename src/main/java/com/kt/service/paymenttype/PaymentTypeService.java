package com.kt.service.paymenttype;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.payment.PaymentType;
import com.kt.dto.paymenttype.PaymentTypeCreateRequest;
import com.kt.repository.paymenttype.PaymentTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentTypeService {

	private final PaymentTypeRepository paymentTypeRepository;

	public void create(PaymentTypeCreateRequest request) {
		PaymentType paymentType = new PaymentType(
			request.name()
		);

		paymentTypeRepository.save(paymentType);
	}

	public void delete(Long id){
		PaymentType paymentType = paymentTypeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("결제수단을 찾을 수 없습니다."));

		paymentTypeRepository.delete(paymentType);
	}

}
