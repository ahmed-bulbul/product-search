package com.es.product.search.controller;

import com.es.product.search.config.SecurityConfig;
import com.es.product.search.dto.RoleDto;
import com.es.product.search.filters.JwtAuthFilter;
import com.es.product.search.models.Permission;
import com.es.product.search.models.Role;
import com.es.product.search.service.RoleService;
import com.es.product.search.service.UserDetailsServiceImpl;
import com.es.product.search.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@Import({SecurityConfig.class, UserDetailsServiceImpl.class, JwtUtil.class})
public class RoleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter; // Mock the filter

    @Autowired
    private ObjectMapper objectMapper;

    private Role roleAdmin, roleUser;
    private Permission permRead, permWrite;

    @BeforeEach
    void setUp() {
        permRead = Permission.builder().id(1L).name("READ_PRIVILEGE").build();
        permWrite = Permission.builder().id(2L).name("WRITE_PRIVILEGE").build();

        roleAdmin = Role.builder().id(1L).name("ROLE_ADMIN").permissions(new HashSet<>(Set.of(permRead, permWrite))).build();
        roleUser = Role.builder().id(2L).name("ROLE_USER").permissions(new HashSet<>(Set.of(permRead))).build();

        // Bypass JWT filter for these tests by default
        when(jwtAuthFilter.shouldNotFilter(any())).thenReturn(true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRole_AsAdmin_Success() throws Exception {
        RoleDto newRoleDto = RoleDto.builder().name("ROLE_NEW").permissionIds(new HashSet<>(Set.of(1L))).build();
        Role createdRole = Role.builder().id(3L).name("ROLE_NEW").permissions(new HashSet<>(Set.of(permRead))).build();
        when(roleService.createRole(any(RoleDto.class))).thenReturn(createdRole);

        mockMvc.perform(post("/api/admin/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRoleDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("ROLE_NEW")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createRole_AsUser_Forbidden() throws Exception {
        RoleDto newRoleDto = RoleDto.builder().name("ROLE_NEW").permissionIds(new HashSet<>()).build();
        mockMvc.perform(post("/api/admin/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRoleDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createRole_Unauthenticated_Forbidden() throws Exception {
        RoleDto newRoleDto = RoleDto.builder().name("ROLE_NEW").permissionIds(new HashSet<>()).build();
         // Ensure filter is active for unauthenticated test
        when(jwtAuthFilter.shouldNotFilter(any())).thenReturn(false);

        mockMvc.perform(post("/api/admin/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRoleDto)))
                .andExpect(status().isForbidden()); // or isUnauthorized if filter sends 401
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllRoles_AsAdmin_Success() throws Exception {
        when(roleService.getAllRoles()).thenReturn(Arrays.asList(roleAdmin, roleUser));

        mockMvc.perform(get("/api/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("ROLE_ADMIN")))
                .andExpect(jsonPath("$[1].name", is("ROLE_USER")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllRoles_AsUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addPermissionToRole_AsAdmin_Success() throws Exception {
        when(roleService.addPermissionToRole(anyLong(), anyLong())).thenReturn(roleAdmin);

        mockMvc.perform(post("/api/admin/roles/1/permissions/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("ROLE_ADMIN")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void addPermissionToRole_AsUser_Forbidden() throws Exception {
        mockMvc.perform(post("/api/admin/roles/1/permissions/2"))
                .andExpect(status().isForbidden());
    }
}
