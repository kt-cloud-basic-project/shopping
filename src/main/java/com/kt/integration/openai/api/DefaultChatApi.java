package com.kt.integration.openai.api;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
	prefix = "spring.ai.openai",
	name = "enabled",
	havingValue = "true"
)
public class DefaultChatApi implements OpenAIChatApi {
	private final ChatClient clientClient;

	@Override
	public String search(String query) {
		var response = clientClient.prompt()
			.user(query)
			.call()
			.content();
		return response;
	}
}
