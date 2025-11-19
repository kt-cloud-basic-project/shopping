package com.kt.service.user;

import com.kt.domain.membership.Membership;
import com.kt.domain.user.User;
import com.kt.dto.user.UserCreateRequest;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.user.UserRepository;
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
    private static final String DEFAULT_MEMBERSHIP_LEVEL = "BRONZE";


    public void create(UserCreateRequest.Create request) {

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
}
