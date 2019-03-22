package com.axell.tukutiket.acara;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "date")
    private Date date;

    @Column(name = "venue")
    private String venue;

    @Column(name = "ticket_price")
    private BigDecimal ticketPrice;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private EventStatus eventStatus;
}
