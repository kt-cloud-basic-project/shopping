package com.kt.service.notify;

import com.kt.dto.notify.MailSendRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EMailSendService implements MailSendService {
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(@Valid MailSendRequest mailSendRequest){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(mailSendRequest.email());
        msg.setSubject(mailSendRequest.title());
        msg.setText(mailSendRequest.content());

        try{
            mailSender.send(msg);
            log.info("메일 전송 성공: subject={}", mailSendRequest.title());
        }catch (MailException e){
            log.error("메일 전송 실패: subject={}, reason={}", mailSendRequest.title(), e.getMessage(), e);
        }
    }

}
