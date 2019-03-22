package com.axell.tukutiket.ngirimtiket;

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
