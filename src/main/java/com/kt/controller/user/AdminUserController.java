package com.kt.controller.user;

import com.kt.common.response.ApiResult;
import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.user.request.*;
import com.kt.dto.user.response.UserInfoResponse;
import com.kt.dto.user.response.UserListResponse;
import com.kt.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin User", description = "유저 관리자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController extends SwaggerAssistance {
    private final UserService userService;

    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "관리자 회원가입")
    public void adminCreate(@Valid @RequestBody UserCreateRequest request){
        userService.createAdmin(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "유저 정보 조회")
    public ApiResult<UserInfoResponse> userInfo(@Valid @RequestBody UserIdRequest request){
        UserInfoResponse userInfo = userService.getUserInfo(request.userId());
        return ApiResult.ok(userInfo);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "유저목록 조회")
    public ApiResult<Page<UserListResponse>> userList(Pageable pageable){
        Page<UserListResponse> userListResponse = userService.getUserList(pageable);
        return ApiResult.ok(userListResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "유저 정보 수정")
    public ApiResult<Void> updateUserInfo(@Valid @RequestBody UserAdminUpdateRequest request){
        userService.updateUserInfo(request);
        return ApiResult.ok();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "유저 비밀번호 변경")
    public ApiResult<Void> changeUserPassword(@Valid @RequestBody UserAdminChangePassword request){
        userService.adminChangePassword(request);
        return ApiResult.ok();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/role")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "권한변경")
    public ApiResult<Void> changeUserRole(@Valid @RequestBody UserChangeRole request){
        userService.changeRole(request);
        return ApiResult.ok();
    }
}
