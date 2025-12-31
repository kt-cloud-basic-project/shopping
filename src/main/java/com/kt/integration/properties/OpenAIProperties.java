package com.kt.integration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
public record OpenAIProperties(
	OpenAI openai,
	Model model
) {
	public record OpenAI(
		Chat chat,
		String apiKey
	) {
		public record Chat(
			ChatOptions options
		) {
			public record ChatOptions(
				int temperature,
				String model
			) {}
		}
	}

	public record Model(
		String embedding
	) {}
}
