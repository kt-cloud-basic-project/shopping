package com.kt.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;

import com.kt.common.profile.AppProfile;
import com.kt.common.profile.DevProfile;
import com.kt.common.profile.LocalProfile;

@Profile("!test")
@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {
	private final RedisProperties redisProperties;

	@Bean
	@AppProfile
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
	@DevProfile
	public RedissonClient devRedissonClient() {
		var config = new Config();

		String host = redisProperties.getHost() + ":" + redisProperties.getPort();

		if(redisProperties.getCluster() != null &&
			redisProperties.getCluster().getNodes() != null &&
		!redisProperties.getCluster().getNodes().isEmpty()) {
			host = redisProperties.getCluster().getNodes().getFirst();
		}

		boolean isSSL = redisProperties.getSsl() != null
			&& redisProperties.getSsl().isEnabled();

		var protocol = isSSL ? "rediss" : "redis";
		var uri = String.format("%s://%s", protocol, host);

		config
			.useSingleServer()
			.setAddress(uri);

		return Redisson.create(config);
	}

	@Bean
	@LocalProfile
	public RedissonClient localRedissonClient() {
		var config = new Config();

		var host = redisProperties.getHost();
		var port = redisProperties.getPort();
		var uri = String.format("redis://%s:%s", host, port);

		config
			.useSingleServer()
			.setAddress(uri);

		return Redisson.create(config);
	}
}
