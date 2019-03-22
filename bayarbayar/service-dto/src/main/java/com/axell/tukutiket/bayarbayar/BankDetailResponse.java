package com.axell.tukutiket.bayarbayar;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankDetailResponse {
    private String name;
    private String accountNumber;
}
