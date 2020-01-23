package com.energizeglobal.internship.dao;

import com.energizeglobal.internship.model.LoginRequest;
import com.energizeglobal.internship.model.RegistrationRequest;
import com.energizeglobal.internship.model.User;
import com.energizeglobal.internship.util.exception.InvalidCredentialsException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {


    boolean isUsernameExists(String username, Connection connection);

    void register(RegistrationRequest registrationRequest, Connection connection);

    User login(LoginRequest loginRequest, Connection connection) throws InvalidCredentialsException;

    Boolean isAdmin(String username, Connection connection);

    void changeAdminState(String username, boolean adminState, Connection connection) throws SQLException;

    void updatePassword(LoginRequest userCredentials, String newPassword, Connection connection) throws InvalidCredentialsException;

    void updateUserInfo(User user, Connection connection);

    User findByUsername(String username, Connection connection);

    List<User> findAll(Connection connection);

    void remove(String username, Connection connection);
}
