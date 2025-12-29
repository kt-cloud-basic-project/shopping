package com.kt.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.membership.Membership;
import com.kt.domain.user.Gender;
import com.kt.domain.user.User;
import com.kt.dto.user.request.UserLoginRequest;
import com.kt.dto.user.request.UserLogoutRequest;
import com.kt.dto.user.response.UserLoginResponse;
import com.kt.repository.auth.RefreshTokenRepository;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.user.UserRepository;
import com.kt.service.auth.AuthService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RBucket;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthServiceTest {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private MembershipRepository membershipRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String AUTH_HEADER = "Authorization";

    @BeforeEach
    void init() {
        // 1) clean
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        membershipRepository.deleteAllInBatch();

        // 2) seed
        Membership membership = membershipRepository.save(new Membership("BRONZE"));

        userRepository.save(User.normalUser(
                "login_user01",
                passwordEncoder.encode("password123"),
                "로그인유저",
                "login@kt.com",
                "010-0000-0000",
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                membership
        ));

        System.out.println("init users=" + userRepository.count()
                + ", membership=" + membershipRepository.count()
                + ", refresh=" + refreshTokenRepository.count());
    }



    // 로그인 테스트
    // 1. 유저 로그인 성공
    @Test
    @DisplayName("로그인 성공 - 유저")
    void 로그인_성공() {

        UserLoginRequest request = new UserLoginRequest("login_user01", "password123");


        UserLoginResponse tokenResponse = userService.login(request);


        assertThat(tokenResponse.accessToken()).isNotBlank();
        assertThat(tokenResponse.refreshToken()).isNotBlank();
    }

    // 2. 유저 로그인 실패(아이디/비밀번호 오기입)
    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void 로그인_실패_비밀번호_불일치() {

        UserLoginRequest request = new UserLoginRequest("login_user01", "wrongPassword");


        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.FAIL_LOGIN.getMessage());
    }

    // 로그아웃 테스트
    // 1. 로그아웃 성공
    // http기반 테스트 추후 분리예정
    @Test
    @DisplayName("로그아웃 성공 - 리프레시 토큰 삭제")
    void 로그아웃_성공() throws Exception {

        System.out.println("before login found=" + userRepository.findByLoginIdAndIsDeletedFalse("login_user01"));

        UserLoginResponse tokenResponse =
                userService.login(new UserLoginRequest("login_user01", "password123"));

        UserLogoutRequest body =
                new UserLogoutRequest(tokenResponse.refreshToken());

        String jsonBody = objectMapper.writeValueAsString(body);

        mockMvc.perform(post("/api/users/auth/logout")
                        .header("Authorization", "Bearer " + tokenResponse.accessToken())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

//        UserLoginRequest request = new UserLoginRequest("login_user01", "password123");
//        UserLoginResponse tokenResponse = userService.login(request);
//
//        UserLogoutRequest refreshToken = new UserLogoutRequest(tokenResponse.refreshToken());
//
//        userService.logout(refreshToken,);
//
//        // 예: RefreshTokenRepository에서 더 이상 찾을 수 없어야 함
//        assertThat(refreshTokenRepository.findByToken(refreshToken.refreshToken())).isEmpty();
        assertThat(refreshTokenRepository.findByToken(tokenResponse.refreshToken())).isEmpty();

    }

    // 2. 로그아웃 실페
    @Test
    @DisplayName("로그아웃 실패 - 이미 로그아웃된 토큰")
    void 로그아웃_실패_로그아웃_연속시도() throws Exception {

        // given
        UserLoginResponse tokenResponse =
                userService.login(new UserLoginRequest("login_user01", "password123"));

        UserLogoutRequest body = new UserLogoutRequest(tokenResponse.refreshToken());
        String jsonBody = objectMapper.writeValueAsString(body);

        // 1st logout -> 성공
        mockMvc.perform(post("/api/users/auth/logout")
                        .header("Authorization", "Bearer " + tokenResponse.accessToken())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        // 2nd logout -> 실패 기대
        mockMvc.perform(post("/api/users/auth/logout")
                        .header("Authorization", "Bearer " + tokenResponse.accessToken())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
