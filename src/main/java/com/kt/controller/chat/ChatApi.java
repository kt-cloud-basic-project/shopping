package com.kt.controller.chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kt.common.response.ApiResult;
import com.kt.service.chat.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatApi {
	private final ChatService chatService;

	@GetMapping
	public ApiResult<String> question(@RequestParam String query) {
		return ApiResult.ok(chatService.questions(query));
	}
}