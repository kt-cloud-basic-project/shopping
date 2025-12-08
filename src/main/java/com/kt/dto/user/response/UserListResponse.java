package com.kt.dto.user.response;

import com.kt.domain.user.Gender;
import com.kt.domain.user.Role;
import com.kt.domain.user.User;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.function.Function;

public record UserListResponse(
        Long id,
        String loginId,
        String name,
        String email,
        String mobile,
        Gender gender,
        LocalDate birthday,
        String membershipLevel,
        Long money,
        Role role,
        String address
) {
    public static Page<UserListResponse> fromList(Page<User> page, Function<User,String> addressMapper) {
        return page.map(user -> new UserListResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getEmail(),
                user.getMobile(),
                user.getGender(),
                user.getBirthday(),
                user.getMembership().getLevel(),
                user.getMoney(),
                user.getRole(),
                addressMapper.apply(user)
        ));
    }
}
