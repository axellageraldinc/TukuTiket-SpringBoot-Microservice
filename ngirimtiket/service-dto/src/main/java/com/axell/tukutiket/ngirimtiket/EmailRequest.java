package com.axell.tukutiket.ngirimtiket;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest implements Serializable {
    private String orderId;
    private String username;
    private String emailAddress;
    private String eventName;
    private String eventVenue;
    private Date eventDate;
    private String qrCodeImageName;
}
