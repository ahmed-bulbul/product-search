package com.es.product.search.service;

import com.es.product.search.dto.PermissionDto;
import com.es.product.search.models.Permission;
import com.es.product.search.respository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public Permission createPermission(PermissionDto permissionDto) {
        if (permissionRepository.findByName(permissionDto.getName()).isPresent()) {
            throw new RuntimeException("Permission with name " + permissionDto.getName() + " already exists.");
        }
        Permission permission = Permission.builder()
                .name(permissionDto.getName())
                .build();
        return permissionRepository.save(permission);
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Permission getPermissionById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + id));
    }
}
