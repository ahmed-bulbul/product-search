package com.es.product.search.service;

import com.es.product.search.dto.RoleDto;
import com.es.product.search.models.Permission;
import com.es.product.search.models.Role;
import com.es.product.search.respository.PermissionRepository;
import com.es.product.search.respository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTests {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RoleService roleService;

    private RoleDto roleDto;
    private Role role;
    private Permission perm1;
    private Permission perm2;

    @BeforeEach
    void setUp() {
        perm1 = Permission.builder().id(1L).name("READ_DATA").build();
        perm2 = Permission.builder().id(2L).name("WRITE_DATA").build();

        roleDto = RoleDto.builder()
                .name("ROLE_TEST")
                .permissionIds(new HashSet<>(Arrays.asList(1L, 2L)))
                .build();

        role = Role.builder()
                .id(1L)
                .name("ROLE_TEST")
                .permissions(new HashSet<>(Arrays.asList(perm1, perm2)))
                .build();
    }

    @Test
    void createRole_Success_WithPermissions() {
        when(roleRepository.findByName("ROLE_TEST")).thenReturn(Optional.empty());
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(perm1));
        when(permissionRepository.findById(2L)).thenReturn(Optional.of(perm2));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role savedRole = invocation.getArgument(0);
            savedRole.setId(1L); // Simulate saving and getting an ID
            return savedRole;
        });

        Role createdRole = roleService.createRole(roleDto);

        assertNotNull(createdRole);
        assertEquals("ROLE_TEST", createdRole.getName());
        assertEquals(2, createdRole.getPermissions().size());
        assertTrue(createdRole.getPermissions().contains(perm1));
        assertTrue(createdRole.getPermissions().contains(perm2));
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void createRole_Success_WithoutPermissions() {
        RoleDto noPermDto = RoleDto.builder().name("ROLE_NO_PERM").permissionIds(new HashSet<>()).build();
        when(roleRepository.findByName("ROLE_NO_PERM")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role savedRole = invocation.getArgument(0);
            savedRole.setId(2L);
            return savedRole;
        });

        Role createdRole = roleService.createRole(noPermDto);

        assertNotNull(createdRole);
        assertEquals("ROLE_NO_PERM", createdRole.getName());
        assertTrue(createdRole.getPermissions().isEmpty());
        verify(roleRepository, times(1)).save(any(Role.class));
    }


    @Test
    void createRole_AlreadyExists() {
        when(roleRepository.findByName("ROLE_TEST")).thenReturn(Optional.of(role));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleService.createRole(roleDto);
        });
        assertEquals("Role with name ROLE_TEST already exists.", exception.getMessage());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void createRole_PermissionNotFound() {
        when(roleRepository.findByName("ROLE_TEST")).thenReturn(Optional.empty());
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(perm1));
        when(permissionRepository.findById(2L)).thenReturn(Optional.empty()); // perm2 not found

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleService.createRole(roleDto);
        });
        assertEquals("Permission not found with id: 2", exception.getMessage());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void addPermissionToRole_Success() {
        Role existingRole = Role.builder().id(1L).name("ROLE_EXISTING").permissions(new HashSet<>()).build();
        when(roleRepository.findById(1L)).thenReturn(Optional.of(existingRole));
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(perm1));
        when(roleRepository.save(any(Role.class))).thenReturn(existingRole);

        Role updatedRole = roleService.addPermissionToRole(1L, 1L);

        assertNotNull(updatedRole);
        assertTrue(updatedRole.getPermissions().contains(perm1));
        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    void addPermissionToRole_RoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleService.addPermissionToRole(1L, 1L);
        });
        assertEquals("Role not found with id: 1", exception.getMessage());
        verify(roleRepository, never()).save(any(Role.class));
    }


    @Test
    void removePermissionFromRole_Success() {
        Role existingRole = Role.builder().id(1L).name("ROLE_EXISTING").permissions(new HashSet<>(Set.of(perm1, perm2))).build();
        when(roleRepository.findById(1L)).thenReturn(Optional.of(existingRole));
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(perm1));
        when(roleRepository.save(any(Role.class))).thenReturn(existingRole);

        Role updatedRole = roleService.removePermissionFromRole(1L, 1L);

        assertNotNull(updatedRole);
        assertFalse(updatedRole.getPermissions().contains(perm1));
        assertTrue(updatedRole.getPermissions().contains(perm2)); // Ensure other permissions remain
        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    void getAllRoles_Success() {
        when(roleRepository.findAll()).thenReturn(Arrays.asList(role, Role.builder().id(2L).name("ROLE_OTHER").build()));
        List<Role> roles = roleService.getAllRoles();
        assertEquals(2, roles.size());
        verify(roleRepository, times(1)).findAll();
    }
}
