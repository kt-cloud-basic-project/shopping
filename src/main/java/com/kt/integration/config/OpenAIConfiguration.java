package com.kt.integration.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.kt.integration.openai.client.OpenAIClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(
	prefix = "spring.ai.openai",
	name = "enabled",
	havingValue = "true"
)
public class OpenAIConfiguration {
	@Bean
	public ChatClient chatClient(ChatClient.Builder builder, BaseAdvisor openAICustomAdvisor) {
		return builder
			.defaultAdvisors(openAICustomAdvisor)
			.build();
	}

	@Bean
	public OpenAIClient openAIClient(RestClient.Builder builder) {
		RestClient restClient = builder.build();
		HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();

		return factory.createClient(OpenAIClient.class);
	}
}