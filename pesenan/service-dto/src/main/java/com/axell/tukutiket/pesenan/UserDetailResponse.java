package com.axell.tukutiket.pesenan;

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
