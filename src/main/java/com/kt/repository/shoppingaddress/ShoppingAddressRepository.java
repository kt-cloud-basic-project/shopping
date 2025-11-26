package com.kt.repository.shoppingaddress;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.shoppingaddress.ShoppingAddress;

import java.util.Optional;

public interface ShoppingAddressRepository extends JpaRepository<ShoppingAddress, Long> {
    Optional<ShoppingAddress> findFirstByUserIdAndIsDefaultTrueOrderByIdDesc(Long userId);
}
