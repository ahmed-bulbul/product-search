package com.es.product.search.controller;

import com.es.product.search.dto.AdminUserResponseDto;
import com.es.product.search.dto.AdminUserUpdateDto;
import com.es.product.search.models.User;
import com.es.product.search.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<AdminUserResponseDto>> getAllUsers() {
        List<User> users = userService.getAllUsersForAdmin();
        List<AdminUserResponseDto> userDtos = users.stream()
                .map(AdminUserResponseDto::fromUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserByIdForAdmin(id)
                .map(user -> ResponseEntity.ok(AdminUserResponseDto.fromUser(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody AdminUserUpdateDto userUpdateDto) {
        try {
            User updatedUser = userService.updateUserAsAdmin(id, userUpdateDto);
            return ResponseEntity.ok(AdminUserResponseDto.fromUser(updatedUser));
        } catch (RuntimeException e) {
             // More specific exception handling can be added (e.g., for UserNotFound, RoleNotFound)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUserAsAdmin(id);
            // For soft delete, you might return OK with a message or the updated (disabled) user
            // For hard delete, No Content is appropriate
            return ResponseEntity.ok("User with id " + id + " has been disabled (soft delete).");
            // return ResponseEntity.noContent().build(); // If hard delete
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
