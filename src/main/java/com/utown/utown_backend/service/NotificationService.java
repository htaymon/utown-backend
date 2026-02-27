package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.NotificationRequestDTO;
import com.utown.utown_backend.dto.response.NotificationResponseDTO;
import com.utown.utown_backend.entity.Notification;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.mapper.NotificationMapper;
import com.utown.utown_backend.repository.NotificationRepository;
import com.utown.utown_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository repository;
    private final UserRepository userRepository;
    private final NotificationMapper mapper;

    public NotificationResponseDTO create(NotificationRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

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
    public NotificationResponseDTO getById(Long id) {

        Notification notification = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        return mapper.toResponseDTO(notification);
    }

    public NotificationResponseDTO update(Long id, NotificationRequestDTO dto) {

        Notification notification = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        notification.setUser(user);
        notification.setMessage(dto.getMessage());
        notification.setStatus(dto.getStatus());

        return mapper.toResponseDTO(
                repository.save(notification)
        );
    }

    public void delete(Long id) {

        Notification notification = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        repository.delete(notification);
    }
}