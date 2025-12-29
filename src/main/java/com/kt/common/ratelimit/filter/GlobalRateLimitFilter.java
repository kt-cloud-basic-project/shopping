package com.kt.common.ratelimit.filter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kt.common.exception.ErrorCode;
import com.kt.common.response.ErrorResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GlobalRateLimitFilter extends OncePerRequestFilter {

	private static final int MAX_REQUEST_SECONDS = 100;
	private static final int EXPIRE_SECONDS = 1;
	private static final int MAX_SIZE = 10000;

	// AtomicInteger는 원자적으로 안전(쓰레드 세이프) -> 쓰레드별 카운트 잘못되는 문제 방지하기
	private final Cache<String, AtomicInteger> ipRequestCounter;

	public GlobalRateLimitFilter() {
		this.ipRequestCounter = Caffeine.newBuilder()
			.expireAfterWrite(EXPIRE_SECONDS, TimeUnit.SECONDS)
			.maximumSize(MAX_SIZE)
			.build();

		log.info("===== GlobalRateLimitFilter 초기화 완료 =====");
		log.info("Max requests per second: {}", MAX_REQUEST_SECONDS);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String clientIp = getClientIp(request);

		AtomicInteger counter = ipRequestCounter.get(clientIp, key -> new AtomicInteger(0));
		int currentCount = counter.incrementAndGet();

		log.debug("IP: {}, Count: {}/{}", clientIp, currentCount, MAX_REQUEST_SECONDS);

		if (currentCount > MAX_REQUEST_SECONDS) {
			log.warn("Rate limit exceeded for IP: {} (count: {})", clientIp, currentCount);
			handleRateLimitExceeded(response, clientIp, currentCount);
			return;
		}

		filterChain.doFilter(request, response);

	}

	private void handleRateLimitExceeded(HttpServletResponse response, String clientIp, int currentCount)
		throws IOException {

		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");

		String jsonResponse = String.format(
			"{\"code\":\"%s\",\"message\":\"%s\"}",
			ErrorCode.EXCEED_REQUEST_LIMIT.getStatus().toString(),
			ErrorCode.EXCEED_REQUEST_LIMIT.getMessage() + String.format("[요청 횟수 제한을 초과했습니다. IP: %s, 현재 요청수: %d]", clientIp, currentCount)
		);

		response.getWriter().write(jsonResponse);
		response.getWriter().flush();
	}

	private String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");

		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (ip != null && ip.contains(",")) {
			ip = ip.split(",")[0].trim();
		}

		log.debug("Extracted IP: {}", ip);

		return ip;
	}
}
