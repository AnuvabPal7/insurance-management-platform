package com.insurance.management.dto;

import com.insurance.management.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9_]{2,19}$",
             message = "Username must start with a letter and be 3-20 characters (letters, numbers, underscore only)")
    private String username;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;
}