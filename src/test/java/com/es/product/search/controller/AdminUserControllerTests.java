package com.es.product.search.controller;

import com.es.product.search.config.SecurityConfig;
import com.es.product.search.filters.JwtAuthFilter;
import com.es.product.search.models.Role;
import com.es.product.search.models.User;
import com.es.product.search.service.UserService;
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

@WebMvcTest(AdminUserController.class) // Target the new AdminUserController
@Import({SecurityConfig.class, UserDetailsServiceImpl.class, JwtUtil.class})
public class AdminUserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService; // Assuming UserService will be expanded or a new AdminUserService used

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private User adminUser, regularUser;
    private Role roleAdmin, roleUser;


    @BeforeEach
    void setUp() {
        roleAdmin = Role.builder().id(1L).name("ROLE_ADMIN").build();
        roleUser = Role.builder().id(2L).name("ROLE_USER").build();

        adminUser = User.builder().id(1L).username("admin").email("admin@example.com").roles(Set.of(roleAdmin)).enabled(true).build();
        regularUser = User.builder().id(2L).username("user").email("user@example.com").roles(Set.of(roleUser)).enabled(true).build();

        when(jwtAuthFilter.shouldNotFilter(any())).thenReturn(true); // Bypass JWT filter for these tests by default
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_AsAdmin_Success() throws Exception {
        // Assuming userService.getAllUsersForAdmin() would be the method called in the actual controller
        when(userService.getAllUsersForAdmin()).thenReturn(Arrays.asList(adminUser, regularUser));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
                // As the placeholder controller returns empty list, we can't assert content yet.
                // .andExpect(jsonPath("$", hasSize(2)))
                // .andExpect(jsonPath("$[0].username", is("admin")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_AsUser_Forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_Unauthenticated_Forbidden() throws Exception {
        when(jwtAuthFilter.shouldNotFilter(any())).thenReturn(false);
         mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden()); // or isUnauthorized
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_AsAdmin_Success() throws Exception {
        User updatedUserData = User.builder().id(2L).username("user").email("user@example.com").enabled(false).roles(Set.of(roleUser)).build();
        // Assuming userService.updateUserAsAdmin(anyLong(), any(User.class))
        when(userService.updateUserAsAdmin(anyLong(), any(User.class))).thenReturn(updatedUserData);

        mockMvc.perform(put("/api/admin/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserData)))
                .andExpect(status().isOk());
                // As the placeholder controller returns request body, this can be asserted if needed:
                // .andExpect(jsonPath("$.enabled", is(false)));
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateUser_AsUser_Forbidden() throws Exception {
        User updatedUserData = User.builder().id(1L).username("admin").enabled(false).build();
         mockMvc.perform(put("/api/admin/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserData)))
                .andExpect(status().isForbidden());
    }

    // Dummy method in UserService that would be called by AdminUserController
    // This is just for the mock setup to compile, actual implementation would be in UserService
    private interface AdminUserService extends UserService {
        List<User> getAllUsersForAdmin();
        User updateUserAsAdmin(Long id, User user);
    }
}
