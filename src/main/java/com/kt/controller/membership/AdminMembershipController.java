package com.kt.controller.membership;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.ApiResult;
import com.kt.common.Paging;
import com.kt.dto.membership.MembershipCreateRequest;
import com.kt.dto.membership.MembershipListResponse;
import com.kt.dto.membership.MembershipUpdateRequest;
import com.kt.service.membership.MembershipService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin Membership", description = "관리자 멤버쉽 기능 관리  API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/memberships")
public class AdminMembershipController {

	private final MembershipService membershipService;

	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> create(
		@Valid @RequestBody MembershipCreateRequest request
	) {

		membershipService.create(request);

		return ApiResult.ok();
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Page<MembershipListResponse>> membershipAllList(
		Paging paging
	) {

		Page<MembershipListResponse> memberships = membershipService.membershipAllList(paging.toPageable());

		return ApiResult.ok(memberships);
	}

	@PutMapping("/{membershipId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> update(
		@PathVariable Long membershipId,
		@Valid @RequestBody MembershipUpdateRequest request
	) {

		membershipService.update(membershipId, request);

		return ApiResult.ok();
	}

	@DeleteMapping("/{membershipId}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> delete(
		@PathVariable Long membershipId
	) {

		membershipService.delete(membershipId);

		return ApiResult.ok();
	}

}
