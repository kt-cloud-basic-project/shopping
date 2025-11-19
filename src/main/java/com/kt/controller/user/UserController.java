package com.kt.controller.user;

import com.kt.common.ApiResult;
import com.kt.dto.user.UserCreateRequest;
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
    public ApiResult<Void> create(@Valid @RequestBody UserCreateRequest.Create request){
        userService.create(request);
        return ApiResult.ok();
    }
}
