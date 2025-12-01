package com.kt.controller.user;

import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.auth.TokenResponse;
import com.kt.dto.user.request.*;
import com.kt.dto.user.response.UserInfoResponse;
import com.kt.dto.user.response.UserLoginResponse;
import com.kt.security.CustomUserDetails;
import com.kt.common.response.ApiResult;
import com.kt.service.auth.AuthService;
import com.kt.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController extends SwaggerAssistance {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입")
    public ApiResult<Void> create(@Valid @RequestBody UserCreateRequest request){
        userService.create(request);
        return ApiResult.ok();
    }

    @PostMapping("auth/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "로그인")
    public ApiResult<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request){
        UserLoginResponse response =  userService.login(request);
        return ApiResult.ok(response);
    }

    @PostMapping("auth/logout")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "로그아웃")
    public ApiResult<Void> logout(@Valid @RequestBody UserLogoutRequest request) {
        userService.logout(request);
        return ApiResult.ok();
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "내 정보 조회")
    public ApiResult<UserInfoResponse> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
       UserInfoResponse userInfoResponse = userService.getMyInfo(userDetails);
        return ApiResult.ok(userInfoResponse);
    }

    @PatchMapping("/update/myinfo")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "내 정보 수정")
    public ApiResult<Void> updateMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UserUpdateRequest request){
        userService.updateMyInfo(userDetails,request);
        return ApiResult.ok();
    }
    @PatchMapping("/update/password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "비밀번호 변경")
    public ApiResult<Void> changePassword(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody UserChangePassword request){
        userService.ChangePassword(userDetails, request);
        return ApiResult.ok();
    }

    @DeleteMapping("/withdraw")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "회원탈퇴")
    public ApiResult<Void> withDraw(@AuthenticationPrincipal CustomUserDetails userDetails){
        userService.WithDraw(userDetails);
        return ApiResult.ok();
    }

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "만료된 토큰 재발급")
    public ApiResult<TokenResponse> reissue(TokenResponse request){
        TokenResponse newToken = authService.reissue(request.accessToken(), request.refreshToken());
        return ApiResult.ok(newToken);
    }

}
