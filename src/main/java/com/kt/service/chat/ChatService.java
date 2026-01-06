package com.kt.service.chat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.vector.Vector;
import com.kt.domain.vector.VectorType;
import com.kt.integration.openai.api.OpenAIChatApi;
import com.kt.repository.vector.VectorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {
	private final OpenAIChatApi openAIChatApi;
	private final VectorRepository vectorRepository;

	public String questions(String query) {
		var ids = vectorRepository.findByTypeIn(VectorType.chatbotRange()).stream().map(Vector::getStoreId).toList();

		log.info("사용 중인 vector store ids = {}", ids);

		var newQuery = String.format("%s:%s", String.join(",", ids), query);

		return openAIChatApi.search(newQuery);
	}
}
