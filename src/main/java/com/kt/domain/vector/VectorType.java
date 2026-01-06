package com.kt.domain.vector;

import java.util.List;

public enum VectorType {
	FAQ;

	public static List<VectorType> chatbotRange() {
		return List.of(FAQ);
	}
}
