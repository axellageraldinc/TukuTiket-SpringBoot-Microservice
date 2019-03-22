package com.axell.tukutiket.uwong;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    private String name;
    private String email;
}
