package com.kt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
