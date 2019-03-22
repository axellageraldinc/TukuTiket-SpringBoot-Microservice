package com.axell.tukutiket.pesenan;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "order_status")
    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "qrcode_name")
    private String qrCodeImageName;

    @Column(name = "ticket_quantity")
    private int ticketQuantity;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "bank_id")
    private String bankId;
}
