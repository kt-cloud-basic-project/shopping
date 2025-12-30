package com.kt.common.ratelimit.key;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.kt.common.ratelimit.annotation.KeyType;
import com.kt.common.support.ClientIpResolver;
import com.kt.security.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
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

		if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
			return userDetails.getId();
		}

		return 0L;
	}
}
