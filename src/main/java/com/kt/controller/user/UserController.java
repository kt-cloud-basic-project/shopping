package com.kt.controller.user;

import com.kt.dto.user.UserInfoResponse;
import com.kt.security.CustomUserDetails;
import com.kt.common.response.ApiResult;
import com.kt.dto.user.UserCreateRequest;
import com.kt.dto.user.UserLoginRequest;
import com.kt.dto.user.UserLoginResponse;
import com.kt.dto.user.UserLogoutRequest;
import com.kt.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PostMapping("auth/logout")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResult<Void> logout(@Valid @RequestBody UserLogoutRequest request) {
        userService.logout(request);
        return ApiResult.ok();
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public ApiResult<UserInfoResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
       UserInfoResponse userInfoResponse = userService.getMyInfo(userDetails);
        return ApiResult.ok(userInfoResponse);
    }
}
