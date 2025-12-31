package com.kt.integration.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class OpenAIConfiguration {
	@Bean
	public ChatClient chatClient(ChatClient.Builder builder, BaseAdvisor openAICustomAdvisor) {
		return builder
			.defaultAdvisors(openAICustomAdvisor)
			.build();
	}
}