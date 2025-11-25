package com.kt.dto.user;

import java.time.LocalDate;

import com.kt.domain.user.Gender;
import com.kt.domain.user.Role;
import com.kt.domain.user.User;

public record UserInfoResponse(
        Long id,
        String loginId,
        String name,
        String email,
        String mobile,
        Gender gender,
        LocalDate birthday,
        String membershipLevel,
        Long money,
        Role role
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getEmail(),
                user.getMobile(),
                user.getGender(),
                user.getBirthday(),
                user.getMembership().getLevel(),
                user.getMoney(),
                user.getRole()
        );
    }
}
