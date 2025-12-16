package com.kt.service.membership;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.membership.Membership;
import com.kt.dto.membership.MembershipCreateRequest;
import com.kt.dto.membership.MembershipListResponse;
import com.kt.dto.membership.MembershipUpdateRequest;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.membership.MembershipRepository;

import jakarta.transaction.Transactional;
import static org.assertj.core.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MembershipServiceTest {

	@Autowired
	private MembershipService membershipService;

	@Autowired
	private MembershipRepository membershipRepository;

	@Test
	public void 멤버십_생성_가능() {

		// given
		MembershipCreateRequest request = new MembershipCreateRequest("GOLD");

		// when
		membershipService.create(request);

		//then
		var membership = membershipRepository.findAll();
		assertThat(membership).hasSize(1);

		var createdMembership = membership.getFirst();
		assertThat(createdMembership.getLevel()).isEqualTo("GOLD");
	}

	@Test
	public void 멤버십_목록_조회_가능() {
		//given
		membershipRepository.saveAndFlush(new Membership("BASIC"));
		membershipRepository.saveAndFlush(new Membership("SILVER"));
		membershipRepository.saveAndFlush(new Membership("GOLD"));

		Pageable pageable = PageRequest.of(0, 10);

		//when
		Page<MembershipListResponse> result = membershipService.getAllMembership(pageable);

		//then
		assertThat(result).hasSize(3);
		assertThat(result.getTotalElements()).isEqualTo(3);

		var levels = result.getContent().stream()
			.map(MembershipListResponse::level)
			.toList();

		assertThat(levels).containsExactlyInAnyOrder("BASIC", "SILVER", "GOLD");
	}

	@Test
	public void 이미_존재하는_멤버십_레벨_수정_불가() {
		//given
		var membership = membershipRepository.saveAndFlush(new Membership("BASIC"));

		//when
		MembershipUpdateRequest request = new MembershipUpdateRequest("BASIC");

		//then
		assertThatThrownBy(() -> membershipService.update(membership.getId(), request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.ALREADY_EXIST_MEMBERSHIP_LEVEL.getMessage());

	}

	@Test
	public void 없는_멤버십_수정_불가() {
		var membershipId = 9999999L;

		//when
		MembershipUpdateRequest request = new MembershipUpdateRequest("BASIC");

		//then
		assertThatThrownBy(() -> membershipService.update(membershipId, request))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_MEMBERSHIP.getMessage());
	}

	@Test
	public void 없는_멤버십_삭제_불가() {
		var membershipId = 9999999L;

		//then
		assertThatThrownBy(() -> membershipService.delete(membershipId))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.NOT_FOUND_MEMBERSHIP.getMessage());
	}

	@Test
	public void 멤버십_삭제_가능() {
		//given
		var membership = membershipRepository.saveAndFlush(new Membership("BASIC"));

		//when
		membershipService.delete(membership.getId());

		//then
		var deletedMembership = membershipRepository.findById(membership.getId()).get();
		assertThat(deletedMembership.isDeleted()).isTrue();
	}
}
