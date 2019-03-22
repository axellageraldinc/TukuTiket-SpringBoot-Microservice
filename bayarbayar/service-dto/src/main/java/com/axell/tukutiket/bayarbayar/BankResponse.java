package com.axell.tukutiket.bayarbayar;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankResponse {
    private String id;
    private String name;
    private String accountNumber;
}
