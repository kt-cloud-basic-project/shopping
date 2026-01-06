package com.kt.service.vector;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.common.vector.VectorApi;
import com.kt.domain.vector.Vector;
import com.kt.domain.vector.VectorType;
import com.kt.repository.vector.VectorRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VectorService {
	private final VectorRepository vectorRepository;
	private final VectorApi vectorApi;

	@PostConstruct
	void init() {
		if (!vectorRepository.existsByType(VectorType.FAQ)) {
			var name = "FAQ 벡터 스토어";
			var description = "자주 묻는 질문(FAQ) 데이터를 위한 벡터 스토어입니다.";

			var vectorStoreId = vectorApi.create(name, description);

			create(
				vectorStoreId,
				name,
				description,
				VectorType.FAQ
			);
		}
	}

	public void create(String storeId, String name, String description, VectorType type) {
		vectorRepository.save(
			new Vector(
				type,
				storeId,
				description,
				name
			)
		);
	}
}
