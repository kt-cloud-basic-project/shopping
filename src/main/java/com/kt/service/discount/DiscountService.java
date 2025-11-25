package com.kt.service.discount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.support.Preconditions;
import com.kt.domain.discount.Discount;
import com.kt.dto.discount.DiscountCreateRequest;
import com.kt.dto.discount.DiscountDetailResponse;
import com.kt.dto.discount.DiscountListResponse;
import com.kt.dto.discount.DiscountUpdateRequest;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.discount.DiscountRepositoryCustom;
import com.kt.repository.membership.MembershipRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DiscountService {

	private final DiscountRepository discountRepository;
	private final MembershipRepository membershipRepository;
	private final DiscountRepositoryCustom discountRepositoryCustom;

	public void create(Long membershipId, DiscountCreateRequest request) {

		var membership = membershipRepository.findByIdOrThrow(membershipId, ErrorCode.NOT_FOUND_MEMBERSHIP);

		Preconditions.validate(!discountRepository.existsByMembershipId(membershipId), ErrorCode.DISCOUNT_ALREADY_EXISTS);

		var discount = new Discount(
			request.name(),
			request.type(),
			request.value(),
			membership
		);

		discountRepository.save(discount);
	}

	public Page<DiscountListResponse> discountAllList(Pageable pageable) {
		return discountRepositoryCustom.discountAllList(pageable);
	}

	public void update(Long discountId, DiscountUpdateRequest request) {

		var discount = discountRepository.findByIdOrThrow(discountId, ErrorCode.NOT_FOUND_DISCOUNT);

		discount.update(
			request.name(),
			request.type(),
			request.value()
		);
	}

	public void delete(Long discountId) {

		var discount = discountRepository.findByIdOrThrow(discountId, ErrorCode.NOT_FOUND_DISCOUNT);

		discountRepository.delete(discount);
	}

	public DiscountDetailResponse detail(Long discountId) {

		// 1+N 문제, 현재는 단건 조회라서 문제는 없겠지만 membership 추가 쿼리 발생
		// var discount = discountRepository.findByIdOrThrow(discountId, ErrorCode.NOT_FOUND_DISCOUNT);

		// 해결 방법: EntityGraph 사용
		var discount = discountRepository.findDiscountDetailByIdOrThrow(discountId, ErrorCode.NOT_FOUND_DISCOUNT);

		return new DiscountDetailResponse(
			discount.getId(),
			discount.getName(),
			discount.getType(),
			discount.getValue(),
			discount.getMembership().getId(),
			discount.getMembership().getLevel()
		);
	}
}
