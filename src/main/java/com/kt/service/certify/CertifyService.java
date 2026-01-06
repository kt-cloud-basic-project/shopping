package com.kt.service.certify;

import com.kt.common.exception.CustomException;
import com.kt.common.exception.ErrorCode;
import com.kt.domain.certify.Certify;
import com.kt.domain.certify.CertifyStatus;
import com.kt.dto.certify.EmailCertificationRequest;
import com.kt.notification.MailSendRequest;
import com.kt.notification.MailSendService;
import com.kt.repository.certify.CertifyRepository;
import com.kt.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class CertifyService {

    private final UserRepository userRepository;
    private final CertifyRepository certifyRepository;
    private final MailSendService mailSendService;
    @Transactional
    public void certifyEmail(EmailCertificationRequest req) {
        String email = req.email();
        LocalDateTime now = LocalDateTime.now();

        if (userRepository.existsByEmailAndIsDeletedFalse(email)) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }

        Certify certify = certifyRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new CustomException(ErrorCode.CERTIFICATION_CODE_NOT_FOUND));

        if (certify.isLocked()) { // 5회이상 틀릴시 잠금, 초과처럼 에러코드를 작성했지만 이상이 맞습니다...
            throw new CustomException(ErrorCode.FAILED_MORE_THAN_FIVE_TIMES);
        }

        if (certify.isExpired(now)) { // 코드 유효기간 만료
            certify.changeStatus(CertifyStatus.EXPIRED);
            throw new CustomException(ErrorCode.EXPIRED_CERTIFICATION_CODE);
        }

        if (certify.isSameCode(req.code())) { // 이메일 인증통과
            certify.changeStatus(CertifyStatus.VERIFIED);
            certify.initAttempts();
            return;
        }

        certify.increaseAttemptsAndLock(); // 실패시 틀림 처리
        throw new CustomException(ErrorCode.INVALID_CERTIFICATION_CODE);
    }


    @Transactional
    public void createCode(String email) { // 인증을 위한 난수 생성, secureRandom을 통해 강한 난수를 생성할수도있다 -> 좀 과한 것 같으니 제외
        Random RANDOM = new Random();
        StringBuilder builder = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            builder.append(RANDOM.nextInt(10)); // 0 ~ 9
        }
        String code = builder.toString();
        LocalDateTime expire = LocalDateTime.now().plusMinutes(5L);
        Certify certifyCode = Certify.create(email,code,expire);
        certifyRepository.save(certifyCode);

        MailSendRequest request = new MailSendRequest(
                email,
                "이메일 인증 코드입니다.",
                "코드: \n" + code
        );
        mailSendService.sendEmail(request);
    }

    public boolean validateEmailVerified(String email) {
        Certify certify = certifyRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new CustomException(ErrorCode.CERTIFICATION_CODE_NOT_FOUND));

        if (certify.isVerified()) {
            return true;
        }
        return false;
    }


}
