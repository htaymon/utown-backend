package com.utown.utown_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private String email;
    private String password;
}
