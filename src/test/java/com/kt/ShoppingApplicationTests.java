package com.kt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import com.kt.integration.openai.api.OpenAIChatApi;
import com.kt.service.chat.ChatService;

@ActiveProfiles("test")
@SpringBootTest
class ShoppingApplicationTests {

	@TestConfiguration
	static class TestConfig {
		// 테스트 환경에서 OpenAIChatApi DI를 통과시키기 위한 Fake 구현
		@Bean
		public OpenAIChatApi fakeChatApi() {
			return new OpenAIChatApi() {
				@Override
				public String search(String query) {
					// 실제 OpenAI 호출 없이 고정 답변 반환
					return "이것은 테스트용 답변입니다.";
				}
			};
		}
	}

	private final ChatService chatService;

	ShoppingApplicationTests(ChatService chatService) {
		this.chatService = chatService;
	}

    @Test
	void contextLoads() {
	}

}
