package com.kt.controller.paymenttype;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.dto.paymenttype.PaymentTypeCreateRequest;
import com.kt.dto.paymenttype.PaymentTypeListResponse;
import com.kt.service.paymenttype.PaymentTypeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin PaymentType", description = "관리자 결제방법 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/payments/types")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPaymentTypeController {

	private final PaymentTypeService paymentTypeService;

	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> create(
		@Valid @RequestBody PaymentTypeCreateRequest request
	) {
		paymentTypeService.create(request);

		return ApiResult.ok();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<Void> delete(@PathVariable Long id) {
		paymentTypeService.delete(id);
		return ApiResult.ok();
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public ApiResult<List<PaymentTypeListResponse>> getAllPaymentTypes(){
		var types = paymentTypeService.getAllPaymentTypes();
		return ApiResult.ok(types);
	}
}
