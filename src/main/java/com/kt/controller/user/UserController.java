package com.kt.controller.user;

import com.kt.common.ApiResult;
import com.kt.dto.user.UserCreateRequest;
import com.kt.dto.user.UserLoginRequest;
import com.kt.dto.user.UserLoginResponse;
import com.kt.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<Void> create(@Valid @RequestBody UserCreateRequest request){
        userService.create(request);
        return ApiResult.ok();
    }

    @PostMapping("auth/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResult<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request){
        UserLoginResponse response =  userService.login(request);
        return ApiResult.ok(response);
    }
}
