package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.DeliveryAreaDTO;
import com.utown.utown_backend.entity.DeliveryArea;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.repository.DeliveryAreaRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryAreaService {

    private final DeliveryAreaRepository repository;
    private final RestaurantRepository restaurantRepository;

    public DeliveryAreaService(DeliveryAreaRepository repository, RestaurantRepository restaurantRepository) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<DeliveryArea> getAllAreas() {
        return repository.findAll();
    }

    public DeliveryArea getAreaById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public DeliveryArea createArea(DeliveryAreaDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        DeliveryArea area = new DeliveryArea(restaurant, dto.getCity(), dto.getAreaName());
        return repository.save(area);
    }

    public DeliveryArea updateArea(Long id, DeliveryAreaDTO dto) {
        DeliveryArea area = repository.findById(id).orElse(null);
        if (area != null) {
            Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            area.setRestaurant(restaurant);
            area.setCity(dto.getCity());
            area.setAreaName(dto.getAreaName());
            return repository.save(area);
        }
        return null;
    }

    public void deleteArea(Long id) {
        repository.deleteById(id);
    }
}
