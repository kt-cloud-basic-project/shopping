package com.kt.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import com.kt.common.profile.LocalProfile;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {
	private final RedisProperties redisProperties;

	@Bean
	public RedissonClient redissonClient() {
		var config = new Config();
		var host = redisProperties.getCluster().getNodes().getFirst();
		var uri = String.format("rediss://%s", host);

		config
			.useSingleServer()
			.setAddress(uri);

		return Redisson.create(config);
	}

	@Bean
	@LocalProfile
	public RedissonClient localRedissonClient() {
		var config = new Config();
		var host = redisProperties.getCluster().getNodes().getFirst();
		var uri = String.format("rediss://%s", host);

		config
			.useSingleServer()
			.setAddress(uri);

		return Redisson.create(config);
	}
}
