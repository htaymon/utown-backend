package com.utown.utown_backend.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long userId;
    private String message;
    private String status;
    private LocalDateTime createdAt;

    public NotificationDTO() {
    }

    public NotificationDTO(Long userId, String message, String status, LocalDateTime createdAt) {
        this.userId = userId;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
