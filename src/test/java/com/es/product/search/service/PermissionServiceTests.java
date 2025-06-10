package com.es.product.search.service;

import com.es.product.search.dto.PermissionDto;
import com.es.product.search.models.Permission;
import com.es.product.search.respository.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermissionServiceTests {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    private PermissionDto permissionDto;
    private Permission permission;

    @BeforeEach
    void setUp() {
        permissionDto = PermissionDto.builder().name("TEST_PERMISSION").build();
        permission = Permission.builder().id(1L).name("TEST_PERMISSION").build();
    }

    @Test
    void createPermission_Success() {
        when(permissionRepository.findByName("TEST_PERMISSION")).thenReturn(Optional.empty());
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        Permission createdPermission = permissionService.createPermission(permissionDto);

        assertNotNull(createdPermission);
        assertEquals("TEST_PERMISSION", createdPermission.getName());
        verify(permissionRepository, times(1)).save(any(Permission.class));
    }

    @Test
    void createPermission_AlreadyExists() {
        when(permissionRepository.findByName("TEST_PERMISSION")).thenReturn(Optional.of(permission));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            permissionService.createPermission(permissionDto);
        });

        assertEquals("Permission with name TEST_PERMISSION already exists.", exception.getMessage());
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    void getAllPermissions_Success() {
        when(permissionRepository.findAll()).thenReturn(Arrays.asList(permission, Permission.builder().id(2L).name("ANOTHER_PERMISSION").build()));
        List<Permission> permissions = permissionService.getAllPermissions();
        assertEquals(2, permissions.size());
        verify(permissionRepository, times(1)).findAll();
    }

    @Test
    void getPermissionById_Success() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));
        Permission foundPermission = permissionService.getPermissionById(1L);
        assertNotNull(foundPermission);
        assertEquals(permission.getName(), foundPermission.getName());
        verify(permissionRepository, times(1)).findById(1L);
    }

    @Test
    void getPermissionById_NotFound() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            permissionService.getPermissionById(1L);
        });
        assertEquals("Permission not found with id: 1", exception.getMessage());
        verify(permissionRepository, times(1)).findById(1L);
    }
}
