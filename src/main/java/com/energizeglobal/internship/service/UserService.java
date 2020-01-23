package com.energizeglobal.internship.service;

import com.energizeglobal.internship.model.LoginRequest;
import com.energizeglobal.internship.model.RegistrationRequest;
import com.energizeglobal.internship.model.User;

import java.util.List;

public interface UserService {
    boolean isUsernameExists(String username);

    void register(RegistrationRequest registrationRequest);

    User login(LoginRequest loginRequest);

    Boolean isAdmin(String username);

    void changeAdminState(String username, boolean adminState);

    void updatePassword(LoginRequest userCredentials, String newPassword);

    void updateUserInfo(User user);

    List<User> findAll();

    User findByUsername(String username);

    void remove(String username);
}
