package com.axell.tukutiket.pesenan;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateQRCodeRequest implements Serializable {
    private String userEmailAddress;
    private String orderId;
}
