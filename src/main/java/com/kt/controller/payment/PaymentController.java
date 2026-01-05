package com.kt.controller.payment;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.common.request.Paging;
import com.kt.common.response.ApiResult;
import com.kt.common.support.SwaggerAssistance;
import com.kt.domain.order.Order;
import com.kt.domain.order.OrderStatus;
import com.kt.dto.payment.PaymentCreateRequest;
import com.kt.dto.payment.PaymentDetailResponse;
import com.kt.dto.payment.PaymentListResponse;
import com.kt.dto.payment.PaymentOrderInfoResponse;
import com.kt.dto.payment.PaymentTossCancelRequest;
import com.kt.dto.payment.PaymentTossCancelResponse;
import com.kt.dto.payment.PaymentTossConfirmRequest;
import com.kt.properties.TossPaymentsProperties;
import com.kt.security.CustomUserDetails;
import com.kt.service.order.OrderService;
import com.kt.service.payment.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payment", description = "Payment 사용자용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController extends SwaggerAssistance {

	private final PaymentService paymentService;
	private final OrderService orderService;
	private final TossPaymentsProperties tossPaymentsProperties;
	private final RestTemplate restTemplate = new RestTemplate();

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "결제 기록 목록 조회")
	public ApiResult<Page<PaymentListResponse>> getMyAllPayment(
		@AuthenticationPrincipal CustomUserDetails currentUser,
		Paging paging
	) {
		return ApiResult.ok(paymentService.getMyAllPayment(currentUser.getId(), paging.toPageable()));
	}

	@GetMapping("/{paymentId}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "결제 기록 상세 조회")
	public ApiResult<PaymentDetailResponse> getPayment(
		@PathVariable("paymentId") Long paymentId,
		@AuthenticationPrincipal CustomUserDetails currentUser
		) {
		return ApiResult.ok(paymentService.getPayment(currentUser.getId(), paymentId));
	}

	/*결제기록 등록 -> toss 결제 이후 성공 시 등록 변경*/
	/*@PostMapping("")
	@Operation(summary = "결제 기록 등록")
	public ApiResult<Void> create(@Valid @RequestBody PaymentCreateRequest request, @AuthenticationPrincipal CustomUserDetails currentUser) {
		paymentService.create(request, currentUser.getId());
		return ApiResult.ok();
	}*/

	@PostMapping("/confirm")
	@Operation(summary = "Toss 결제 승인 요청")
	public ResponseEntity<?> confirmPayment(@RequestBody PaymentTossConfirmRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		try {
			String url = tossPaymentsProperties.getApiUrl() + "/confirm";
			String auth = tossPaymentsProperties.getSecretKey() + ":";
			String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

			String orderId = request.orderId().split("-")[0];
			Long orderIdLong = Long.parseLong(orderId);
			var order = orderService.getOrderDetail(orderIdLong, currentUser.getId());
			if (order.orderStatus() != OrderStatus.ORDERED) {
				return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(Map.of(
						"code", ErrorCode.ALREADY_PAID_ORDER.getStatus(),
						"message", ErrorCode.ALREADY_PAID_ORDER.getMessage()
					));
			}

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Basic " + encodedAuth);
			headers.setContentType(MediaType.APPLICATION_JSON);
			System.out.println(request.toString());
			Map<String, String> body = Map.of(
				"paymentKey", request.paymentKey(),
				"orderId", request.orderId(),
				"amount", request.amount().toString()
			);

			HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
			ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				Map<String, Object> tossPayment = response.getBody();
				Long paymentId = paymentService.create(tossPayment, currentUser.getId());

				Map<String, Object> responseBody = new HashMap<>(tossPayment);
				responseBody.put("paymentId", paymentId);

				return ResponseEntity.ok(responseBody);
			}

			return ResponseEntity.ok(response.getBody());

		} catch (HttpClientErrorException e) {
			// Toss API에서 반환한 에러 응답을 그대로 전달
			return ResponseEntity
				.status(e.getStatusCode())
				.body(e.getResponseBodyAsString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of(
					"code", "INTERNAL_SERVER_ERROR",
					"message", "결제 승인 처리 중 오류가 발생했습니다: " + e.getMessage()
				));
		}
	}

	@GetMapping("/order/{orderId}")
	@Operation(summary = "주문 ID로 결제 정보 조회")
	public ApiResult<PaymentDetailResponse> getPaymentByOrderId(
		@PathVariable Long orderId,
		@AuthenticationPrincipal CustomUserDetails currentUser
	) {
		return ApiResult.ok(paymentService.getPaymentByOrderId(currentUser.getId(), orderId));
	}

	@PostMapping("/{paymentId}/cancel")
	@Operation(summary = "결제 취소")
	public ResponseEntity<?> cancelPayment(
		@PathVariable Long paymentId,
		@RequestBody PaymentTossCancelRequest request,
		@AuthenticationPrincipal CustomUserDetails currentUser) {
		try {
			String paymentKey = paymentService.getPaymentKey(paymentId, currentUser.getId());


			String url = tossPaymentsProperties.getApiUrl() + "/" + paymentKey + "/cancel";
			String auth = tossPaymentsProperties.getSecretKey() + ":";
			String encodedAuth = java.util.Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Basic " + encodedAuth);
			headers.setContentType(MediaType.APPLICATION_JSON);

			Map<String, Object> body = new HashMap<>();
			body.put("cancelReason", request.cancelReason());
			if (request.cancelAmount() != null) {
				body.put("cancelAmount", request.cancelAmount());
			}

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
			ResponseEntity<Map> tossResponse = restTemplate.postForEntity(url, entity, Map.class);

			if (tossResponse.getStatusCode() == HttpStatus.OK) {
				PaymentTossCancelResponse response = paymentService.cancelPayment(request, currentUser.getId(), paymentId);
				return ResponseEntity.ok(ApiResult.ok(response));
			}

			throw new CustomException(ErrorCode.PAYMENT_CANCEL_FAILED);

		} catch (HttpClientErrorException e) {
			// Toss API에서 반환한 에러 응답을 그대로 전달
			return ResponseEntity
				.status(e.getStatusCode())
				.body(e.getResponseBodyAsString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of(
					"code", "INTERNAL_SERVER_ERROR",
					"message", "결제 승인 처리 중 오류가 발생했습니다: " + e.getMessage()
				));
		}
	}

	@GetMapping("/order/{orderId}/info")
	@Operation(summary = "주문 번호로 결제 정보 조회")
	public ApiResult<PaymentOrderInfoResponse> getPaymentDetail(@PathVariable Long orderId,
		@AuthenticationPrincipal CustomUserDetails currentUser) {
		return ApiResult.ok(paymentService.getPaymentDetail(currentUser.getId(), orderId));
	}

	@GetMapping("/client-key")
	@Operation(summary = "Toss 클라이언트 키 조회")
	public ApiResult<Map<String, String>> getClientKey() {
		return ApiResult.ok(Map.of("clientKey", tossPaymentsProperties.getClientKey()));
	}


}

