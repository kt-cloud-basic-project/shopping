package com.kt.notify;

import jakarta.validation.Valid;

public interface MailSendService {
    void sendEmail(@Valid MailSendRequest mailSendRequest);
}
