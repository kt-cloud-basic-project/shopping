package com.kt.domain.category;

import java.util.ArrayList;
import java.util.List;

import com.kt.domain.product.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String type;

	@OneToMany(mappedBy = "category")
	private final List<Product> products = new ArrayList<>();

	public Category(String type) {
		this.type = type;
	}
}
