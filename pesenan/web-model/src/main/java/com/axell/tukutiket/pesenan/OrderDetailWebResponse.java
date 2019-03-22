package com.axell.tukutiket.pesenan;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailWebResponse {
    private String id;
    private String eventName;
    private String eventVenue;
    private Date eventDate;
    private BigDecimal totalPrice;
    private String bankName;
    private String bankAccountNumber;
    private int ticketQuantity;
}
