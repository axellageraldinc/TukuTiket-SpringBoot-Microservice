package com.axell.tukutiket.bayarbayar;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankDetailWebResponse {
    private String name;
    private String accountNumber;
}
