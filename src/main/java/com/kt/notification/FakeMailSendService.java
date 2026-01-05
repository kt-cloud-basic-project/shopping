package com.kt.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("test")
public class FakeMailSendService implements MailSendService {

    @Override
    public void sendEmail(MailSendRequest mailSendRequest) {
        log.info("[TEST] skip email send: to={}, subject={}",
                mailSendRequest.email(), mailSendRequest.title());
    }
}

