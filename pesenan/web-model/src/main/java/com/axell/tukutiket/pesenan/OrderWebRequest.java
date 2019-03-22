package com.axell.tukutiket.pesenan;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderWebRequest {
    private String eventId;
    private String userId;
    private String bankId;
    private int ticketQuantity;
}
