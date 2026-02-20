package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.NotificationDTO;
import com.utown.utown_backend.entity.Notification;
import com.utown.utown_backend.service.NotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Notification> getAll() {
        return service.getAllNotifications();
    }

    @GetMapping("/{id}")
    public Notification getById(@PathVariable Long id) {
        return service.getNotificationById(id);
    }

    @PostMapping
    public Notification create(@RequestBody NotificationDTO dto) {
        return service.createNotification(dto);
    }

    @PutMapping("/{id}")
    public Notification update(@PathVariable Long id, @RequestBody NotificationDTO dto) {
        return service.updateNotification(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteNotification(id);
    }
}
