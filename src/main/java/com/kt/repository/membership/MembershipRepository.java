package com.kt.repository.membership;

import java.util.Optional;

import com.kt.common.CustomException;
import com.kt.common.ErrorCode;
import com.kt.domain.membership.Membership;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

	default Membership findByIdOrThrow(Long id, ErrorCode errorCode) {
		return findById(id).orElseThrow(() -> new CustomException(errorCode));
	}

	Optional<Membership> findByLevel(String level);
}
