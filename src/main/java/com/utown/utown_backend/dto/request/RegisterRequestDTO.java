package com.utown.utown_backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    private String name;
    private String email;
    private String password;
    private String phoneNumber;
}
