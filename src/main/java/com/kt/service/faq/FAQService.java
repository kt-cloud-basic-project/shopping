package com.kt.service.faq;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.common.vector.VectorApi;
import com.kt.domain.faq.FAQ;
import com.kt.domain.vector.VectorType;
import com.kt.dto.faq.FAQCreateRequest;
import com.kt.repository.faq.FAQRepository;
import com.kt.repository.vector.VectorRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FAQService {
	private final FAQRepository fAQRepository;
	private final VectorApi vectorApi;
	private final VectorRepository vectorRepository;
	private final ObjectMapper objectMapper;

	public void create(FAQCreateRequest request) throws Exception {
		var faq = fAQRepository.save(
			new FAQ(
				request.title(),
				request.content(),
				request.category()
			)
		);

		var vector = vectorRepository.findByType(VectorType.FAQ)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VECTOR_STORE));

		var fileId = vectorApi.uploadFile(vector.getStoreId(), objectMapper.writeValueAsBytes(faq));

		faq.updateFileId(fileId);
	}

	public void delete(Long id) {
		var faq = fAQRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.NOT_FOUND_FAQ)
		);

		var vector = vectorRepository.findByType(VectorType.FAQ)
			.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_VECTOR_STORE));

		vectorApi.delete(vector.getStoreId(), faq.getFileId());

		fAQRepository.delete(faq);
	}
}
