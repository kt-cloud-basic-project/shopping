package com.kt.controller.certify;

import com.kt.common.response.ApiResult;
import com.kt.common.support.SwaggerAssistance;
import com.kt.dto.certify.EmailCertificationRequest;
import com.kt.dto.certify.EmailRequest;
import com.kt.service.certify.CertifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Certify", description = "이메일 검증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certify")
public class CertifyController extends SwaggerAssistance{

    private final CertifyService certifyService;

    @PostMapping("/code")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "이메일 검증 코드 생성")
    public ApiResult<Void> createCode(@RequestBody EmailRequest email){
        certifyService.createCode(email.email());
        return ApiResult.ok();
    }

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "코드를 통한 이메일 검증")
    public ApiResult<Void> certify(@Valid @RequestBody EmailCertificationRequest emailCertificationRequest){
        certifyService.certifyEmail(emailCertificationRequest);
        return ApiResult.ok();
    }
}
