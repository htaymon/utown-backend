package com.utown.utown_backend.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private final long id;
    private final String name;
    private final String email;
    private final String password;
    private final String phoneNumber;
    private final String roleName;
}