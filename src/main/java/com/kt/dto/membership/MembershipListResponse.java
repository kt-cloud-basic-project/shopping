package com.kt.dto.membership;

import org.springframework.data.domain.Page;

import com.kt.domain.membership.Membership;
import com.querydsl.core.annotations.QueryProjection;

public record MembershipListResponse(
		Long id,
		String level
) {
	public static Page<MembershipListResponse> fromList(Page<Membership> page) {
		return page.map(membership -> new MembershipListResponse(
			membership.getId(),
			membership.getLevel()
		));
	}

	@QueryProjection
	public MembershipListResponse {
	}
}
