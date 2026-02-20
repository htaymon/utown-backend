package com.utown.utown_backend.dto;

public class AddressDTO {
    private Long userId;
    private String street;
    private String city;
    private String state;
    private String postalCode;

    public AddressDTO() {
    }

    public AddressDTO(Long userId, String street, String city, String state, String postalCode) {
        this.userId = userId;
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
    }

    public Long getUserId() {
        return userId;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
