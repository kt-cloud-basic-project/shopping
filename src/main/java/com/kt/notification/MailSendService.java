package com.kt.notification;

import jakarta.validation.Valid;

public interface MailSendService {
    void sendEmail(@Valid MailSendRequest mailSendRequest);
}
