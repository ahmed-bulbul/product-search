package com.es.product.search.controller;

import com.es.product.search.dto.JwtResponse;
import com.es.product.search.dto.LoginRequest;
import com.es.product.search.dto.UserDto;
import com.es.product.search.models.User;
import com.es.product.search.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import com.es.product.search.config.SecurityConfig; // Import SecurityConfig
import com.es.product.search.filters.JwtAuthFilter;
import com.es.product.search.service.UserDetailsServiceImpl;
import com.es.product.search.utils.JwtUtil;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Use WebMvcTest and specify the controller to test
@WebMvcTest(AuthController.class)
// Import SecurityConfig and other necessary beans for Spring Security context
@Import({SecurityConfig.class, UserDetailsServiceImpl.class, JwtUtil.class})
@AutoConfigureMockMvc // No need to explicitly add filters if Spring Security is managed by SecurityConfig
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // JwtAuthFilter is part of SecurityConfig, so it should be auto-configured.
    // If not, it might need to be @MockBean too, but usually not needed for WebMvcTest with SecurityConfig
    @MockBean
    private JwtAuthFilter jwtAuthFilter;


    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .build();
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
    }

    @Test
    void registerUser_Success() throws Exception {
        when(userService.registerUser(any(UserDto.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully: " + user.getUsername()));
    }

    @Test
    void registerUser_UserAlreadyExists() throws Exception {
        when(userService.registerUser(any(UserDto.class))).thenThrow(new RuntimeException("Username already exists"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    void loginUser_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        JwtResponse jwtResponse = new JwtResponse("test-token");
        when(userService.loginUser(any(LoginRequest.class))).thenReturn(jwtResponse);
        // Mock jwtAuthFilter to bypass actual token validation during this specific test
        when(jwtAuthFilter.shouldNotFilter(any())).thenReturn(true);


        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"));
    }

    @Test
    void loginUser_InvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        when(userService.loginUser(any(LoginRequest.class))).thenThrow(new BadCredentialsException("Invalid credentials"));
        when(jwtAuthFilter.shouldNotFilter(any())).thenReturn(true);


        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Login failed: Invalid credentials"));
    }
}
