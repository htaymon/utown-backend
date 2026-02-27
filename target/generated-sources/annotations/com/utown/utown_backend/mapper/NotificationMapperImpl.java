package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.NotificationRequestDTO;
import com.utown.utown_backend.dto.response.NotificationResponseDTO;
import com.utown.utown_backend.entity.Notification;
import com.utown.utown_backend.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T02:40:03+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public Notification toEntity(NotificationRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Notification.NotificationBuilder notification = Notification.builder();

        notification.message( dto.getMessage() );
        notification.status( dto.getStatus() );

        return notification.build();
    }

    @Override
    public NotificationResponseDTO toResponseDTO(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationResponseDTO.NotificationResponseDTOBuilder notificationResponseDTO = NotificationResponseDTO.builder();

        notificationResponseDTO.userId( notificationUserId( notification ) );
        notificationResponseDTO.id( notification.getId() );
        notificationResponseDTO.message( notification.getMessage() );
        notificationResponseDTO.status( notification.getStatus() );

        return notificationResponseDTO.build();
    }

    @Override
    public List<NotificationResponseDTO> toResponseList(List<Notification> notifications) {
        if ( notifications == null ) {
            return null;
        }

        List<NotificationResponseDTO> list = new ArrayList<NotificationResponseDTO>( notifications.size() );
        for ( Notification notification : notifications ) {
            list.add( toResponseDTO( notification ) );
        }

        return list;
    }

    private Long notificationUserId(Notification notification) {
        if ( notification == null ) {
            return null;
        }
        User user = notification.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
