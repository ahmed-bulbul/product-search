package com.es.product.search.service;

import com.es.product.search.dto.JwtResponse;
import com.es.product.search.dto.LoginRequest;
import com.es.product.search.dto.UserDto;
import com.es.product.search.models.Role;
import com.es.product.search.models.User;
import com.es.product.search.respository.RoleRepository;
import com.es.product.search.respository.UserRepository;
import com.es.product.search.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.util.HashSet;
import java.util.List; // Import List
import java.util.Set;
import java.util.stream.Collectors; // Import Collectors

import com.es.product.search.dto.AdminUserUpdateDto; // Import DTO

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    public User registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setEnabled(true);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public JwtResponse loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);
        return new JwtResponse(jwt);
    }

    // Admin methods
    public List<User> getAllUsersForAdmin() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByIdForAdmin(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUserAsAdmin(Long id, AdminUserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (userUpdateDto.getEnabled() != null) {
            user.setEnabled(userUpdateDto.getEnabled());
        }

        if (userUpdateDto.getRoleNames() != null) {
            Set<Role> newRoles = userUpdateDto.getRoleNames().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(newRoles);
        }
        // Add other updatable fields here, e.g., email
        // if (userUpdateDto.getEmail() != null) {
        //     user.setEmail(userUpdateDto.getEmail());
        // }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUserAsAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        // Consider business logic: what happens to user's data?
        // For now, just delete the user.
        // userRepository.delete(user);
        // Or, if you want to keep user but disable, set enabled to false and remove sensitive data
         user.setEnabled(false); // Soft delete example
         userRepository.save(user);
    }
}
