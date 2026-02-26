package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.DeliveryAreaRequestDTO;
import com.utown.utown_backend.dto.response.DeliveryAreaResponseDTO;
import com.utown.utown_backend.entity.DeliveryArea;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.mapper.DeliveryAreaMapper;
import com.utown.utown_backend.repository.DeliveryAreaRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryAreaService {

    private final DeliveryAreaRepository repository;
    private final RestaurantRepository restaurantRepository;
    private final DeliveryAreaMapper mapper;

    public DeliveryAreaResponseDTO create(DeliveryAreaRequestDTO dto) {

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        DeliveryArea entity = DeliveryArea.builder()
                .restaurant(restaurant)
                .city(dto.getCity())
                .name(dto.getName())
                .build();

        repository.save(entity);

        return mapper.toResponseDTO(entity);
    }

    public List<DeliveryAreaResponseDTO> getAll() {
        return mapper.toResponseList(repository.findAll());
    }

    public DeliveryAreaResponseDTO getById(Long id) {
        DeliveryArea entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryArea not found"));

        return mapper.toResponseDTO(entity);
    }

    public DeliveryAreaResponseDTO update(Long id, DeliveryAreaRequestDTO dto) {

        DeliveryArea entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryArea not found"));

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        entity.setRestaurant(restaurant);
        entity.setCity(dto.getCity());
        entity.setName(dto.getName());

        repository.save(entity);

        return mapper.toResponseDTO(entity);
    }

    public void delete(Long id) {
        DeliveryArea entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryArea not found"));

        repository.delete(entity);
    }
}