package com.kt.domain.payment;

import com.kt.common.support.BaseEntity;
import com.kt.domain.order.Order;
import com.kt.domain.paymenttype.PaymentType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "payment")
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer totalPrice;

	@Column(nullable = false)
	private Integer deliveryFee;

	@Column(nullable = false)
	private Integer finalPrice;

	@Column(nullable = false)
	private boolean isDeleted = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_type_id", nullable = false)
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
