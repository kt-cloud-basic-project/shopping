package com.kt.service.user;

import com.kt.common.CustomException;
import com.kt.common.ErrorCode;
import com.kt.domain.membership.Membership;
import com.kt.domain.user.User;
import com.kt.dto.user.UserCreateRequest;
import com.kt.dto.user.UserLoginRequest;
import com.kt.dto.user.UserLoginResponse;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private static final String DEFAULT_MEMBERSHIP_LEVEL = "BRONZE";

    public boolean checkLoginIdDuplicated(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    public void create(UserCreateRequest request) {

        if (checkLoginIdDuplicated(request.loginId())) {
            throw new CustomException(ErrorCode.DUPLICATED_LOGIN_ID);
        }
        Membership defaultMembership = membershipRepository.findByLevel(DEFAULT_MEMBERSHIP_LEVEL)
                .orElseThrow(() -> new IllegalStateException("기본 멤버십이 설정되어 있지 않습니다."));

        var newUser = User.normalUser(
                request.loginId(),
                passwordEncoder.encode(request.password()),
                request.name(),
                request.email(),
                request.mobile(),
                request.gender(),
                request.birthday(),
                defaultMembership
        );

        userRepository.save(newUser);

    }

    // readOnly -> 불필요한 flush호출방지
    // 토큰을 반환하는 방식으로 변경
    @Transactional(readOnly = true)
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new CustomException(ErrorCode.FAIL_LOGIN));

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new CustomException(ErrorCode.FAIL_LOGIN);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        return UserLoginResponse.of(accessToken,refreshToken);
    }
}
