package com.kt.domain.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.kt.common.BaseEntity;
import com.kt.domain.membership.Membership; //멤버쉽 임의 생성

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String loginId;

    @Column(nullable = false, length = 256)
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String email;

    @Column(nullable = false, length = 13)
    private String mobile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Long money = 0L;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;


    private User(String loginId, String password, String name, String email, String mobile,
                 Gender gender, LocalDate birthday, Role role, Membership membership) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.gender = gender;
        this.birthday = birthday;
        this.role = role;
        this.money = 0L;
        this.isDeleted = false;
        this.membership = membership;
    }


    public static User normalUser(String loginId, String password, String name, String email, String mobile,
                                  Gender gender, LocalDate birthday, Membership membership) {
        return new User(loginId, password, name, email, mobile, gender, birthday, Role.USER, membership);
    }


    public static User admin(String loginId, String password, String name, String email, String mobile,
                             Gender gender, LocalDate birthday, Membership membership) {
        return new User(loginId, password, name, email, mobile, gender, birthday, Role.ADMIN, membership);
    }


    public void changePassword(String password) {
        this.password = password;
    }

    public void update(String name, String email, String mobile) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    public void delete() {
        this.isDeleted = true;
    }

    // 결제기능 사용을 위한 보유금액 추가
    public void addMoney(long amount) {
        this.money += amount;
    }

    // 결제기능 사용을 통한 보유금액 차감
    public void useMoney(long amount) {
        this.money -= amount;
    }
}
