package com.kt.service.payment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.payment.Payment;
import com.kt.dto.payment.PaymentCreateRequest;
import com.kt.repository.order.OrderRepository;
import com.kt.repository.payment.PaymentRepository;
import com.kt.repository.paymenttype.PaymentTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final PaymentTypeRepository paymentTypeRepository;


	@Transactional
	public void create(PaymentCreateRequest request) {

		var order = orderRepository.findById(request.orderId())
			.orElseThrow();

		var paymentType = paymentTypeRepository.findById(request.paymentTypeId())
			.orElseThrow();

		int total = order.getOrderProducts().stream()
			.mapToInt(orderproduct -> (int) (orderproduct.getProduct().getPrice() * orderproduct.getCount()))
			.sum();

		int delivery = 3000;

		int finalPrice = total + delivery;

		var payment = new Payment(order, paymentType, total, delivery, finalPrice);

		paymentRepository.save(payment);
	}

}
