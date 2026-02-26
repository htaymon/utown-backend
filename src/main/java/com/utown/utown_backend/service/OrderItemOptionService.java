package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.OrderItemOptionRequestDTO;
import com.utown.utown_backend.dto.response.OrderItemOptionResponseDTO;
import com.utown.utown_backend.entity.Option;
import com.utown.utown_backend.entity.OrderItem;
import com.utown.utown_backend.entity.OrderItemOption;
import com.utown.utown_backend.mapper.OrderItemOptionMapper;
import com.utown.utown_backend.repository.OptionRepository;
import com.utown.utown_backend.repository.OrderItemOptionRepository;
import com.utown.utown_backend.repository.OrderItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemOptionService {

    private final OrderItemOptionRepository repository;
    private final OrderItemRepository orderItemRepository;
    private final OptionRepository optionRepository;
    private final OrderItemOptionMapper mapper;

    public OrderItemOptionResponseDTO create(OrderItemOptionRequestDTO dto) {

        OrderItem orderItem = orderItemRepository.findById(dto.getOrderItemId())
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found"));
        Option option = optionRepository.findById(dto.getOptionId())
                .orElseThrow(() -> new EntityNotFoundException("Option not found"));
        OrderItemOption entity = new OrderItemOption();
        entity.setOrderItem(orderItem);
        entity.setOption(option);

        OrderItemOption saved = repository.save(entity);

        return mapper.toResponseDTO(saved);
    }

    public List<OrderItemOptionResponseDTO> getAll() {
        List<OrderItemOption> list = repository.findAll();
        return mapper.toResponseList(list);
    }

    public OrderItemOptionResponseDTO getById(Long id) {
        OrderItemOption entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItemOption not found"));
        return mapper.toResponseDTO(entity);
    }

    public OrderItemOptionResponseDTO update(Long id, OrderItemOptionRequestDTO dto) {

        OrderItemOption entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItemOption not found"));
        OrderItem orderItem = orderItemRepository.findById(dto.getOrderItemId())
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found"));
        Option option = optionRepository.findById(dto.getOptionId())
                .orElseThrow(() -> new EntityNotFoundException("Option not found"));

        entity.setOrderItem(orderItem);
        entity.setOption(option);
        OrderItemOption updated = repository.save(entity);

        return mapper.toResponseDTO(updated);
    }

    public void delete(Long id) {
        OrderItemOption entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItemOption not found"));
        repository.delete(entity);
    }
}
