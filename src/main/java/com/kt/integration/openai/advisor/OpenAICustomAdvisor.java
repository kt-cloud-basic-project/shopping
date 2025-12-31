package com.kt.integration.openai.advisor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.stereotype.Component;

import com.kt.integration.openai.client.OpenAIClient;
import com.kt.integration.openai.dto.request.VectorSearchRequest;
import com.kt.integration.openai.dto.response.OpenAIResponse.SearchData;
import com.kt.integration.properties.OpenAIProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OpenAICustomAdvisor implements BaseAdvisor {
	private static final SearchData EMPTY_SEARCH_DATA =
		new SearchData("", "", 0.0, null, null);

	private final OpenAIClient openAIClient;
	private final OpenAIProperties openAIProperties;

	@NotNull
	@Override
	public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
		var prompt = chatClientRequest.prompt();
		var message = prompt.getUserMessage().getText();
		var candidateAnswers = new ArrayList<SearchData>();

		var parsing = message.split(":", 2);
		if (parsing.length < 2 || parsing[1].isBlank()) {
			return chatClientRequest;
		}

		var request = new VectorSearchRequest(parsing[1]);

		var ids = parsing[0].split(",");

		Arrays.stream(ids).forEach(id -> {
			var response = openAIClient.search(id, String.format("Bearer %s", openAIProperties.openai().apiKey()), request);

			var searchData = response.data().stream().max(Comparator.comparingDouble(SearchData::score))
				.orElse(EMPTY_SEARCH_DATA);

			candidateAnswers.add(searchData);
		});

		var topScoreSearchData = candidateAnswers.stream().max(Comparator.comparingDouble(SearchData::score))
			.orElse(EMPTY_SEARCH_DATA);

		var context = topScoreSearchData.content() == null
			? ""
			: topScoreSearchData.content().toString();

		var newPrompt = prompt.augmentSystemMessage(context);

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
}
