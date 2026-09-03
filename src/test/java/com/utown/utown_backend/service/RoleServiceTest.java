package com.utown.utown_backend.service;

import com.utown.utown_backend.entity.Role;
import com.utown.utown_backend.repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository repository;
    @Mock
    private com.utown.utown_backend.mapper.RoleMapper mapper;

    @InjectMocks
    private RoleService roleService;

    @Test
    void delete_removesRole_whenRoleExists() {
        Role role = Role.builder().id(1L).name("CLIENT").build();
        when(repository.findById(1L)).thenReturn(Optional.of(role));

        roleService.delete(1L);

        verify(repository).delete(role);
    }

    @Test
    void delete_throwsEntityNotFound_whenRoleDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class);

        verify(repository, never()).delete(any());
        verify(repository, never()).deleteById(any());
    }
}
