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
import com.kt.common.support.ClientIpResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Slf4j
@Component
@ConditionalOnProperty(name = "ratelimit.enabled", havingValue = "true", matchIfMissing = true)
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

		String clientIp = ClientIpResolver.getClientIp(request);

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
			ErrorCode.GLOBAL_EXCEED_REQUEST_LIMIT.getStatus().toString(),
			ErrorCode.GLOBAL_EXCEED_REQUEST_LIMIT.getMessage() + String.format("[IP: %s, 현재 요청수: %d]", clientIp, currentCount)
		);

		response.getWriter().write(jsonResponse);
		response.getWriter().flush();
	}
}
