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
		return clientClient.prompt()
			.system("""
				너는 고객센터 챗봇이다.
				제공된 참고 정보를 바탕으로 질문에 답변하되,
				최종 답변 문장만 출력해라.
				설명, 출처, 문맥, JSON 형식은 절대 포함하지 마라.
				한두 문장으로 간결하게 답변해라.
				""")
			.user(query)
			.call()
			.content();
	}
}
