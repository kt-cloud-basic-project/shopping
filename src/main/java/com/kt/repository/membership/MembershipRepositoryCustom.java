package com.kt.repository.membership;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.kt.dto.membership.MembershipListResponse;

public interface MembershipRepositoryCustom {
	Page<MembershipListResponse> getAllMembership(Pageable pageable);
}
