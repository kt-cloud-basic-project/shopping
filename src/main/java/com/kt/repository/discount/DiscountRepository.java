package com.kt.repository.discount;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.common.CustomException;
import com.kt.common.ErrorCode;
import com.kt.domain.discount.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

	default Discount findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	default Discount findDiscountDetailByIdOrThrow(Long id, ErrorCode errorCode) {
		return findWithMembershipById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	@EntityGraph(attributePaths = {"membership"})
	Optional<Discount> findWithMembershipById(Long id);

	boolean existsByMembershipId(Long membershipId);
}
