package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.AddressRequestDTO;
import com.utown.utown_backend.dto.response.AddressResponseDTO;
import com.utown.utown_backend.entity.Address;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.mapper.AddressMapper;
import com.utown.utown_backend.repository.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper mapper;
    private final AuthService authService;

    public AddressResponseDTO create(AddressRequestDTO dto) {
        User user = authService.getCurrentUser();

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
        User user = authService.getCurrentUser();
        return mapper.toResponseList(addressRepository.findByUserId(user.getId()));
    }

    public AddressResponseDTO getById(Long id) {
        User user = authService.getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }
        return mapper.toResponseDTO(address);
    }

    public AddressResponseDTO update(Long id, AddressRequestDTO dto) {

        User user = authService.getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        address.setStreet(dto.getStreet());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());

        return mapper.toResponseDTO(addressRepository.save(address));
    }

    public void delete(Long id) {
        User user = authService.getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }
        addressRepository.deleteById(id);
    }
}