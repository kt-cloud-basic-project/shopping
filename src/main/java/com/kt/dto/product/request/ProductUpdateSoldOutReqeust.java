package com.kt.dto.product.request;

import java.util.List;

public record ProductUpdateSoldOutReqeust(
	List<Long> productIds
) {
}
