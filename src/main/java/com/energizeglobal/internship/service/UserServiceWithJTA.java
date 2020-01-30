package com.energizeglobal.internship.service;

import com.energizeglobal.internship.dao.UserDao;
import com.energizeglobal.internship.model.LoginRequest;
import com.energizeglobal.internship.model.RegistrationRequest;
import com.energizeglobal.internship.model.User;
import com.energizeglobal.internship.util.exception.ServerSideException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.*;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Data
@TransactionManagement(TransactionManagementType.BEAN)
@Stateless(name = "userService")
public class UserServiceWithJTA implements UserService {

    @Resource
    UserTransaction tx;

    @EJB
    UserDao userDao;


    @Override
    public boolean isUsernameExists(String username) {
        log.debug("start checking is username exists");
        return userDao.isUsernameExists(username);

    }

    @Override
    public void register(RegistrationRequest registrationRequest) {
        try {
            tx.begin();

            userDao.register(registrationRequest);

            tx.commit();
            log.debug("transaction successfully finished");

        } catch (NotSupportedException | SystemException | ServerSideException |
                RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            log.debug("error in transaction.");
            try {
                tx.rollback();
            } catch (SystemException ex) {
                throw new ServerSideException(ex);
            }
            throw new ServerSideException(e);
        }
    }

    @Override
    public User login(LoginRequest loginRequest) {
        log.debug("starting  login processing");
        final User user = userDao.login(loginRequest);
        return user;
    }

    @Override
    public Boolean isAdmin(String username) {
        log.debug("starting transaction for checking user's isAdmin ");

        final Boolean isAdmin = userDao.isAdmin(username);
        log.debug("transaction successfully finished");
        return isAdmin;
    }

    @Override
    public void changeAdminState(String username, boolean adminState) {
        log.debug("starting transaction for changing admin state");
        try {
            tx.begin();
            userDao.changeAdminState(username, adminState);
            tx.commit();
            log.debug("transaction successfully finished");
        } catch (NotSupportedException | SystemException | SQLException | ServerSideException
                | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            log.debug("error in transaction.");
            try {
                tx.rollback();
            } catch (SystemException ex) {
                throw new ServerSideException(ex);
            }
            throw new ServerSideException(e);
        }
    }

    @Override
    public void updatePassword(LoginRequest userCredentials, String newPassword) {
        log.debug("starting transaction for update password");
        try {
            tx.begin();
            userDao.updatePassword(userCredentials, newPassword);
            tx.commit();
            log.debug("transaction successfully finished");

        } catch (NotSupportedException | SystemException | ServerSideException
                | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            log.debug("error in transaction.");
            try {
                tx.rollback();
            } catch (SystemException ex) {
                throw new ServerSideException(ex);
            }
            throw new ServerSideException(e);
        }
    }

    @Override
    public void updateUserInfo(User user) {
        log.debug("starting transaction for update user info");
        try {
            tx.begin();
            userDao.updateUserInfo(user);
            tx.commit();
            log.debug("transaction successfully finished");
        } catch (NotSupportedException | SystemException | ServerSideException
                | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            log.debug("error in transaction.");
            try {
                tx.rollback();
            } catch (SystemException ex) {
                throw new ServerSideException(ex);
            }
            throw new ServerSideException(e);
        }
    }

    @Override
    public List<User> findAll() {
        log.debug("starting  getting all users");
            return  userDao.findAll();
    }

    @Override
    public User findByUsername(String username) {
        log.debug("start for finding by username");
        return userDao.findByUsername(username);
    }

    @Override
    public void remove(String username) {
        log.debug("starting transaction for finding by username");
        try {
            tx.begin();
            userDao.remove(username);
            tx.commit();
            log.debug("transaction successfully finished");

        } catch (NotSupportedException | SystemException | ServerSideException
                | RollbackException | HeuristicMixedException | HeuristicRollbackException e) {
            log.debug("error in transaction.");
            try {
                tx.rollback();
            } catch (SystemException ex) {
                throw new ServerSideException(ex);
            }
            throw new ServerSideException(e);
        }
    }


}
