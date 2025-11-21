package com.kt.controller.review;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController{
	@PostMapping("/user")
	@ResponseStatus(HttpStatus.CREATED)
	public void create(){


	}
}
