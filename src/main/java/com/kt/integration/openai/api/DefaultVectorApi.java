package com.kt.integration.openai.api;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import com.kt.common.vector.VectorApi;
import com.kt.integration.openai.client.OpenAIClient;
import com.kt.integration.openai.dto.request.VectorCreateRequest;
import com.kt.integration.openai.dto.request.VectorUploadFileRequest;
import com.kt.integration.properties.OpenAIProperties;

@Component
@Profile("!test")
public class DefaultVectorApi implements VectorApi {
	private final OpenAIClient openAIClient;
	private final OpenAIProperties openAIProperties;
	private final String token;

	public DefaultVectorApi(OpenAIClient openAIClient, OpenAIProperties openAIProperties) {
		this.openAIClient = openAIClient;
		this.openAIProperties = openAIProperties;
		this.token = "Bearer " + openAIProperties.openai().apiKey();
	}

	@Override
	public String create(String name, String description) {
		var response = openAIClient.create(
			token,
			new VectorCreateRequest(name, description)
		);
		return response.id();
	}

	@Override
	public String uploadFile(String vectorStoreId, byte[] json) {
		var map = new LinkedMultiValueMap<String, Object>();

		var fileResource = new ByteArrayResource(
			json
		) {
			@Override
			public String getFilename() {
				return String.format("%s.json", UUID.randomUUID());
			}
		};

		map.add("purpose", "assistants");
		map.add("file", fileResource);

		var response = openAIClient.upload(
			token,
			map
		);

		openAIClient.uploadVectorStore(
			vectorStoreId,
			token,
			new VectorUploadFileRequest(response.id())
		);

		return response.id();
	}

	@Override
	public void delete(String vectorStoreId, String fileId) {
		openAIClient.delete(
			vectorStoreId,
			fileId,
			token
		);

		openAIClient.deleteFile(
			fileId,
			token
		);
	}
}
