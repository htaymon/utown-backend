package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.AddressDTO;
import com.utown.utown_backend.entity.Address;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.repository.AddressRepository;
import com.utown.utown_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    public Address getAddressById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    public Address createAddress(AddressDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = new Address(
                dto.getStreet(),
                dto.getCity(),
                dto.getState(),
                dto.getPostalCode(),
                user
        );
        return addressRepository.save(address);
    }

    public Address updateAddress(Long id, AddressDTO dto) {
        Address address = addressRepository.findById(id).orElse(null);
        if (address != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            address.setUser(user);
            address.setStreet(dto.getStreet());
            address.setCity(dto.getCity());
            address.setState(dto.getState());
            address.setPostalCode(dto.getPostalCode());

            return addressRepository.save(address);
        }
        return null;
    }

    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
}
