package com.kt.service.auth;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.user.User;
import com.kt.dto.auth.TokenResponse;
import com.kt.repository.auth.RefreshTokenRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.beans.Transient;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public TokenResponse reissue(String accessToken,String refreshToken){
        String loginId = jwtTokenProvider.getLoginId(accessToken);

        String dbRefreshToken = refreshTokenRepository.findRefreshTokenByToken(refreshToken);

        if (dbRefreshToken == null || !dbRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.EXPIRED_JWT_TOKEN);
        }
        var user = userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user);

        // RTR방식 -> access토큰을 갱신할때 refresh토큰을 함께 갱신하는방법
        // 장점: refresh토큰이 탈취당했을때를 대비할수있어 보안성이높음
        // 단점: stateless하지않고 서버에 부담을 줄수있다? jwt의 장점이 희석된다
        // 일단 보류
        /*String newRefreshToken = jwtTokenProvider.createRefreshToken();
        refreshTokenService.save(userId, newRefreshToken);
        return new TokenResponse(newAccessToken, newRefreshToken);
        */
        return new TokenResponse(newAccessToken, refreshToken);
    }
}
