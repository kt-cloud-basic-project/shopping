package com.kt.service.paymenttype;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.kt.common.exception.ErrorCode;
import com.kt.domain.paymenttype.PaymentType;
import com.kt.dto.paymenttype.PaymentTypeCreateRequest;
import com.kt.dto.paymenttype.PaymentTypeListResponse;
import com.kt.repository.paymenttype.PaymentTypeRepository;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
class PaymentTypeServiceTest {

	@Autowired
	private PaymentTypeService paymentTypeService;

	@Autowired
	private PaymentTypeRepository paymentTypeRepository;

	@BeforeEach
	void setUp() {
		paymentTypeRepository.deleteAll();
	}

	@Test
	void 결제타입_생성_가능() {
		// given
		PaymentTypeCreateRequest request =
			new PaymentTypeCreateRequest("CARD");

		// when
		paymentTypeService.create(request);

		// then
		List<PaymentType> paymentTypes = paymentTypeRepository.findAll();
		assertThat(paymentTypes).hasSize(1);
		assertThat(paymentTypes.getFirst().getName()).isEqualTo("CARD");
		assertThat(paymentTypes.getFirst().isDeleted()).isFalse();
	}

	@Test
	void 결제타입_목록_조회_가능() {
		// given
		paymentTypeRepository.save(new PaymentType("CARD"));
		paymentTypeRepository.save(new PaymentType("CASH"));

		// when
		List<PaymentTypeListResponse> result =
			paymentTypeService.getAllPaymentTypes();

		// then
		assertThat(result).hasSize(2);
		assertThat(result)
			.extracting(PaymentTypeListResponse::name)
			.containsExactlyInAnyOrder("CARD", "CASH");
	}

	@Test
	void 결제타입_삭제_가능() {
		// given
		PaymentType paymentType =
			paymentTypeRepository.save(new PaymentType("CARD"));

		// when
		paymentTypeService.delete(paymentType.getId());

		// then
		PaymentType deleted =
			paymentTypeRepository.findById(paymentType.getId()).get();

		assertThat(deleted.isDeleted()).isTrue();
	}

	@Test
	void 없는_결제타입_삭제_불가() {
		// given
		Long notExistId = 999999L;

		// when & then
		assertThatThrownBy(() ->
			paymentTypeService.delete(notExistId)
		).isInstanceOf(IllegalArgumentException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_PAYMENT_TYPE.getMessage());
	}
}
