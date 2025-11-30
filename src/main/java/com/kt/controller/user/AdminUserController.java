package com.kt.controller.user;

import com.kt.common.response.ApiResult;
import com.kt.dto.user.request.UserCreateRequest;
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

@PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResult<UserInfoResponse> userInfo(@PathVariable String userId){
        UserInfoResponse userInfo = userService.getUserInfo(userId);
        return ApiResult.ok(userInfo);
    }
    @GetMapping("")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResult<Page<UserListResponse>> userList(Pageable pageable){
        Page<UserListResponse> userListResponse = userService.getUserList(pageable);
        return ApiResult.ok(userListResponse);
    }

    //유저 정보 수정
    //유저 비밀번호 수정
    //권한변경
}
