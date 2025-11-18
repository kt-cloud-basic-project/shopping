package com.kt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.dto.UserCreateRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	// private final UserService userService;

	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
	//loginId, password, name, birthday
	//json 형태의 body에 담겨서 post 요청으로 /uSERS로 들어오면
	//@RequestBody를 보고 jackson object mapper가 동작해서 json을 읽어서 dto로 변환
	public void create(@RequestBody UserCreateRequest request) {
		// userService.create(request);
	}
}
