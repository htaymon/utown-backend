package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.AddressRequestDTO;
import com.utown.utown_backend.dto.response.AddressResponseDTO;
import com.utown.utown_backend.entity.Address;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.mapper.AddressMapper;
import com.utown.utown_backend.repository.AddressRepository;
import com.utown.utown_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper mapper;

    public AddressResponseDTO create(AddressRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Address address = Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .user(user)
                .build();

        return mapper.toResponseDTO(addressRepository.save(address));
    }

    public List<AddressResponseDTO> getAll() {
        return mapper.toResponseList(addressRepository.findAll());
    }

    public AddressResponseDTO getById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        return mapper.toResponseDTO(address);
    }

    public AddressResponseDTO update(Long id, AddressRequestDTO dto) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            address.setUser(user);
        }

        return mapper.toResponseDTO(addressRepository.save(address));
    }

    public void delete(Long id) {
        addressRepository.deleteById(id);
    }
}