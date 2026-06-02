package com.kt.common.ratelimit.aspect;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.common.ratelimit.annotation.RateLimit;
import com.kt.common.ratelimit.key.RateLimitKeyGenerator;
import com.kt.common.ratelimit.manager.RateLimitManager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "ratelimit.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitAspect {

	private final RateLimitManager rateLimitManager;
	private final RateLimitKeyGenerator keyGenerator;

	@Around("@annotation(rateLimit)")
	public Object handleRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
		HttpServletRequest request = getCurrentRequest();
		String httpMethod = request.getMethod();
		String path = request.getRequestURI();

		// ratelimit:user_id(key_type):1(request):GET(method):_api_reviews_me(path)
		String key = keyGenerator.generateKey(rateLimit.keyType(), request, httpMethod, path);

		if (!rateLimitManager.tryConsume(key, rateLimit)) {
			// 제한 초과 - 응답 헤더 추가 후 예외 발생
			long secondsToWait = rateLimitManager.getSecondsToWait(key, rateLimit);
			addHeaderExceeded(rateLimit, secondsToWait);

			throw new CustomException(ErrorCode.API_EXCEED_REQUEST_LIMIT);
		}

		long availableTokens = rateLimitManager.getAvailableTokens(key, rateLimit);
		addHeaderSuccess(rateLimit, availableTokens);

		return joinPoint.proceed();
	}

	private HttpServletRequest getCurrentRequest() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

		if (attributes == null) {
			throw new CustomException(ErrorCode.NO_HTTP_REQUEST);
		}

		return attributes.getRequest();
	}

	private void addHeaderSuccess(RateLimit rateLimit, long availableTokens) {
		HttpServletResponse response = ((ServletRequestAttributes)Objects.requireNonNull(
			RequestContextHolder.getRequestAttributes())).getResponse();

		if (response != null) {
			response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.capacity()));
			response.setHeader("X-RateLimit-Remaining", String.valueOf(availableTokens));

			long resetTime = System.currentTimeMillis() / 1000 + rateLimit.refillSeconds();
			response.setHeader("X-RateLimit-Reset", String.valueOf(resetTime));
		}
	}

	private void addHeaderExceeded(RateLimit rateLimit, long secondsToWait) {
		HttpServletResponse response = ((ServletRequestAttributes)Objects.requireNonNull(
			RequestContextHolder.getRequestAttributes())).getResponse();

		if (response != null) {
			response.setHeader("X-RateLimit-Limit", String.valueOf(rateLimit.capacity()));
			response.setHeader("X-RateLimit-Remaining", "0");

			long resetTime = System.currentTimeMillis() / 1000 + secondsToWait;
			response.setHeader("X-RateLimit-Reset", String.valueOf(resetTime));
			response.setHeader("Retry-After", String.valueOf(secondsToWait));
		}
	}
}
