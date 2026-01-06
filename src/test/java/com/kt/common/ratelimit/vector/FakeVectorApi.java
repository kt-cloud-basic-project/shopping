package com.kt.common.ratelimit.vector;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.kt.common.vector.VectorApi;

// 테스트용 Fake 구현체
@Profile("test")
@Component
public class FakeVectorApi implements VectorApi {

	@Override
	public String create(String name, String description) {
		// 테스트용 더미 vector store id
		return "fake-vector-store-id";
	}

	@Override
	public String uploadFile(String vectorStoreId, byte[] json) {
		// 테스트용 더미 file id
		return "fake-file-id";
	}

	@Override
	public void delete(String vectorStoreId, String fileId) {
		// 테스트에서는 아무 작업도 하지 않음
	}
}
