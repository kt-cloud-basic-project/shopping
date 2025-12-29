package com.kt.service.notify;

import com.kt.dto.notify.MailSendRequest;
import jakarta.validation.Valid;

public interface MailSendService {
    void sendEmail(@Valid MailSendRequest mailSendRequest);
}
