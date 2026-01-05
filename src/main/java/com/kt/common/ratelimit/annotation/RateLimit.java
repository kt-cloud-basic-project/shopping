package com.kt.common.ratelimit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

	// 용량
	int capacity() default 10;

	// 리필 토큰 갯수
	int refillTokens() default 10;

	// 리필 주기 현재는 초
	int refillSeconds() default 60;

	// 키타입(이 타입에 따라서 카페인에 저장됌)
	KeyType keyType() default KeyType.USER_ID;
}
