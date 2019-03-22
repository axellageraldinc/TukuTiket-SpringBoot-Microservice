package com.axell.tukutiket.pesenan;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSentResponse implements Serializable {
    private String orderId;
}
