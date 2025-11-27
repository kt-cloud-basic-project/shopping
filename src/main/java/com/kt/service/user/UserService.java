package com.kt.service.user;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.auth.RefreshToken;
import com.kt.domain.membership.Membership;
import com.kt.domain.shoppingaddress.ShoppingAddress;
import com.kt.domain.user.User;
import com.kt.dto.user.*;
import com.kt.repository.auth.RefreshTokenRepository;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.shoppingaddress.ShoppingAddressRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.CustomUserDetails;
import com.kt.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.kt.common.support.ObjectUtils.orElseIfEmpty;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ShoppingAddressRepository shoppingAddressRepository;
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

    // readOnly추가를 통한 불필요한 flush호출방지 -> 리프레시 토큰을 위해 readOnly 삭제
    // 토큰을 반환하는 방식으로 변경
    // access토큰 반환 및 기존 리프레시 토큰 삭제및 저장 추가
    @Transactional
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new CustomException(ErrorCode.FAIL_LOGIN));

        if(!passwordEncoder.matches(request.password(), user.getPassword())){
            throw new CustomException(ErrorCode.FAIL_LOGIN);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        refreshTokenRepository.deleteAllByUser(user);

        LocalDateTime refreshExpiresAt = jwtTokenProvider.getRefreshTokenExpiryDateTime();

        RefreshToken refreshTokenEntity = new RefreshToken(
                refreshToken,
                user,
                refreshExpiresAt
        );

        refreshTokenRepository.save(refreshTokenEntity);

        return UserLoginResponse.of(accessToken,refreshToken);
    }

    public void logout(UserLogoutRequest request){
        if(request.refreshToken() == null || request.refreshToken().isBlank()){
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }

        refreshTokenRepository.findByToken(request.refreshToken())
                .ifPresent(token -> refreshTokenRepository.delete(token));

    }

    public UserInfoResponse getMyInfo(CustomUserDetails customUserDetails){
        String loginId = customUserDetails.getUsername();
        var user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        var shoppingAddressOpt =
                shoppingAddressRepository.findFirstByUserIdAndIsDefaultTrueOrderByIdDesc(user.getId());

        String address = shoppingAddressOpt
                .map(ShoppingAddress::getAddress)
                .orElse(null);

        return UserInfoResponse.from(user,address);
    }

    public void updateMyInfo(CustomUserDetails customUserDetails, UserUpdateRequest request){
        String loginId = customUserDetails.getUsername();
        var user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        user.update(
                orElseIfEmpty(request.name(),user.getName()),
                orElseIfEmpty(request.email(),user.getEmail()),
                orElseIfEmpty(request.mobile(),user.getMobile())
        );
    }

    public void ChangePassword(CustomUserDetails customUserDetails, UserChangePassword request){
        String loginId = customUserDetails.getUsername();
        var user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        user.changePassword(request.password());
    }
}
