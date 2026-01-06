package com.kt.domain.certify;

import com.kt.common.support.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Certify extends BaseEntity {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String certifyCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertifyStatus codeStatus;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime lockedAt;

    private LocalDateTime verifiedAt;

    public void changeStatus(CertifyStatus codeStatus){ // 코드 사용여부등 상태확인용도
        this.codeStatus = codeStatus;
    }

    public void initAttempts(){ // 시도횟수 초기화
        this.attempts = 0;
    }

    public void increaseAttemptsAndLock() {
        this.attempts++;
        if (this.attempts >= 5) {
            this.codeStatus = CertifyStatus.LOCKED;
            this.lockedAt = LocalDateTime.now();
        }
    }

    public static Certify create(String email, String code, LocalDateTime expire) {
        Certify certify = new Certify();
        certify.email = email;
        certify.certifyCode = code;
        certify.codeStatus = CertifyStatus.PENDING;
        certify.attempts = 0;
        certify.expiresAt = expire;
        return certify;
    }

    public boolean isExpired(LocalDateTime now) {
        return this.expiresAt.isBefore(now);
    }

    public boolean isLocked() {
        return this.codeStatus == CertifyStatus.LOCKED;
    }

    public boolean isSameCode(String code) {
        return this.certifyCode.equals(code);
    }

    public boolean isVerified() {
        if(this.codeStatus.equals(CertifyStatus.VERIFIED)){
            this.verifiedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }
    public void markVerifiedForTest(LocalDateTime expiresAt) {
        this.codeStatus = CertifyStatus.VERIFIED;
        this.expiresAt = expiresAt;
    }
    public static Certify TestVerify(String email, String code, LocalDateTime expiresAt) {
        Certify c = new Certify();
        c.email = email;
        c.certifyCode = code;
        c.codeStatus = CertifyStatus.PENDING;
        c.expiresAt = expiresAt;
        return c;
    }


}