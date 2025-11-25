package com.kt.domain.product;

import java.util.ArrayList;
import java.util.List;

import com.kt.common.support.BaseEntity;
import com.kt.domain.category.Category;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.variant.Variant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product extends BaseEntity {
	private String name;

	private String description;

	private Long price;

	private Long stock;

	@Enumerated(EnumType.STRING)
	private ProductStatus status;

	private boolean isDeleted;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@OneToMany(mappedBy = "product")
	private List<Variant> variants = new ArrayList<>();

	@OneToMany(mappedBy = "product")
	private List<OrderProduct> orderProducts = new ArrayList<>();

	public Product(String name, String description, Long price, Long stock, Category category) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.status = ProductStatus.ACTIVATED;
		this.isDeleted = false;

		this.category = category;
	}

	public void update(String name, String description, Long price, Long stock) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
	}

	public void updateCategory(Category category) {
		this.category = category;
	}

}