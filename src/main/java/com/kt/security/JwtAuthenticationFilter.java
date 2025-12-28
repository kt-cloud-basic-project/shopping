package com.kt.security;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;

import com.kt.common.response.ApiResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request,response);
            return;
        }
        try{
            jwtTokenProvider.validateAccessTokenOrThrow(token);

            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request,response);

        } catch (CustomException e){
            setErrorResponse(response, e.getErrorCode());
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(AUTH_HEADER);

        if (!StringUtils.hasText(header) || !header.startsWith(BEARER_PREFIX)) {
            return null; // jwt가 없어야하는 회원가입등의 기능도 jwt에러를 뱉어서 수정
        }

        return header.substring(BEARER_PREFIX.length());
    }

    private void setErrorResponse(HttpServletResponse response,ErrorCode errorCode) throws IOException{
        response.setStatus(errorCode.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        ApiResult<Void> body = ApiResult.error(errorCode.name(),errorCode.getMessage());

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

}