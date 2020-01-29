package com.energizeglobal.internship.dao;

import com.energizeglobal.internship.model.LoginRequest;
import com.energizeglobal.internship.model.RegistrationRequest;
import com.energizeglobal.internship.model.User;
import com.energizeglobal.internship.util.exception.InvalidCredentialsException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {


    boolean isUsernameExists(String username );

    void register(RegistrationRequest registrationRequest);

    User login(LoginRequest loginRequest) throws InvalidCredentialsException;

    Boolean isAdmin(String username);

    void changeAdminState(String username, boolean adminState) throws SQLException;

    void updatePassword(LoginRequest userCredentials, String newPassword) throws InvalidCredentialsException;

    void updateUserInfo(User user);

    User findByUsername(String username);

    List<User> findAll();

    void remove(String username);
}
