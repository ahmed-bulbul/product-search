package com.es.product.search.service;

import com.es.product.search.dto.JwtResponse;
import com.es.product.search.dto.LoginRequest;
import com.es.product.search.dto.UserDto;
import com.es.product.search.models.Role;
import com.es.product.search.models.User;
import com.es.product.search.respository.RoleRepository;
import com.es.product.search.respository.UserRepository;
import com.es.product.search.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User user;
    private Role userRole;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .username("testuser")
                .password("password")
                .email("test@example.com")
                .build();

        userRole = Role.builder().id(1L).name("ROLE_USER").build();

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setEmail("test@example.com");
        user.setEnabled(true);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);
    }

    @Test
    void registerUser_Success() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser(userDto);

        assertNotNull(registeredUser);
        assertEquals(userDto.getUsername(), registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertTrue(registeredUser.getRoles().contains(userRole));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_UsernameExists() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(userDto);
        });
        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_EmailExists() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(userDto);
        });
        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_DefaultRoleNotFound() {
        when(userRepository.findByUsername(userDto.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(userDto);
        });
        assertEquals("Default role not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_Success() {
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        Authentication authentication = mock(Authentication.class);
        org.springframework.security.core.userdetails.User userDetails =
            new org.springframework.security.core.userdetails.User("testuser", "encodedPassword", new HashSet<>());

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("test-jwt-token");

        JwtResponse jwtResponse = userService.loginUser(loginRequest);

        assertNotNull(jwtResponse);
        assertEquals("test-jwt-token", jwtResponse.getToken());
    }

    @Test
    void loginUser_InvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest("testuser", "wrongpassword");
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            userService.loginUser(loginRequest);
        });
        verify(jwtUtil, never()).generateToken(any());
    }
}
