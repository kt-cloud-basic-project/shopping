package com.kt.dto.product.request;

import java.util.List;

public record ProductUpdateSoldOutRequest(
	List<Long> productIds
) {
}
