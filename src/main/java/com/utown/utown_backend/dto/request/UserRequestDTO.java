package com.utown.utown_backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    private String name;
    private String email;
    private String phoneNumber;
    private Long roleId;
}