package com.energizeglobal.internship.util;

import com.energizeglobal.internship.service.UserService;
import com.energizeglobal.internship.service.UserServiceWithJTA;
import com.energizeglobal.internship.util.exception.ServerSideException;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Context {
    private static
    javax.naming.Context context;

    static {
        try {
            final Properties properties = new Properties();
            properties.loadFromXML(Context.class.getClassLoader().getResourceAsStream("context.xml"));
            final EJBContainer ejbContainer = EJBContainer.createEJBContainer(properties);
            ejbContainer.getContext().bind("userService", new UserServiceWithJTA());
            context = ejbContainer.getContext();
        } catch (NamingException | IOException e) {
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
