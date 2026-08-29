package com.ner.logistics.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String fullName;

    private String phoneNumber;

    private String role; // ADMIN, FIELD_OFFICER, DRIVER, LOGISTICS_OPERATOR
}
