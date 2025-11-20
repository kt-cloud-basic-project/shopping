package com.kt.repository.shoppingaddress;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.shoppingaddress.ShoppingAddress;

public interface ShoppingAddressRepository extends JpaRepository<ShoppingAddress, Long> {
}
