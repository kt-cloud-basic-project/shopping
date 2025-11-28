package com.kt.domain.order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.kt.common.support.BaseEntity;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	//TODO: payment domain 연결

	public Order(String receiverName, String receiverPhone, String receiverAddress, User user) {
		this.receiverName = receiverName;
		this.receiverPhone = receiverPhone;
		this.receiverAddress = receiverAddress;
		this.orderStatus = OrderStatus.ORDERED;
		this.isDeleted = false;

		this.user = user;
	}
}
