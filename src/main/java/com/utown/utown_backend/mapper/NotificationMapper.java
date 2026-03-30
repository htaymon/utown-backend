package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.NotificationRequestDTO;
import com.utown.utown_backend.dto.response.NotificationResponseDTO;
import com.utown.utown_backend.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    Notification toEntity(NotificationRequestDTO dto);

    @Mapping(source = "user.id", target = "userId")
    NotificationResponseDTO toResponseDTO(Notification notification);

    List<NotificationResponseDTO> toResponseList(List<Notification> notifications);
}