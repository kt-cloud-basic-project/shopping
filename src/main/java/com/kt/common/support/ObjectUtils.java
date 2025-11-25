package com.kt.common.support;

public class ObjectUtils {
	public static <T> T orElse(T newValue, T originalValue) {
		return newValue != null ? newValue : originalValue;
	}
}
