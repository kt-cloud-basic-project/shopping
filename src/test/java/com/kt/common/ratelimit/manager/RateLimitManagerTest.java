package com.kt.common.ratelimit.manager;

import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.ratelimit.annotation.KeyType;
import com.kt.common.ratelimit.annotation.RateLimit;

import io.github.bucket4j.distributed.proxy.ProxyManager;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class RateLimitManagerTest {

	@Autowired
	private RateLimitManager rateLimitManager;

	@Autowired
	private ProxyManager<String> proxyManager;

	// 테스트에서 사용한 키들을 추적
	private final Set<String> usedKeys = new HashSet<>();

	private final static String DEFAULT_KEY = "test:user:1:POST:/api/reviews";

	@AfterEach
	void tearDown() {
		usedKeys.forEach(proxyManager::removeProxy);
		usedKeys.clear();
	}

	private String trackKey(String key) {
		usedKeys.add(key);
		return key;
	}

	private RateLimit createRateLimit(int capacity, int refillTokens, int refillSeconds) {
		return new RateLimit() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return RateLimit.class;
			}

			@Override
			public int capacity() {
				return capacity;
			}

			@Override
			public int refillTokens() {
				return refillTokens;
			}

			@Override
			public int refillSeconds() {
				return refillSeconds;
			}

			@Override
			public KeyType keyType() {
				return KeyType.USER_ID;
			}
		};
	}

	@Test
	void 토큰_소비_성공() {
		// given
		RateLimit rateLimit = createRateLimit(5, 5, 60);

		//when
		boolean result = rateLimitManager.tryConsume(trackKey(DEFAULT_KEY), rateLimit);

		//then
		assertThat(result).isTrue();
	}

	@Test
	void 토큰_소진_후_소비_실패() {

		// given
		String key = trackKey(DEFAULT_KEY);

		RateLimit rateLimit = createRateLimit(3, 3, 60);

		// when
		for (int i = 0; i < 3; i++) {
			boolean result = rateLimitManager.tryConsume(key, rateLimit);
			assertThat(result).isTrue();
		}

		// then
		boolean result = rateLimitManager.tryConsume(key, rateLimit);
		assertThat(result).isFalse();
	}

	@Test
	void 남은_토큰_수_조회() {

		// given
		String key = trackKey(DEFAULT_KEY);

		RateLimit rateLimit = createRateLimit(5, 5, 60);

		// when
		rateLimitManager.tryConsume(key, rateLimit);
		rateLimitManager.tryConsume(key, rateLimit);

		// then
		long availableTokens = rateLimitManager.getAvailableTokens(key, rateLimit);
		assertThat(availableTokens).isEqualTo(3);
	}

	@Test
	void 서로_다른_키로_토큰_소비() {

		// given
		String key = trackKey(DEFAULT_KEY);

		String otherKey = "test:user:7:POST:/api/reviews"; // 7번
		RateLimit rateLimit = createRateLimit(3, 3, 60);

		//when
		for (int i = 0; i < 3; i++) {
			rateLimitManager.tryConsume(key, rateLimit);
		}

		boolean result = rateLimitManager.tryConsume(otherKey, rateLimit);
		assertThat(result).isTrue();
	}

	@Test
	void 토큰_소진_시_대기_시간_조회() {
		// given
		String key = trackKey(DEFAULT_KEY);

		RateLimit rateLimit = createRateLimit(2, 2, 60);

		// when
		for (int i = 0; i < 2; i++) {
			rateLimitManager.tryConsume(key, rateLimit);
		}

		// then
		long waitTime = rateLimitManager.getSecondsToWait(key, rateLimit);
		assertThat(waitTime).isGreaterThan(0);
		assertThat(waitTime).isLessThanOrEqualTo(60);
	}
}