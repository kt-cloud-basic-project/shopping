package com.kt.common.support;

public class ObjectUtils {
	public static <T> T orElse(T newValue, T originalValue) {
		return newValue != null ? newValue : originalValue;
	}

    public static <T> T orElseIfEmpty(T newValue, T oldValue) {
        if (newValue == null) return oldValue;
        if (newValue instanceof String && ((String) newValue).isBlank()) return oldValue;
        return newValue;
    }

}
