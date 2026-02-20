package com.utown.utown_backend.dto;

public class WorkScheduleDTO {
    private Long restaurantId;
    private String dayOfWeek;
    private String startTime;
    private String endTime;

    public WorkScheduleDTO() {
    }

    public WorkScheduleDTO(Long restaurantId, String dayOfWeek, String startTime, String endTime) {
        this.restaurantId = restaurantId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
