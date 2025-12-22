package com.kt.service.user;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.membership.Membership;
import com.kt.domain.user.Gender;
import com.kt.domain.user.Role;
import com.kt.domain.user.User;
import com.kt.dto.user.request.UserAdminUpdateRequest;
import com.kt.dto.user.request.UserCreateRequest;
import com.kt.dto.user.request.UserUpdateRequest;
import com.kt.repository.membership.MembershipRepository;
import com.kt.repository.user.UserRepository;
import com.kt.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class UserServiceTest {

    // 유저 유닛 테스트 작성

    //테스트 환경 사전설정
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MembershipRepository membershipRepository;

    @BeforeEach
    void setUp(){
				userRepository.deleteAll();
				membershipRepository.deleteAll();

        Membership membership1 = new Membership("BRONZE");
        membershipRepository.save(membership1);
    }

    // 회원가입 테스트
    // 1. 유저회원가입(성공)
    @Test
    @DisplayName("회원가입 테스트")
    @Transactional
    public void CreateSuccessTest() {
        UserCreateRequest request = new UserCreateRequest(
                "test_user01",
                "password",
                "김케클",
                "user@kt.com",
                "010-1234-5678",
                Gender.MALE,
                LocalDate.of(2000,1,1),
                "BRONZE"
        );

        userService.create(request);

        User savedUser = userRepository.findByLoginIdAndIsDeletedFalse(request.loginId())
                .orElseThrow(() -> new IllegalStateException("유저가 저장되지 않았습니다."));

        assertThat(savedUser.getLoginId()).isEqualTo("test_user01");
        assertThat(savedUser.getEmail()).isEqualTo("user@kt.com");
        assertThat(savedUser.getRole().name()).isEqualTo(Role.USER.toString());

    }

    // 2. 관리자 회원가입(성공)
    @Test
    @DisplayName("회원가입 성공 - 관리자")
    @Transactional
    void createAdminSuccess() {

        UserCreateRequest request = new UserCreateRequest(
                "admin01",
                "adminPass!",
                "관리자",
                "admin@kt.com",
                "010-9999-9999",
                Gender.MALE,
                LocalDate.of(1990, 1, 1),
                "BRONZE"
        );

        userService.createAdmin(request);

        User savedAdmin = userRepository.findByLoginIdAndIsDeletedFalse(request.loginId())
                .orElseThrow(() -> new IllegalStateException("관리자가 저장되지 않았습니다."));

        assertThat(savedAdmin.getLoginId()).isEqualTo("admin01");
        assertThat(savedAdmin.getEmail()).isEqualTo("admin@kt.com");
        assertThat(savedAdmin.getRole()).isEqualTo(Role.ADMIN);
    }

    // 3. 회원가입 실패 - 아이디 중복
    @Test
    @DisplayName("회원가입 실패 - 로그인 아이디 중복")
    @Transactional
    void createFailDuplicateLoginId() {

        UserCreateRequest request = new UserCreateRequest(
                "dup_user",
                "password",
                "중복유저",
                "dup@kt.com",
                "010-0000-0000",
                Gender.FEMALE,
                LocalDate.of(1995, 5, 5),
                "BRONZE"
        );

        userService.create(request);

        // 같은 loginId로 또 가입 시도하면 CustomException 발생
        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.DUPLICATED_LOGIN_ID.getMessage());
    }

    // 4. 회원가입 실패 - 핸드폰/이메일/비밀번호 양식 오기입
    // 서비스단에서는 에러를 처리하지않고 컨트롤러에서 vaild로 처리하기때문에 서비스 유닛테스트에서는 작동하지않음 컨트롤러 테스트에서 진행
//    @Test
//    @DisplayName("회원가입 실패 - 잘못된 이메일 형식")
//    @Transactional
//    void createFailInvalidEmailFormat() {
//
//        UserCreateRequest request = new UserCreateRequest(
//                "invalid_email_user",
//                "password",
//                "이메일오류",
//                "not-email-format", // 잘못된 이메일
//                "010-1111-2222",
//                Gender.FEMALE,
//                LocalDate.of(1998, 3, 3),
//                "BRONZE"
//        );
//
//
//        assertThatThrownBy(() -> userService.create(request))
//                .isInstanceOf(CustomException.class)
//                .hasMessageContaining(ErrorCode.INVALID_PARAMETER.getMessage());
//    }



    // 정보조회
    // 1. 유저의 본인 정보 조회
    @Test
    @DisplayName("정보조회 - 내 정보 조회 성공")
    @Transactional
    void getMyInfoSuccess() {


        Membership membership = membershipRepository.findByLevel("BRONZE").orElseThrow();

        User user = User.normalUser(
                "login_user01",
                "password123",
                "로그인유저",
                "login1@kt.com",
                "010-1234-5678",
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                membership
        );

        userRepository.save(user);

        CustomUserDetails principal = new CustomUserDetails(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getRole()
        );

        var response = userService.getMyInfo(principal);


        assertThat(response.name()).isEqualTo("로그인유저");
        assertThat(response.email()).isEqualTo("login1@kt.com");
    }

    // 2. 유저의 다른 유저 정보 조회 - 권한부족 실패 -> 컨트롤러에서 @PreAuthorize로 막는케이스
    // @WebMvcTest + MockMvc + @WithMockUser(또는 SecurityContext 세팅)으로 컨트롤러 테스트진행해야 검증가능
//    @Test
//    @DisplayName("정보조회 실패 - 다른 유저 정보 조회 (권한 부족)")
//    @Transactional
//    void getOtherUserInfoFailByUser() {
//
//        Membership membership = membershipRepository.findByLevel("BRONZE").orElseThrow();
//
//        User me = User.normalUser(
//                "login_user01",
//                "password123",
//                "로그인유저",
//                "login@kt.com",
//                "010-0000-0000",
//                Gender.MALE,
//                LocalDate.of(2000, 1, 1),
//                membership
//        );
//
//        User notMe = User.normalUser(
//                "login_user02",
//                "password123",
//                "로그인유저2",
//                "login@kt.com",
//                "010-0000-0000",
//                Gender.MALE,
//                LocalDate.of(2000, 1, 1),
//                membership
//        );
//
//        userRepository.save(me);
//        userRepository.save(notMe);
//
//
//        assertThatThrownBy(() -> userService.getUserInfo(notMe.getLoginId()))
//                .isInstanceOf(CustomException.class)
//                .hasMessageContaining(ErrorCode.FORBIDDEN.getMessage());
//    }

    // 3. 관리자의 다른 유저정보 조회
    @Test
    @DisplayName("정보조회 - 관리자 타 유저 정보 조회 성공")
    @Transactional
    void getUserInfoByAdminSuccess() {

        Membership membership = membershipRepository.findByLevel("BRONZE").orElseThrow();

        User admin = User.admin(
                "admin",
                "password123",
                "관리자",
                "admin@kt.com",
                "010-0000-0000",
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                membership
        );


        User user = User.normalUser(
                "login_user",
                "password123",
                "로그인유저",
                "login@kt.com",
                "010-0000-0000",
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                membership
        );

        userRepository.save(admin);
        userRepository.save(user);

        var response = userService.getUserInfo(user.getLoginId());

        assertThat(response.loginId()).isEqualTo("login_user");
        assertThat(response.email()).isEqualTo("login@kt.com");
    }

    // 정보수정
    // 1. 유저 본인 정보 수정
    @Test
    @DisplayName("정보수정 - 유저 본인 정보 수정 성공")
    @Transactional
    void updateMyInfoSuccess() {

        Membership membership = membershipRepository.findByLevel("BRONZE").orElseThrow();

        User user = User.normalUser(
                "login_user",
                "password123",
                "로그인유저",
                "login2@kt.com",
                "010-4567-6789",
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                membership
        );

        userRepository.save(user);

        CustomUserDetails principal = new CustomUserDetails(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getRole()
        );

        UserUpdateRequest request = new UserUpdateRequest(
                "새이름",
                "new@kt.com",
                "010-1111-2222"
        );


        userService.updateMyInfo(principal, request);


        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("새이름");
        assertThat(updated.getEmail()).isEqualTo("new@kt.com");
        assertThat(updated.getMobile()).isEqualTo("010-1111-2222");
    }

    // 2. 유저 본인 정보 수정 실패 - 기입 양식 오기입
    // 서비스단에서는 에러를 처리하지않고 컨트롤러에서 vaild로 처리하기때문에 서비스 유닛테스트에서는 작동하지않음 컨트롤러 테스트에서 진행
//    @Test
//    @DisplayName("정보수정 실패 - 잘못된 이메일 형식")
//    @Transactional
//    void updateMyInfoFailInvalidEmail() {
//
//        Membership membership = membershipRepository.findByLevel("BRONZE").orElseThrow();
//
//        User user = User.normalUser(
//                "login_user",
//                "password123",
//                "로그인유저",
//                "login@kt.com",
//                "010-0000-0000",
//                Gender.MALE,
//                LocalDate.of(2000, 1, 1),
//                membership
//        );
//
//        userRepository.save(user);
//
//        CustomUserDetails principal = new CustomUserDetails(
//                user.getId(),
//                user.getLoginId(),
//                user.getName(),
//                user.getRole()
//        );
//
//        UserUpdateRequest request = new UserUpdateRequest(
//                "새이름",
//                "not-email-format",
//                "010-1111-2222"
//        );
//
//        assertThatThrownBy(() -> userService.updateMyInfo(principal, request))
//                .isInstanceOf(CustomException.class)
//                .hasMessageContaining(ErrorCode.INVALID_FORMAT.getMessage());
//    }

    // 3. 관리자 타 유저 정보 수정
    @Test
    @DisplayName("정보수정 - 관리자 타 유저 정보 수정 성공")
    @Transactional
    void updateUserByAdminSuccess() {

        Membership membership = membershipRepository.findByLevel("BRONZE").orElseThrow();

        User admin = User.admin(
                "admin",
                "password123",
                "관리자",
                "admin@kt.com",
                "010-0000-0000",
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                membership
        );


        User user = User.normalUser(
                "login_user",
                "password123",
                "로그인유저",
                "login@kt.com",
                "010-0000-0000",
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                membership
        );

        userRepository.save(admin);
        userRepository.save(user);

        UserAdminUpdateRequest request = new UserAdminUpdateRequest(
                "login_user",
                "관리자가바꾼이름",
                "user_new@kt.com",
                "010-2222-3333"
        );

        userService.updateUserInfo(request);


        User updated = userRepository.findByLoginIdAndIsDeletedFalse(user.getLoginId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("관리자가바꾼이름");
        assertThat(updated.getEmail()).isEqualTo("user_new@kt.com");
    }

    // 회원탈퇴
    // 1. 회원탈퇴 성공(소프트 딜리트)
    @Test
    @DisplayName("회원탈퇴 성공 - 소프트 딜리트 처리")
    @Transactional
    void deleteMyAccountSuccess() {

        Membership membership = membershipRepository.findByLevel("BRONZE").orElseThrow();

        User user = User.normalUser(
                "delete_user",
                "password123",
                "탈퇴유저",
                "delete@kt.com",
                "010-5555-6666",
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                membership
        );
        userRepository.save(user);

        CustomUserDetails principal = new CustomUserDetails(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getRole()
        );


        userService.withDraw(principal);


        User deletedUser = userRepository.findByLoginId(user.getLoginId()).orElseThrow();
        assertThat(deletedUser.isDeleted()).isTrue();

        // 조회 메서드가 "isDeletedFalse" 조건이면 조회가 안 되어야 함
        assertThat(userRepository.findByLoginIdAndIsDeletedFalse("delete_user")).isEmpty();
    }

    // 2. 탈퇴한 유저 정보 기반으로 재가입 성공
    @Test
    @DisplayName("회원탈퇴 후 재가입 성공 - 탈퇴한 loginId 재사용 가능")
    @Transactional
    void rejoinAfterSoftDeleteSuccess() {

        UserCreateRequest request1 = new UserCreateRequest(
                "rejoin_user",
                "password",
                "첫가입",
                "first@kt.com",
                "010-1111-1111",
                Gender.MALE,
                LocalDate.of(2000, 1, 1),
                "BRONZE"
        );
        userService.create(request1);

        User savedUser = userRepository.findByLoginIdAndIsDeletedFalse("rejoin_user")
                .orElseThrow(() -> new IllegalStateException("유저가 저장되지 않았습니다."));

        CustomUserDetails principal = new CustomUserDetails(
                savedUser.getId(),
                savedUser.getLoginId(),
                savedUser.getName(),
                savedUser.getRole()
        );

        userService.withDraw(principal);

        User deletedUser = userRepository.findByLoginId(savedUser.getLoginId()).orElseThrow();
        assertThat(deletedUser.isDeleted()).isTrue();

        UserCreateRequest request2 = new UserCreateRequest(
                "rejoin_user",
                "newPassword",
                "재가입",
                "second@kt.com",
                "010-2222-2222",
                Gender.MALE,
                LocalDate.of(2001, 2, 2),
                "BRONZE"
        );


        userService.create(request2);


        User rejoined = userRepository.findByLoginIdAndIsDeletedFalse("rejoin_user")
                .orElseThrow(() -> new IllegalStateException("재가입 유저가 저장되지 않았습니다."));

        assertThat(rejoined.isDeleted()).isFalse();
        assertThat(rejoined.getEmail()).isEqualTo("second@kt.com");
        assertThat(rejoined.getName()).isEqualTo("재가입");

        // 소프트딜리트 유저까지 포함하면 데이터가 2개 있을 수 있음 -> 정책에 따라 다름 (완전 새 row 생성 vs 기존 row 복구)
    }
}
