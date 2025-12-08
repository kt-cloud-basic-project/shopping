package com.kt.repository.shoppingaddress;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.shoppingaddress.ShoppingAddress;

import java.util.Optional;

public interface ShoppingAddressRepository extends JpaRepository<ShoppingAddress, Long> {

  Optional<ShoppingAddress> findFirstByUserIdAndIsDefaultTrueOrderByIdDesc(Long userId);

	default ShoppingAddress findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	List<ShoppingAddress> findByUserId(Long userId);

	Optional<ShoppingAddress> findByIdAndUserId(Long id, Long userId);

	default ShoppingAddress findByIdAndUserIdOrThrow(Long id, Long userId, ErrorCode errorCode) {
		return findByIdAndUserId(id, userId).orElseThrow(() -> new CustomException(errorCode));
	}

}
