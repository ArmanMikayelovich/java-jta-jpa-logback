package com.energizeglobal.internship.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotNull(message = "username should be not empty")
    @Size(min = 4, max = 25, message = "username should be between 4-25 characters.")
    private String username;
    @NotNull(message = "password should be not empty")
    @Size(min = 8, max = 25, message = "password should be between 8-25 characters:")
    private String password;
}
