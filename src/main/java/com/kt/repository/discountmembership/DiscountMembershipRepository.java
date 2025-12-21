package com.kt.repository.discountmembership;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.discountMembership.DiscountMembership;

public interface DiscountMembershipRepository extends JpaRepository<DiscountMembership, Long> {
	default DiscountMembership findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	boolean existsByMembershipId(Long id);

	void deleteByDiscountId(Long membershipId);
}
