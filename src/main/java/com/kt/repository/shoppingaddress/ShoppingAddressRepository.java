package com.kt.repository.shoppingaddress;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.CustomException;
import com.kt.common.ErrorCode;
import com.kt.domain.shoppingaddress.ShoppingAddress;

public interface ShoppingAddressRepository extends JpaRepository<ShoppingAddress, Long> {

	default ShoppingAddress findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	List<ShoppingAddress> findByUserId(Long userId);
}
