package com.kt.service.variant;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.domain.variant.Variant;
import com.kt.domain.variant.VariantType;
import com.kt.dto.variant.VariantCreateRequest;
import com.kt.dto.variant.VariantListResponse;
import com.kt.dto.variant.VariantUpdateRequest;
import com.kt.repository.product.ProductRepository;
import com.kt.repository.variant.VariantRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VariantService {
	private final ProductRepository productRepository;
	private final VariantRepository variantRepository;


	public void create(Long productId, List<VariantCreateRequest> requests) {
		var product = productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);

		requests.forEach(request -> {
			variantRepository.save(new Variant(request.type(), request.detail(), product));
		});
	}


	public List<VariantListResponse> getVariantList(Long productId) {
		productRepository.findByIdOrThrow(productId, ErrorCode.NOT_FOUND_PRODUCT);
		var variants = variantRepository.findByProductId(productId);

		Map<VariantType, List<String>> groupedVariants = variants.stream()
			.collect(Collectors.groupingBy(
				Variant::getType,
				Collectors.mapping(Variant::getDetail, Collectors.toList())
			));


		return Arrays.stream(VariantType.values())
			.map(type -> new VariantListResponse(
				type.getDescription(),
				groupedVariants.getOrDefault(type, List.of())
			))
			.toList();
	}


	public void updateVariant(Long variantId, VariantUpdateRequest request) {
		 var variant = variantRepository.findByIdOrThrow(variantId, ErrorCode.NOT_FOUND_VARIANT);

		 variant.updateDetail(request.detail());
	}
}
