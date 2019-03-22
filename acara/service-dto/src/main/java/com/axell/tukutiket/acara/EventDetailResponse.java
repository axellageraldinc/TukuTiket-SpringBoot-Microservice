package com.axell.tukutiket.acara;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailResponse {
    private String id;
    private String name;
    private String venue;
    private Date date;
    private BigDecimal ticketPrice;
    private EventStatus status;
}
