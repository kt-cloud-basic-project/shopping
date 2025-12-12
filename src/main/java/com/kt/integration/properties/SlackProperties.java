package com.kt.integration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
public record SlackProperties(
	String botToken,
	String logChannel
) {
}
