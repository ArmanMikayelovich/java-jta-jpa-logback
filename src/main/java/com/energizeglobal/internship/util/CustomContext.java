package com.energizeglobal.internship.util;

import com.energizeglobal.internship.service.UserService;
import com.energizeglobal.internship.util.exception.ServerSideException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CustomContext {
    private static javax.naming.Context context;



    static {
        try {
            context = (Context) new InitialContext().lookup("java:app");
        } catch (NamingException e) {
            e.printStackTrace();
        }

    }

    public static UserService getUserService() {
        try {
            return (UserService) context.lookup("userService");
        } catch (NamingException e) {
            throw new ServerSideException(e);
        }
    }

}
