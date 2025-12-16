package com.kt.util;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.membership.Membership;
import com.kt.domain.user.Gender;
import com.kt.domain.user.User;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.CustomUserDetails;

@SpringBootTest
@Transactional
public abstract class UserTestSupport {

	@Autowired
	protected UserRepository userRepository;

	@Autowired
	protected MembershipRepository membershipRepository;

	protected CustomUserDetails customUserDetails;

	@BeforeEach
	void setUp() {
		initUser();
	}

	protected void initUser() {
		Membership membership = membershipRepository.save(
			new Membership("BRONZE")
		);

		User user = userRepository.save(
			User.normalUser(
				"tester",
				"password123!",
				"테스트유저",
				"test@test.com",
				"010-1234-5679",
				Gender.MALE,
				LocalDate.of(1990, 1, 1),
				membership
			)
		);

		CustomUserDetails.from(user);
	}
}

