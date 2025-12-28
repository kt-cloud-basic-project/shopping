package com.kt.domain.discountProduct;

import com.kt.domain.discount.Discount;
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
public class DiscountProduct {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "discount_id")
	private Discount discount;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	public DiscountProduct(Discount discount, Product product) {
		this.discount = discount;
		this.product = product;
	}
}
