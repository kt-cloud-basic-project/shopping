package com.kt.service.user;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
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

    @BeforeEach
    void setUp() {
				refreshTokenRepository.deleteAll();
				userRepository.deleteAll();
				membershipRepository.deleteAll();

        Membership membership = new Membership("BRONZE");
        membershipRepository.save(membership);

        // 로그인 테스트용 유저 저장
        User user = User.normalUser(
                "login_user01",
                passwordEncoder.encode("password123"),
                "로그인유저",
                "login@kt.com",
                "010-0000-0000",
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                membership
        );


        userRepository.save(user);
    }

    // 로그인 테스트
    // 1. 유저 로그인 성공
    @Test
    @DisplayName("로그인 성공 - 유저")
    void loginSuccess() {

        UserLoginRequest request = new UserLoginRequest("login_user01", "password123");


        UserLoginResponse tokenResponse = userService.login(request);


        assertThat(tokenResponse.accessToken()).isNotBlank();
        assertThat(tokenResponse.refreshToken()).isNotBlank();
    }

    // 2. 유저 로그인 실패(아이디/비밀번호 오기입)
    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginFailWrongPassword() {

        UserLoginRequest request = new UserLoginRequest("login_user01", "wrongPassword");


        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.FAIL_LOGIN.getMessage());
    }

    // 로그아웃 테스트
    // 1. 로그아웃 성공
    @Test
    @DisplayName("로그아웃 성공 - 리프레시 토큰 삭제")
    void logoutSuccess() {

        UserLoginRequest request = new UserLoginRequest("login_user01", "password123");
        UserLoginResponse tokenResponse = userService.login(request);

        UserLogoutRequest refreshToken = new UserLogoutRequest(tokenResponse.refreshToken());

        userService.logout(refreshToken);

        // 예: RefreshTokenRepository에서 더 이상 찾을 수 없어야 함
        assertThat(refreshTokenRepository.findByToken(refreshToken.refreshToken())).isEmpty();
    }

    // 2. 로그아웃 실페
    @Test
    @DisplayName("로그아웃 실패 - 이미 로그아웃된 토큰")
    void logoutFailAlreadyLoggedOut() {

        UserLoginRequest request = new UserLoginRequest("login_user01", "password123");
        UserLoginResponse tokenResponse = userService.login(request);

        UserLogoutRequest refreshToken = new UserLogoutRequest(tokenResponse.refreshToken());

        userService.logout(refreshToken);

        // 다시 로그아웃 시도 시 예외
        assertThatThrownBy(() -> userService.logout(refreshToken))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.INVALID_JWT_TOKEN.getMessage());
    }
}
