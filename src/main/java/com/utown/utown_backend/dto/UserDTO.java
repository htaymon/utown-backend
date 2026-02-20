package com.utown.utown_backend.dto;

public class UserDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private Long roleId;

    public UserDTO() {
    }

    public UserDTO(String name, String email, String phoneNumber, Long roleId) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
