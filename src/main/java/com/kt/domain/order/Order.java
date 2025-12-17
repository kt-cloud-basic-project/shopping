package com.kt.domain.order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.kt.common.support.BaseEntity;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.payment.Payment;
import com.kt.domain.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order extends BaseEntity {

	String receiverName;

	String receiverPhone;

	String receiverAddress;

	@Enumerated(EnumType.STRING)
	OrderStatus orderStatus;

	LocalDate deliveredAt;

	boolean isDeleted;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "order")
	private List<OrderProduct> orderProducts = new ArrayList<>();

	public Order(String receiverName, String receiverPhone, String receiverAddress, User user) {
		this.receiverName = receiverName;
		this.receiverPhone = receiverPhone;
		this.receiverAddress = receiverAddress;
		this.orderStatus = OrderStatus.ORDERED;
		this.isDeleted = false;

		this.user = user;
	}

	public void cancel() {
		this.isDeleted = true;
		this.orderStatus = OrderStatus.CANCELLED;
	}

	public void update(String receiverName, String receiverPhone, String receiverAddress) {
		this.receiverName = receiverName;
		this.receiverPhone = receiverPhone;
		this.receiverAddress = receiverAddress;
	}

	public void requestRefund() {
		this.orderStatus = OrderStatus.REFUND_REQUESTED;
	}

	public void requestReturn() {
		this.orderStatus = OrderStatus.RETURN_REQUESTED;
	}

	public void approveRefund() {
		this.orderStatus = OrderStatus.REFUNDED;
	}

	public void approveReturn() {
		this.orderStatus = OrderStatus.RETURNED;
	}

	public void updateStatus(OrderStatus newStatus) {
		this.orderStatus = newStatus;

		if(newStatus.equals(OrderStatus.DELIVERED)) {
			this.deliveredAt = LocalDate.now();
		}
	}
}
