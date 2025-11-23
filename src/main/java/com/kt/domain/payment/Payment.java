package com.kt.domain.payment;

import com.kt.common.BaseEntity;
import com.kt.domain.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 전체 금액
	@Column(nullable = false)
	private Integer totalPrice;

	// 배달 금액
	@Column(nullable = false)
	private Integer deliveryFee;

	// 최종 결제 금액
	@Column(nullable = false)
	private Integer finalPrice;

	@Column(nullable = false)
	private boolean isDeleted = false;

	// 주문 FK
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	// 결제 방식
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_type_id", nullable = false)
	private PaymentType paymentType;

	public Payment(
		Order order,
		Integer totalPrice,
		Integer deliveryFee,
		Integer finalPrice,
		PaymentType paymentType
	) {
		this.order = order;
		this.totalPrice = totalPrice;
		this.deliveryFee = deliveryFee;
		this.finalPrice = finalPrice;
		this.paymentType = paymentType;
		this.isDeleted = false;
	}

	public void delete() {
		this.isDeleted = true;
	}
}
