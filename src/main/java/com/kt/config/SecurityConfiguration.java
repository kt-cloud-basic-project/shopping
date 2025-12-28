package com.kt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.security.JwtAuthenticationFilter;
import com.kt.security.JwtTokenProvider;
import com.kt.security.blacklist.TokenBlacklistStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {


	private static final String[] GET_PERMIT_ALL = {"/api/health/**", "/swagger-ui.html","/swagger-ui/**", "/v3/api-docs/**", "/actuator/**"};
	private static final String[] POST_PERMIT_ALL = {"/api/users/auth/signup", "/api/users/auth/login","/api/admin/users/auth/signup", "/api/admin/users/auth/login","/api/users/reissue"};
	private static final String[] PUT_PERMIT_ALL = {"/api/v1/public/**"};
	private static final String[] PATCH_PERMIT_ALL = {"/api/v1/public/**"};
	private static final String[] DELETE_PERMIT_ALL = {"/api/v1/public/**"};
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final TokenBlacklistStore tokenBlacklistStore;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// 기능 개발 및 테스트를 위해 주석처리 (주석해제)
        // jwt를 사용해서 인증을 진행하기때문에 csrf protection을 적용할필요없기때문에 csrf를 disable
        http.sessionManagement(
				session ->
					session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests(
				request -> {
					request.requestMatchers(HttpMethod.GET, GET_PERMIT_ALL).permitAll();
					request.requestMatchers(HttpMethod.POST, POST_PERMIT_ALL).permitAll();
					request.requestMatchers(HttpMethod.PATCH, PATCH_PERMIT_ALL).permitAll();
					request.requestMatchers(HttpMethod.PUT, PUT_PERMIT_ALL).permitAll();
					request.requestMatchers(HttpMethod.DELETE, DELETE_PERMIT_ALL).permitAll();
					request.anyRequest().authenticated();
				}
			)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .csrf(AbstractHttpConfigurer::disable);

                http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider,objectMapper,tokenBlacklistStore), UsernamePasswordAuthenticationFilter.class);


		return http.build();
	}
}

