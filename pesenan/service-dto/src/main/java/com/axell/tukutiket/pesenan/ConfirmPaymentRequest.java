package com.axell.tukutiket.pesenan;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPaymentRequest {
    private BigDecimal totalPrice;
    private BigDecimal amountPaid;
}
