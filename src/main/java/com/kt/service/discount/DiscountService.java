package com.kt.service.discount;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.common.support.Preconditions;
import com.kt.domain.discount.Discount;
import com.kt.domain.discount.DiscountTargetType;
import com.kt.domain.discount.DiscountType;
import com.kt.domain.discountMembership.DiscountMembership;
import com.kt.domain.discountProduct.DiscountProduct;
import com.kt.dto.discount.request.DiscountCreateRequest;
import com.kt.dto.discount.response.DiscountMembershipDetailResponse;
import com.kt.dto.discount.response.DiscountListResponse;
import com.kt.dto.discount.request.DiscountUpdateRequest;
import com.kt.dto.discount.response.DiscountProductDetailResponse;
import com.kt.dto.discount.response.DiscountUserResponse;
import com.kt.event.MembershipDiscountEvent;
import com.kt.event.ProductDiscountEvent;
import com.kt.repository.discount.DiscountRepository;
import com.kt.repository.discount.DiscountRepositoryCustom;
import com.kt.repository.discountmembership.DiscountMembershipCustom;
import com.kt.repository.discountmembership.DiscountMembershipRepository;
import com.kt.repository.discountproduct.DiscountProductCustom;
import com.kt.repository.discountproduct.DiscountProductRepository;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DiscountService {

	private final DiscountRepository discountRepository;
	private final MembershipRepository membershipRepository;
	private final DiscountRepositoryCustom discountRepositoryCustom;
	private final UserRepository userRepository;
	private final DiscountMembershipRepository discountMembershipRepository;
	private final ProductRepository productRepository;
	private final DiscountProductRepository discountProductRepository;
	private final DiscountMembershipCustom DiscountMembershipCustom;
	private final DiscountProductCustom DiscountProductCustom;
	private final ApplicationEventPublisher eventPublisher;

	public Long create(DiscountCreateRequest request) {

		if (request.type() == DiscountType.PERCENTAGE) {
			boolean isValidPercentage = request.value() > 0 && request.value() < 100;

			Preconditions.validate(isValidPercentage, ErrorCode.INVALID_PERCENTAGE_DISCOUNT_VALUE);
		}

		var discount = new Discount(
			request.name(),
			request.targetType(),
			request.type(),
			request.isCombinable(),
			request.value()
		);

		if(request.targetType().equals(DiscountTargetType.MEMBERSHIP)) {
			var membership = membershipRepository.findByIdOrThrow(request.targetId(), ErrorCode.NOT_FOUND_MEMBERSHIP);
			Preconditions.validate(!discountMembershipRepository.existsByMembershipId(membership.getId()), ErrorCode.DISCOUNT_ALREADY_EXISTS);

			var savedDiscount = discountRepository.save(discount);
			discountMembershipRepository.save(new DiscountMembership(savedDiscount, membership));

			eventPublisher.publishEvent(
				new MembershipDiscountEvent(
					savedDiscount.getId(),
					membership.getId(),
					savedDiscount.getName(),
					savedDiscount.getValue()
				)
			);

			return savedDiscount.getId();

		} else {
			var product = productRepository.findByIdOrThrow(request.targetId(), ErrorCode.NOT_FOUND_PRODUCT);

			var savedDiscount = discountRepository.save(discount);
			discountProductRepository.save(new DiscountProduct(savedDiscount, product));

			eventPublisher.publishEvent(
				new ProductDiscountEvent(
					savedDiscount.getId(),
					product.getId(),
					product.getName(),
					savedDiscount.getName(),
					savedDiscount.getValue()
				)
			);

			return savedDiscount.getId();
		}
	}

	public Page<DiscountListResponse> getAllDiscount(Pageable pageable) {
		return discountRepositoryCustom.getAllDiscount(pageable);
	}

	public Long update(Long discountId, DiscountUpdateRequest request) {

		var discount = discountRepository.findByIdOrThrow(discountId, ErrorCode.NOT_FOUND_DISCOUNT);

		if (request.type() == DiscountType.PERCENTAGE) {
			boolean isValidPercentage = request.value() > 0 && request.value() < 100;

			Preconditions.validate(isValidPercentage, ErrorCode.INVALID_PERCENTAGE_DISCOUNT_VALUE);
		}

		discount.update(
			request.name(),
			request.type(),
			request.isCombinable(),
			request.value()
		);

		return discount.getId();
	}

	public Long delete(Long discountId) {
		var discount = discountRepository.findByIdOrThrow(discountId, ErrorCode.NOT_FOUND_DISCOUNT);

		if(discount.getTargetType().equals(DiscountTargetType.MEMBERSHIP)) {
			discountMembershipRepository.deleteByDiscountId(discount.getId());
		} else {
			discountProductRepository.deleteByDiscountId(discount.getId());
		}

		discountRepository.delete(discount);

		return discount.getId();
	}

	public DiscountMembershipDetailResponse getDetailMembership(Long discountId) {

		var discount = discountRepository.findByIdOrThrow(discountId, ErrorCode.NOT_FOUND_DISCOUNT);

		return DiscountMembershipCustom.findDiscountDetailByDiscountId(discount.getId());
	}

	public DiscountProductDetailResponse getDetailProduct(Long discountId) {

		var discount = discountRepository.findByIdOrThrow(discountId, ErrorCode.NOT_FOUND_DISCOUNT);

		return DiscountProductCustom.findDiscountDetailByDiscountId(discount.getId());
	}

	public DiscountUserResponse getMyDiscount(Long userId) {
		var user = userRepository.findByIdOrThrow(userId, ErrorCode.NOT_FOUND_USER);

		return discountRepositoryCustom.findDiscountUserById(user.getId());
	}
}
