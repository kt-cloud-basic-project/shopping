package com.kt.service.variant;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.exception.ErrorCode;
import com.kt.domain.variant.Variant;
import com.kt.dto.variant.VariantCreateRequest;
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
}
