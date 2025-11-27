package com.kt.repository.variant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.variant.Variant;

public interface VariantRepository extends JpaRepository<Variant, Long> {
	List<Variant> findByProductId(Long productId);
}
