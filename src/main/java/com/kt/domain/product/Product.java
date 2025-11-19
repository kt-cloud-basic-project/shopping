package com.kt.domain.product;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.util.Strings;

import com.kt.common.BaseEntity;
import com.kt.common.ErrorCode;
import com.kt.common.Preconditions;
import com.kt.domain.category.Category;
import com.kt.domain.option.Variant;

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

	@OneToMany
	private final List<Variant> variants = new ArrayList<>();

	public Product(String name, String description, Long price, Long stock, Category category) {
		Preconditions.validate(Strings.isNotBlank(name), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(Strings.isNotBlank(description), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(price >= 0, ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(stock >= 0, ErrorCode.INVALID_PARAMETER);

		this.name = name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.status = ProductStatus.ACTIVATED;
		this.isDeleted = false;

		this.category = category;
	}

}