package com.kt.controller.user;

import com.kt.common.response.ApiResult;
import com.kt.dto.user.request.*;
import com.kt.dto.user.response.UserInfoResponse;
import com.kt.dto.user.response.UserListResponse;
import com.kt.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final UserService userService;

    @PostMapping("/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void adminCreate(@Valid @RequestBody UserCreateRequest request){
        userService.createAdmin(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResult<UserInfoResponse> userInfo(@Valid @RequestBody UserIdRequest request){
        UserInfoResponse userInfo = userService.getUserInfo(request.userId());
        return ApiResult.ok(userInfo);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResult<Page<UserListResponse>> userList(Pageable pageable){
        Page<UserListResponse> userListResponse = userService.getUserList(pageable);
        return ApiResult.ok(userListResponse);
    }

    //유저 정보 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResult<Void> updateUserInfo(@Valid @RequestBody UserAdminUpdateRequest request){
        userService.updateUserInfo(request);
        return ApiResult.ok();
    }
    //유저 비밀번호 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResult<Void> changeUserPassword(@Valid @RequestBody UserAdminChangePassword request){
        userService.adminChangePassword(request);
        return ApiResult.ok();
    }
    //권한변경
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update/role")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResult<Void> changeUserRole(@Valid @RequestBody UserChangeRole request){
        userService.changeRole(request);
        return ApiResult.ok();
    }
}
