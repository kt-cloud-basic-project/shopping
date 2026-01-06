package com.kt.integration.openai.advisor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.domain.vector.Vector;
import com.kt.domain.vector.VectorType;
import com.kt.integration.openai.client.OpenAIClient;
import com.kt.integration.openai.dto.request.VectorSearchRequest;
import com.kt.integration.openai.dto.response.OpenAIResponse;
import com.kt.integration.openai.dto.response.OpenAIResponse.SearchData;
import com.kt.integration.properties.OpenAIProperties;
import com.kt.repository.vector.VectorRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
	prefix = "spring.ai.openai",
	name = "enabled",
	havingValue = "true"
)
public class OpenAICustomAdvisor implements BaseAdvisor {

	private static final SearchData EMPTY_SEARCH_DATA =
		new SearchData("", "", 0.0, null, null);

	private final OpenAIClient openAIClient;
	private final OpenAIProperties openAIProperties;
	private final ObjectMapper objectMapper;
	private final VectorRepository vectorRepository;

	@NotNull
	@Override
	public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
		var prompt = chatClientRequest.prompt();
		var message = prompt.getUserMessage().getText();

		var parsing = message.split(":", 2);
		if (parsing.length < 2 || parsing[1].isBlank()) {
			return chatClientRequest;
		}

		var request = new VectorSearchRequest(parsing[1]);
		var ids = parsing[0].split(",");

		var candidateAnswers = new ArrayList<SearchData>();

		Arrays.stream(ids).forEach(id -> {
			var response = openAIClient.search(
				id,
				String.format("Bearer %s", openAIProperties.openai().apiKey()),
				request
			);

			var searchData = response.data().stream()
				.max(Comparator.comparingDouble(SearchData::score))
				.orElse(EMPTY_SEARCH_DATA);

			candidateAnswers.add(searchData);
		});

		var topScoreSearchData = candidateAnswers.stream()
			.max(Comparator.comparingDouble(SearchData::score))
			.orElse(EMPTY_SEARCH_DATA);

		String answer = "";
		if (topScoreSearchData.content() != null) {
			answer = topScoreSearchData.content().stream()
				.map(c -> extractAnswerFromVectorResult(c.text()))
				.filter(s -> s != null && !s.isBlank())
				.findFirst()
				.orElse("");
		}

		String contextText;

		// FAQ가 없으면 안내, 있으면 FAQ JSON 기반 답변
		if (answer.isBlank()) {
			contextText = "죄송하지만, 현재 FAQ에 등록된 정보 외에는 답변할 수 없습니다.";
		}

		// FAQ가 있을 경우 기존 로직 그대로
		contextText = """
        다음은 참고 정보이다.
        아래 내용을 기반으로 사용자의 질문에 답변하되,
        최종 답변 문장만 출력하라.

        [참고 정보]
        %s
        """.formatted(answer);

		var newPrompt = prompt.augmentSystemMessage(contextText);

		return chatClientRequest.mutate()
			.prompt(newPrompt)
			.build();
	}

	@Override
	public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
		return chatClientResponse;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	// query 매칭 제거, answer만 추출
	private String extractAnswerFromVectorResult(String vectorText) {
		try {
			JsonNode arrayNode = objectMapper.readTree(vectorText);
			if (arrayNode.isArray()) {
				for (JsonNode node : arrayNode) {
					if (node.has("answer")) {
						return node.get("answer").asText();
					}
				}
			}
		} catch (Exception e) {
			// ignore
		}
		return "";
	}
}

