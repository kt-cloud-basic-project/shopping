package com.kt.service.discount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.support.Preconditions;
import com.kt.domain.discount.Discount;
import com.kt.domain.discount.DiscountType;
import com.kt.domain.user.User;
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

		if (request.type() == DiscountType.PERCENTAGE) {
			boolean isValidPercentage = request.value() > 0 && request.value() < 100;

			Preconditions.validate(isValidPercentage, ErrorCode.INVALID_PERCENTAGE_DISCOUNT_VALUE);
		}

		var discount = new Discount(
			request.name(),
			request.type(),
			request.value(),
			membership
		);

		discountRepository.save(discount);
	}

	public Page<DiscountListResponse> getAllDiscount(Pageable pageable) {
		return discountRepositoryCustom.getAllDiscount(pageable);
	}

	public void update(Long discountId, DiscountUpdateRequest request) {

		var discount = discountRepository.findByIdOrThrow(discountId, ErrorCode.NOT_FOUND_DISCOUNT);
		Preconditions.validate(request.type() == DiscountType.PERCENTAGE && (request.value() < 1 || request.value() > 100), ErrorCode.INVALID_PERCENTAGE_DISCOUNT_VALUE);

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

		// 해결 방법: EntityGraph 사용
		var discount = discountRepository.findDiscountDetailByIdOrThrow(discountId, ErrorCode.NOT_FOUND_DISCOUNT);

		return new DiscountDetailResponse(
			discount.getMembership().getId(),
			discount.getMembership().getLevel(),
			discount.getId(),
			discount.getName(),
			discount.getType(),
			discount.getValue()
		);
	}
}
