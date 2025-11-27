package com.kt.service.membership;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.support.Preconditions;
import com.kt.domain.membership.Membership;
import com.kt.dto.membership.MembershipCreateRequest;
import com.kt.dto.membership.MembershipListResponse;
import com.kt.dto.membership.MembershipUpdateRequest;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.membership.MembershipRepositoryCustom;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class MembershipService {

	private final MembershipRepository membershipRepository;
	private final MembershipRepositoryCustom membershipRepositoryCustom;

	public void create(MembershipCreateRequest request) {
		Membership membership = new Membership(
			request.level()
		);

		membershipRepository.save(membership);
	}

	public Page<MembershipListResponse> getAllMembership(Pageable pageable) {

		return membershipRepositoryCustom.getAllMembership(pageable);
	}

	public void update(Long membershipId, MembershipUpdateRequest request) {
		var membership = membershipRepository.findByIdOrThrow(membershipId, ErrorCode.NOT_FOUND_MEMBERSHIP);

		Preconditions.validate(membershipRepository.findByLevel(request.level()).isEmpty(), ErrorCode.ALREADY_EXIST_MEMBERSHIP_LEVEL);

		membership.update(request.level());
	}

	public void delete(Long membershipId) {
		var membership = membershipRepository.findByIdOrThrow(membershipId, ErrorCode.NOT_FOUND_MEMBERSHIP);

		membershipRepository.delete(membership);
	}

}
