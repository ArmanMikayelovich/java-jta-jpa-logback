package com.energizeglobal.internship.service;

import com.energizeglobal.internship.dao.UserDao;
import com.energizeglobal.internship.model.LoginRequest;
import com.energizeglobal.internship.model.RegistrationRequest;
import com.energizeglobal.internship.model.User;
import com.energizeglobal.internship.util.DSUtil;
import com.energizeglobal.internship.util.exception.ServerSideException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.sql.DataSource;
import javax.transaction.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Data
@TransactionManagement(TransactionManagementType.BEAN)
@Stateless
public class UserServiceWithJTA implements UserService {
    private static UserService userService = new UserServiceWithJTA();

    public static UserService getInstance() {
        return null;
    }

    @Resource
    UserTransaction tx;

    @EJB
    UserDao userDao;

    DataSource mySqlDataSource = DSUtil.getDataSource();


    @Override
    public boolean isUsernameExists(String username) {
        log.debug("starting transaction for checking is username exists");
        try {
            tx.begin();
            try (Connection connection = getConnection()) {
                boolean usernameExists = userDao.isUsernameExists(username, connection);
                connection.commit();
                tx.commit();
                log.debug("transaction successfully finished");
                return usernameExists;
            }
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
    public void register(RegistrationRequest registrationRequest) {
        try {
            tx.begin();
            try (Connection connection = getConnection()) {
                userDao.register(registrationRequest, connection);
                connection.commit();
                tx.commit();
                log.debug("transaction successfully finished");
            }
        } catch (NotSupportedException | SystemException | SQLException | ServerSideException |
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
        log.debug("starting transaction for login processing");
        try {
            tx.begin();
            try (Connection connection = getConnection()) {
                connection.setAutoCommit(false);
                final User user = userDao.login(loginRequest, connection);
                connection.commit();
                tx.commit();
                log.debug("transaction successfully finished");
                return user;
            }
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
    public Boolean isAdmin(String username) {
        log.debug("starting transaction for checking user's isAdmin ");
        try {
            tx.begin();
            try (Connection connection = getConnection()) {
                final Boolean isAdmin = userDao.isAdmin(username, connection);
                connection.commit();
                tx.commit();
                log.debug("transaction successfully finished");
                return isAdmin;
            }
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
    public void changeAdminState(String username, boolean adminState) {
        log.debug("starting transaction for changing admin state");
        try {
            tx.begin();
            try (Connection connection = getConnection()) {
                userDao.changeAdminState(username, adminState, connection);
                connection.commit();//TODO check connection's autoCommit flag
                tx.commit();
                log.debug("transaction successfully finished");
            }
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
            try (Connection connection = getConnection()) {
                userDao.updatePassword(userCredentials, newPassword, connection);
                connection.commit();
                tx.commit();
                log.debug("transaction successfully finished");
            }
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
    public void updateUserInfo(User user) {
        log.debug("starting transaction for update user info");
        try {
            tx.begin();
            try (Connection connection = getConnection()) {
                userDao.updateUserInfo(user, connection);
                connection.commit();
                tx.commit();
                log.debug("transaction successfully finished");
            }
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
    public List<User> findAll() {
        log.debug("starting transaction for getting all users");
        try {
            tx.begin();
            try (Connection connection = getConnection()) {
                final List<User> allUsers = userDao.findAll(connection);
                connection.commit();
                tx.commit();
                log.debug("transaction successfully finished");
                return allUsers;
            }
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
    public User findByUsername(String username) {
        log.debug("starting transaction for finding by username");
        try {
            tx.begin();
            try (Connection connection = getConnection()) {
                final User user = userDao.findByUsername(username, connection);
                connection.commit();
                tx.commit();
                log.debug("transaction successfully finished");
                return user;
            }
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
    public void remove(String username) {
        log.debug("starting transaction for finding by username");
        try {
            tx.begin();
            try (Connection connection = getConnection()) {
                userDao.remove(username, connection);
                connection.commit();
                tx.commit();
                log.debug("transaction successfully finished");
                ;
            }
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

    private Connection getConnection() throws SQLException {
        return mySqlDataSource.getConnection();
    }
}
