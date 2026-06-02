package com.kt.common.ratelimit.key;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.common.ratelimit.annotation.KeyType;
import com.kt.common.support.ClientIpResolver;
import com.kt.security.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@Slf4j
@ConditionalOnProperty(name = "ratelimit.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitKeyGenerator {

	public String generateKey(KeyType keyType, HttpServletRequest request, String httpMethod, String path) {
		String identifier = extractIdentifier(keyType, request);

		return String.format(
			"rate_limit:%s:%s:%s:%s",
			keyType.name().toLowerCase(),
			identifier,
			httpMethod,
			path
		);
	}

	private String extractIdentifier(KeyType keyType, HttpServletRequest request) {
		return switch (keyType) {
			case IP -> ClientIpResolver.getClientIp(request);
			case USER_ID -> extractUserId().toString();
			case IP_AND_USER -> ClientIpResolver.getClientIp(request) + ":" + extractUserId();
			case GLOBAL -> "global";
		};
	}

	private Long extractUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// USER_ID는 무조건 인증된 사용자만 접근해야한다
		if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
			throw new CustomException(ErrorCode.FORBIDDEN);
		}

		return userDetails.getId();
	}
}
