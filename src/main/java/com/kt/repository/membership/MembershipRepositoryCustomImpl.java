package com.kt.repository.membership;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.kt.domain.membership.QMembership;
import com.kt.dto.membership.MembershipListResponse;
import com.kt.dto.membership.QMembershipListResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MembershipRepositoryCustomImpl implements  MembershipRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QMembership membership = QMembership.membership;

	@Override
	public Page<MembershipListResponse> membershipAllList(Pageable pageable) {

		var content = queryFactory
			.select(new QMembershipListResponse(
				membership.id,
				membership.level
			))
			.from(membership)
			.orderBy(membership.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		var total = queryFactory
			.select(membership.id)
			.from(membership)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}
}
