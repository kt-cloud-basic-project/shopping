package com.kt.domain.payment;

import com.kt.common.support.BaseEntity;
import com.kt.domain.order.Order;
import com.kt.domain.orderproduct.OrderProduct;
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
	private Long totalPrice;    // 총 상품 금액

	@Column(nullable = false)
	private Long deliveryFee;   // 배송비

	@Column(nullable = false)
	private Long finalPrice;    // 총 결제 금액 (상품금액 + 배송비)

	@Column(nullable = false)
	private boolean isDeleted = false;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_type_id", nullable = false)
	private PaymentType paymentType;

	public Payment(
		Order order,
		PaymentType paymentType,
		Long totalPrice,
		Long deliveryFee,
		Long finalPrice
	) {
		this.order = order;
		this.paymentType = paymentType;
		this.totalPrice = totalPrice;
		this.deliveryFee = deliveryFee;
		this.finalPrice = finalPrice;
		this.isDeleted = false;
	}

	public void delete() {
		this.isDeleted = true;
	}
}
