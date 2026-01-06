package com.kt.integration;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.kt.integration.openai.api.OpenAIChatApi;

@Component
@Profile("test")
public class FakeChatApi implements OpenAIChatApi {

	@Override
	public String search(String query) {
		// CI 테스트용, 실제 답변 필요 없음
		return "테스트용 답변";
	}
}
