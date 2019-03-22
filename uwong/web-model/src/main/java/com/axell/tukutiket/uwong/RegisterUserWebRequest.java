package com.axell.tukutiket.uwong;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserWebRequest {
    private String name;
    private String email;
    private String password;
}
