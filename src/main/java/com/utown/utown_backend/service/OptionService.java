package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.OptionDTO;
import com.utown.utown_backend.dto.OptionResponseDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.Option;
import com.utown.utown_backend.repository.DishRepository;
import com.utown.utown_backend.repository.OptionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OptionService {

    private final OptionRepository repository;
    private final DishRepository dishRepository;

    public OptionService(OptionRepository repository, DishRepository dishRepository) {
        this.repository = repository;
        this.dishRepository = dishRepository;
    }

    public List<OptionResponseDTO> getAllOptions() {
        List<Option> options = repository.findAll();
        List<OptionResponseDTO> response = new ArrayList<>();
        for (Option o : options) {
            response.add(new OptionResponseDTO(
                    o.getOptionId(),
                    o.getName(),
                    o.getExtraPrice(),
                    o.getDish().getDishId(),
                    o.getDish().getName()
            ));
        }
        return response;
    }

    public OptionResponseDTO getOptionById(Long id) {
        Option o = repository.findById(id).orElse(null);
        if (o == null) return null;
        return new OptionResponseDTO(
                o.getOptionId(),
                o.getName(),
                o.getExtraPrice(),
                o.getDish().getDishId(),
                o.getDish().getName()
        );
    }

    public Option createOption(OptionDTO dto) {
        Dish dish = dishRepository.findById(dto.getDishId())
                .orElseThrow(() -> new RuntimeException("Dish not found"));
        Option option = new Option(dish, dto.getName(), dto.getExtraPrice());
        return repository.save(option);
    }

    public Option updateOption(Long id, OptionDTO dto) {
        Option option = repository.findById(id).orElse(null);
        if (option != null) {
            Dish dish = dishRepository.findById(dto.getDishId())
                    .orElseThrow(() -> new RuntimeException("Dish not found"));
            option.setDish(dish);
            option.setName(dto.getName());
            option.setExtraPrice(dto.getExtraPrice());
            return repository.save(option);
        }
        return null;
    }

    public void deleteOption(Long id) {
        repository.deleteById(id);
    }
}
