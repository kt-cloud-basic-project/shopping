package com.kt.service.paymenttype;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.domain.paymenttype.PaymentType;
import com.kt.dto.paymenttype.PaymentTypeCreateRequest;
import com.kt.dto.paymenttype.PaymentTypeListResponse;
import com.kt.repository.paymenttype.PaymentTypeRepository;
import com.kt.repository.user.UserRepository;

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

	public void delete(Long id) {
		var paymentType = paymentTypeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException(ErrorCode.NOT_FOUND_PAYMENT_TYPE.getMessage()));

		paymentType.delete();
	}

	public List<PaymentTypeListResponse> getAllPaymentTypes() {
		var paymentType = paymentTypeRepository.findAll();

		return paymentType.stream()
			.map(paymentType1 -> new PaymentTypeListResponse(paymentType1.getId(), paymentType1.getName(),
				paymentType1.isDeleted())).toList();
	}

}
