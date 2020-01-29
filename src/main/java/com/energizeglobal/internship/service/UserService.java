package com.energizeglobal.internship.service;

import com.energizeglobal.internship.model.LoginRequest;
import com.energizeglobal.internship.model.RegistrationRequest;
import com.energizeglobal.internship.model.User;
import com.energizeglobal.internship.util.exception.ServerSideException;
import lombok.extern.log4j.Log4j;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;
import java.util.Properties;

public interface UserService {
    static UserService getInstance() {
        Properties properties = new Properties();
        properties.put("jboss.naming.client.ejb.context", true);
        properties.put(Context.URL_PKG_PREFIXES,"org.jboss.ejb.client.naming");
        Context ctx= null;
        try {
            ctx = new InitialContext(properties);
            return  (UserService) ctx.lookup("java:module/userService");
        } catch (NamingException e) {
            throw new ServerSideException(e);
        }
    }
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
