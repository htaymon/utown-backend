package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.UserRequestDTO;
import com.utown.utown_backend.dto.response.UserResponseDTO;
import com.utown.utown_backend.entity.Role;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.exception.EmailAlreadyExistsException;
import com.utown.utown_backend.mapper.UserMapper;
import com.utown.utown_backend.repository.RoleRepository;
import com.utown.utown_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO create(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phoneNumber(dto.getPhoneNumber())
                .role(role)
                .build();

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAll() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(role);

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}