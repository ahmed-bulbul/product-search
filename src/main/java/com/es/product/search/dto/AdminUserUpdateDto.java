package com.es.product.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserUpdateDto {
    private Set<String> roleNames; // Admin will send a set of role names
    private Boolean enabled; // Optional: to update enabled status
    // Add other fields admin can update, e.g., email, if necessary
    // private String email;
}
