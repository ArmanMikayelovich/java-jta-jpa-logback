package com.energizeglobal.internship.model;

import com.energizeglobal.internship.util.Validator;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class User {

    private String username;

    @NotNull(message = "birthday field should not be empty")
    private LocalDate birthday;

    @NotNull(message = "email field should be not empty")
    @Size(min = 3,max = 100,message = "Not valid email")
    @Pattern(regexp = Validator.EMAIL_REGEX_PATTERN,message = "Not valid email")
    private String email;

    @NotNull(message = "country field should be not empty")
    @Size(min = 2, max = 25, message = "country should be between 2-25 characters.")
    private String country;

    private boolean isAdmin;

    public User(String username, LocalDate birthday, String email, String country) {
        this.username = username;
        this.birthday = birthday;
        this.email = email;
        this.country = country;
    }

    public User(String username, LocalDate birthday, String email, String country, boolean isAdmin) {
        this.username = username;
        this.birthday = birthday;
        this.email = email;
        this.country = country;
        this.isAdmin = isAdmin;
    }
}
