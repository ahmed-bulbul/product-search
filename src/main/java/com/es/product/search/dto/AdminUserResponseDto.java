package com.es.product.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

import com.es.product.search.models.User;
import com.es.product.search.models.Role;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponseDto {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private Set<String> roles;

    public static AdminUserResponseDto fromUser(User user) {
        return AdminUserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .build();
    }
}
