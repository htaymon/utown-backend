package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.OrderItemOptionDTO;
import com.utown.utown_backend.entity.OrderItem;
import com.utown.utown_backend.entity.Option;
import com.utown.utown_backend.entity.OrderItemOption;
import com.utown.utown_backend.repository.OrderItemOptionRepository;
import com.utown.utown_backend.repository.OrderItemRepository;
import com.utown.utown_backend.repository.OptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemOptionService {

    private final OrderItemOptionRepository repository;
    private final OrderItemRepository itemRepository;
    private final OptionRepository optionRepository;

    public OrderItemOptionService(OrderItemOptionRepository repository,
                                  OrderItemRepository itemRepository,
                                  OptionRepository optionRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.optionRepository = optionRepository;
    }

    public List<OrderItemOptionDTO> getAllOrderItemOptions() {
        return repository.findAll()
                .stream()
                .map(entity -> {
                    OrderItemOptionDTO dto = new OrderItemOptionDTO();
                    dto.setOrderItemOptionId(entity.getOrderItemOptionId());
                    dto.setOrderItemId(entity.getOrderItem() != null ? entity.getOrderItem().getOrderItemId() : null);
                    dto.setOptionId(entity.getOption() != null ? entity.getOption().getOptionId() : null);
                    return dto;
                })
                .toList();
    }

    public OrderItemOptionDTO getOrderItemOptionById(Long id) {
        return repository.findById(id)
                .map(entity -> {
                    OrderItemOptionDTO dto = new OrderItemOptionDTO();
                    dto.setOrderItemOptionId(entity.getOrderItemOptionId());
                    dto.setOrderItemId(entity.getOrderItem() != null ? entity.getOrderItem().getOrderItemId() : null);
                    dto.setOptionId(entity.getOption() != null ? entity.getOption().getOptionId() : null);
                    return dto;
                })
                .orElse(null);
    }

    public OrderItemOptionDTO createOrderItemOption(OrderItemOptionDTO dto) {
        OrderItem item = itemRepository.findById(dto.getOrderItemId()).orElse(null);
        Option option = optionRepository.findById(dto.getOptionId()).orElse(null);

        OrderItemOption entity = new OrderItemOption(item, option);
        OrderItemOption saved = repository.save(entity);

        dto.setOrderItemOptionId(saved.getOrderItemOptionId());
        return dto;
    }

    public OrderItemOptionDTO updateOrderItemOption(Long id, OrderItemOptionDTO dto) {
        OrderItemOption entity = repository.findById(id).orElse(null);
        if (entity != null) {
            OrderItem item = itemRepository.findById(dto.getOrderItemId()).orElse(null);
            Option option = optionRepository.findById(dto.getOptionId()).orElse(null);
            entity.setOrderItem(item);
            entity.setOption(option);
            repository.save(entity);
            dto.setOrderItemOptionId(entity.getOrderItemOptionId());
            return dto;
        }
        return null;
    }

    public void deleteOrderItemOption(Long id) {
        repository.deleteById(id);
    }
}
