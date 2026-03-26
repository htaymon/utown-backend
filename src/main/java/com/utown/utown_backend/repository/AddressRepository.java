package com.utown.utown_backend.repository;

import com.utown.utown_backend.entity.Address;
import com.utown.utown_backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);

}
