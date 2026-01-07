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
            너의 작업은 "복사-붙여넣기"이다.

            규칙:
            [참고 정보]에 있는 answer 텍스트를 글자 단위로 그대로 출력한다.
            요약/의역/정리/문장부호 변경/공백 변경을 절대 하지 않는다.
            [참고 정보]가 없으면 정확히 다음만 출력한다: 죄송하지만, 현재 FAQ에 등록된 정보 외에는 답변할 수 없습니다.""").user(query).call().content();
	}
}