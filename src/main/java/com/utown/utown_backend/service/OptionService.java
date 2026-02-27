package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.OptionRequestDTO;
import com.utown.utown_backend.dto.response.OptionResponseDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.Option;
import com.utown.utown_backend.mapper.OptionMapper;
import com.utown.utown_backend.repository.DishRepository;
import com.utown.utown_backend.repository.OptionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionService {

    private final OptionRepository optionRepository;
    private final DishRepository dishRepository;
    private final OptionMapper mapper;

    public OptionResponseDTO create(OptionRequestDTO dto) {

        Dish dish = dishRepository.findById(dto.getDishId())
                .orElseThrow(() -> new EntityNotFoundException("Dish not found"));

        Option option = mapper.toEntity(dto);
        option.setDish(dish);

        return mapper.toResponseDTO(
                optionRepository.save(option)
        );
    }

    @Transactional(readOnly = true)
    public List<OptionResponseDTO> getAll() {
        return mapper.toResponseList(
                optionRepository.findAll()
        );
    }

    @Transactional(readOnly = true)
    public OptionResponseDTO getById(Long id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Option not found"));
        return mapper.toResponseDTO(option);
    }

    public OptionResponseDTO update(Long id, OptionRequestDTO dto) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Option not found"));
        Dish dish = dishRepository.findById(dto.getDishId())
                .orElseThrow(() -> new EntityNotFoundException("Dish not found"));
        option.setName(dto.getName());
        option.setExtraPrice(dto.getExtraPrice());
        option.setDish(dish);

        return mapper.toResponseDTO(
                optionRepository.save(option)
        );
    }

    public void delete(Long id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Option not found"));
        optionRepository.delete(option);
    }
}