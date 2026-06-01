package com.kt.common.ratelimit.manager;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.kt.common.ratelimit.annotation.RateLimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "ratelimit.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitManager {

	private final ProxyManager<String> proxyManager;

	// 토큰 검증 및 소비
	public boolean tryConsume(String key, RateLimit rateLimit) {
		BucketConfiguration configuration = buildConfiguration(rateLimit);

		boolean success = proxyManager.builder()
			.build(key, configuration)
			.tryConsume(1);

		if (!success) {
			log.warn("Rate limit exceeded - key: {}, limit: {}, {} seconds", key, rateLimit.refillTokens(), rateLimit.refillSeconds());
		}

		return success;
	}

	// 남은 토큰 수 조회
	public long getAvailableTokens(String key, RateLimit rateLimit) {
		BucketConfiguration configuration = buildConfiguration(rateLimit);

		return proxyManager.builder()
			.build(key, configuration)
			.getAvailableTokens();
	}

	//다음 대기시간
	public long getSecondsToWait(String key, RateLimit rateLimit) {
		BucketConfiguration configuration = buildConfiguration(rateLimit);

		ConsumptionProbe probe = proxyManager.builder()
			.build(key, configuration)
			.tryConsumeAndReturnRemaining(1);

		if (probe.isConsumed()) {
			return 0;
		}

		return probe.getNanosToWaitForRefill() / 1_000_000_000;
	}

	// token bucket 설w정
	private BucketConfiguration buildConfiguration(RateLimit rateLimit) {
		Bandwidth limit = Bandwidth.builder()
			.capacity(rateLimit.capacity())
			.refillGreedy(rateLimit.refillTokens(), Duration.ofSeconds(rateLimit.refillSeconds()))
			.build();

		return BucketConfiguration.builder()
			.addLimit(limit)
			.build();
	}
}
