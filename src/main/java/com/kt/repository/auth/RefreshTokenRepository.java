package com.kt.repository.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.auth.RefreshToken;
import com.kt.domain.user.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Boolean existsByToken(String token);

    void deleteByToken(String token);

    void deleteAllByUser(User user);

    String findRefreshTokenByToken(String token);
}

