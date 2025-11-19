package com.kt.repository.variant;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.variant.Variant;

public interface VariantRepository extends JpaRepository<Variant, Long> {
}
