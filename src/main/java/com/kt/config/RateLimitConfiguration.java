package com.kt.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.github.benmanes.caffeine.cache.Caffeine;

import io.github.bucket4j.caffeine.CaffeineProxyManager;
import io.github.bucket4j.distributed.proxy.ProxyManager;

@Configuration
@EnableAspectJAutoProxy
public class RateLimitConfiguration {

	private static final int CACHE_MAX_SIZE = 100000;
	private static final int CACHE_EXP_MINUTES = 60;


	@Bean
	public ProxyManager<String> bucketProxyManager() {
		Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
			.maximumSize(CACHE_MAX_SIZE);

		return new CaffeineProxyManager<>(caffeineBuilder, Duration.ofMinutes(CACHE_EXP_MINUTES));
	}
}
