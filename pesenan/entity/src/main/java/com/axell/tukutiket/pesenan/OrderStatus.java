package com.axell.tukutiket.pesenan;

public enum OrderStatus {
    WAITING_PAYMENT,
    PAYMENT_VERIFIED,
    EMAIL_SENDING_PENDING,
    EMAIL_SENDING_DONE,
    COMPLETED
}
