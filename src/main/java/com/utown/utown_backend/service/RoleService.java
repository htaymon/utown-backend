package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.RoleRequestDTO;
import com.utown.utown_backend.dto.response.RoleResponseDTO;
import com.utown.utown_backend.entity.Role;
import com.utown.utown_backend.mapper.RoleMapper;
import com.utown.utown_backend.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository repository;
    private final RoleMapper mapper;

    public RoleResponseDTO create(RoleRequestDTO dto) {
        Role role = mapper.toEntity(dto);
        return mapper.toResponseDTO(repository.save(role));
    }

    @Transactional(readOnly = true)
    public List<RoleResponseDTO> getAll() {
        return mapper.toResponseList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public RoleResponseDTO getById(Long id) {
        Role role = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        return mapper.toResponseDTO(role);
    }

    public RoleResponseDTO update(Long id, RoleRequestDTO dto) {
        Role role = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        role.setName(dto.getName());
        return mapper.toResponseDTO(repository.save(role));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}