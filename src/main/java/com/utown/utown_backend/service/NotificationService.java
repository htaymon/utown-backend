package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.NotificationRequestDTO;
import com.utown.utown_backend.dto.response.NotificationResponseDTO;
import com.utown.utown_backend.entity.Notification;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.mapper.NotificationMapper;
import com.utown.utown_backend.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final AuthService authService;

    public NotificationResponseDTO create(NotificationRequestDTO dto) {

        User user = authService.getCurrentUser();

        Notification notification = mapper.toEntity(dto);
        notification.setUser(user);

        return mapper.toResponseDTO(
                repository.save(notification)
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getAll() {
        return mapper.toResponseList(
                repository.findAll()
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getMyNotifications() {
        User user = authService.getCurrentUser();
        return mapper.toResponseList(
                repository.findByUserIdOrderByCreatedAtDesc(user.getId())
        );
    }

    @Transactional(readOnly = true)
    public NotificationResponseDTO getById(Long id) {

        User user = authService.getCurrentUser();
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        checkOwnership(notification, user);

        return mapper.toResponseDTO(notification);
    }

    public NotificationResponseDTO update(Long id, NotificationRequestDTO dto) {

        User user = authService.getCurrentUser();
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        checkOwnership(notification, user);

        notification.setMessage(dto.getMessage());
        notification.setStatus(dto.getStatus());

        return mapper.toResponseDTO(
                repository.save(notification)
        );
    }

    public void delete(Long id) {

        User user = authService.getCurrentUser();
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        checkOwnership(notification, user);

        repository.delete(notification);
    }

    private void checkOwnership(Notification notification, User user) {
        boolean isOwner = notification.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() != null && "ADMIN".equals(user.getRole().getName());

        if (!isOwner && !isAdmin) {
            log.warn("NOTIFICATION_ACCESS_DENIED: notificationId={}, userId={}",
                    notification.getId(), user.getId());
            throw new AccessDeniedException("Access denied");
        }
    }
}
