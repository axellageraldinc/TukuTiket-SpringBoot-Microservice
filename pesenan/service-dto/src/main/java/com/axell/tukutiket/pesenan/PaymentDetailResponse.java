package com.axell.tukutiket.pesenan;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailResponse {
    private String name;
    private String accountNumber;
}
