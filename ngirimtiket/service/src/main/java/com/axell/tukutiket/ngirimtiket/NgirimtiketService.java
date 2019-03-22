package com.axell.tukutiket.ngirimtiket;

public interface NgirimtiketService {
    EmailSentResponse sendEmail(EmailRequest emailRequest);
}
