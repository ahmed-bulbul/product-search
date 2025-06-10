package com.es.product.search.service;

import com.es.product.search.dto.RoleDto;
import com.es.product.search.models.Permission;
import com.es.product.search.models.Role;
import com.es.product.search.respository.PermissionRepository;
import com.es.product.search.respository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Transactional
    public Role createRole(RoleDto roleDto) {
        if (roleRepository.findByName(roleDto.getName()).isPresent()) {
            throw new RuntimeException("Role with name " + roleDto.getName() + " already exists.");
        }

        Set<Permission> permissions = new HashSet<>();
        if (roleDto.getPermissionIds() != null && !roleDto.getPermissionIds().isEmpty()) {
            permissions = roleDto.getPermissionIds().stream()
                    .map(permissionId -> permissionRepository.findById(permissionId)
                            .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId)))
                    .collect(Collectors.toSet());
        }

        Role role = Role.builder()
                .name(roleDto.getName())
                .permissions(permissions)
                .build();
        return roleRepository.save(role);
    }

    @Transactional
    public Role addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        role.getPermissions().add(permission);
        return roleRepository.save(role);
    }

    @Transactional
    public Role removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        role.getPermissions().remove(permission);
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }
}
