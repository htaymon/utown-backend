package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.NotificationDTO;
import com.utown.utown_backend.entity.Notification;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.repository.NotificationRepository;
import com.utown.utown_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<Notification> getAllNotifications() {
        return repository.findAll();
    }

    public Notification getNotificationById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Notification createNotification(NotificationDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Notification notification = new Notification(user, dto.getMessage(), dto.getStatus(), dto.getCreatedAt());
        return repository.save(notification);
    }

    public Notification updateNotification(Long id, NotificationDTO dto) {
        Notification notification = repository.findById(id).orElse(null);
        if (notification != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            notification.setUser(user);
            notification.setMessage(dto.getMessage());
            notification.setStatus(dto.getStatus());
            notification.setCreatedAt(dto.getCreatedAt());
            return repository.save(notification);
        }
        return null;
    }

    public void deleteNotification(Long id) {
        repository.deleteById(id);
    }
}
