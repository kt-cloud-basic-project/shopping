package com.kt.config;

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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	private static final String[] GET_PERMIT_ALL = {"/api/health/**", "/swagger-ui/**", "/v3/api-docs/**"};
	private static final String[] POST_PERMIT_ALL = {"/users", "/auth/login"};
	private static final String[] PUT_PERMIT_ALL = {"/api/v1/public/**"};
	private static final String[] PATCH_PERMIT_ALL = {"/api/v1/public/**"};
	private static final String[] DELETE_PERMIT_ALL = {"/api/v1/public/**"};

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
		// 기능 개발 및 테스트를 위해 주석처리
        /*http.sessionManagement(
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
			.csrf(AbstractHttpConfigurer::disable);*/
        // csrf 기능off, 요청인가 -> 누구나 가능하게
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/auth/signup", "/users/auth/login").permitAll()
                        .anyRequest().permitAll()
                );

		return http.build();
	}
}

