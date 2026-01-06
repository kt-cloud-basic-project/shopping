package com.kt.integration.openai.initializer;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.vector.Vector;
import com.kt.domain.vector.VectorType;
import com.kt.repository.vector.VectorRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class VectorStoreInitializer {

	private final VectorRepository vectorRepository;

	@PostConstruct
	@Transactional
	public void init() {
		String storeId = "vs_695c6f5c5c308191ac81f6368270b637";

		// type 기준으로 체크
		if (vectorRepository.existsByType(VectorType.FAQ)) {
			return;
		}

		vectorRepository.save(
			new Vector(
				VectorType.FAQ,
				storeId,
				"FAQ_VectorStore",
				"FAQ 챗봇용 벡터 스토어"
			)
		);
	}
}

