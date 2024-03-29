package com.axell.tukutiket.bayarbayar;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWebRequest {
    private BigDecimal totalPrice;
    private BigDecimal amountPaid;
}
