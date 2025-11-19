package com.kt.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 *packageName    : com.kt.dto.review
 * fileName       : ReviewCreateReqeust
 * author         : howee
 * date           : 2025-11-18
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2025-11-18        howee       최초 생성
 */
public record ReviewCreateReqeust(

	@NotBlank
	String title,

	@NotBlank
	String description,

	@NotNull
	Integer star
) {
}
