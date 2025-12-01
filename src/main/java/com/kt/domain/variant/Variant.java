package com.kt.domain.variant;

import com.kt.domain.product.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Variant {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Enumerated(EnumType.STRING)
	private VariantType type;

	private String detail;

	private boolean deleted;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	public Variant(VariantType type, String detail,  Product product) {
		this.type = type;
		this.detail = detail;
		this.product = product;
		this.deleted = false;
	}

	public void updateDetail(String detail) {  this.detail = detail;  }

	public void delete() { deleted = true; }
}
