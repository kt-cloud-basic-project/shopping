package com.kt.domain.orderproduct;

import com.kt.domain.order.Order;
import com.kt.domain.product.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OrderProduct {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	Long count;

	Long variantId;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	public OrderProduct(Long count, Long variantId, Product product, Order order) {
		this.count = count;
		this.variantId = variantId;
		this.product = product;
		this.order = order;
	}

	public OrderProduct(Long count, Long variantId, Product product) {
		this.count = count;
		this.variantId = variantId;
		this.product = product;
	}

	public void assignOrder(Order order) {
		this.order = order;
	}
}
