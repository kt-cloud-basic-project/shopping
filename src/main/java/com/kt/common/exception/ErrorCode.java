package com.kt.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	NOT_FOUND_PRODUCT(HttpStatus.BAD_REQUEST, "상품을 찾을 수 없습니다."),
	NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 회원입니다."),
	FAIL_LOGIN(HttpStatus.BAD_REQUEST, "아이디 혹은 비밀번호가 일치하지 않습니다."),
    DUPLICATED_LOGIN_ID(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
	DOES_NOT_MATCH_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호가 일치하지 않습니다."),
    CAN_NOT_ALLOWED_SAME_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일한 비밀번호로 변경할 수 없습니다."),
    INVALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    MALFORMED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 형식의 JWT 토큰입니다."),
    JWT_CLAIM_ERROR(HttpStatus.UNAUTHORIZED, "JWT Claim 정보가 잘못되었습니다."),
    NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "필수값 누락입니다."),
	FAIL_ACQUIRED_LOCK(HttpStatus.BAD_REQUEST, "락 획득에 실패했습니다."),
	ERROR_SYSTEM(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 오류가 발생했습니다."),
	INVALID_REVIEW_STAR(HttpStatus.BAD_REQUEST, "리뷰 평점은 1부터 5까지 가능합니다."),
	NOT_REVIEW_AUTHOR(HttpStatus.FORBIDDEN, "리뷰 작성자만 수정가능합니다."),
	NOT_FOUND_REVIEW(HttpStatus.BAD_REQUEST, "리뷰를 찾을 수 없습니다."),
	NOT_ADDRESS_OWNER(HttpStatus.FORBIDDEN, "본인의 배송지만 수정/삭제할 수 있습니다."),
	EXCEED_MAX_ADDRESS_COUNT(HttpStatus.BAD_REQUEST, "배송지는 최대 5개까지만 등록 가능합니다."),
	REQUIRED_DEFAULT_ADDRESS(HttpStatus.BAD_REQUEST, "기본 배송지는 최소 1개 이상 필요합니다."),
	CANNOT_DELETE_DEFAULT_ADDRESS(HttpStatus.BAD_REQUEST, "기본 배송지는 삭제할 수 없습니다. 다른 배송지를 기본으로 설정 후 삭제해주세요."),
	NOT_FOUND_CATEGORY(HttpStatus.BAD_REQUEST, "카테고리를 찾을 수 없습니다."),
	ALREADY_EXIST_MEMBERSHIP_LEVEL(HttpStatus.BAD_REQUEST, "이미 존재하는 멤버십 등급입니다."),
	NOT_FOUND_MEMBERSHIP(HttpStatus.BAD_REQUEST, "멤버십을 찾을 수 없습니다."),
	DISCOUNT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "해당 멤버십은 이미 할인 정책이 존재합니다"),
	NOT_FOUND_DISCOUNT(HttpStatus.BAD_REQUEST, "할인 정책을 찾을 수 없습니다."),

	;

	private final HttpStatus status;
	private final String message;

}

