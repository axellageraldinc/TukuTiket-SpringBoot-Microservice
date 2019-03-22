package com.axell.tukutiket.pesenan;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateQRCodeResponse implements Serializable {
    private String orderId;
    private String imageName;
}
