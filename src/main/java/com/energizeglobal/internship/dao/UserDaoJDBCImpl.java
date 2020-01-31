package com.energizeglobal.internship.dao;

import com.energizeglobal.internship.model.LoginRequest;
import com.energizeglobal.internship.model.RegistrationRequest;
import com.energizeglobal.internship.model.User;
import com.energizeglobal.internship.util.DSUtil;
import com.energizeglobal.internship.util.DateConverter;
import com.energizeglobal.internship.util.exception.InvalidCredentialsException;
import com.energizeglobal.internship.util.exception.ServerSideException;
import com.energizeglobal.internship.util.exception.UsernameAlreadyExists;
import com.energizeglobal.internship.util.exception.UsernameNotFountException;
import lombok.extern.slf4j.Slf4j;

import javax.ejb.Stateless;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.energizeglobal.internship.util.DateConverter.convertDateToLocalDate;
import static com.energizeglobal.internship.util.DateConverter.convertLocalDateToSqlDate;

@Slf4j
@Stateless
public class UserDaoJDBCImpl implements UserDao {

    private UserDaoJDBCImpl() {
    }

    DataSource mySqlDataSource = DSUtil.getDataSource();


    private static final String DB_URL = "db.url";
    private static final String USERNAME = "db.username";
    private static final String PASSWORD = "db.password";
    private static final String JDBC_DRIVER = "db.driver";


    private static final String REGISTER_QUERY = "INSERT INTO users " +
            "(username, password, birthday, email, country) " +
            "values (?,?,?,?,?)";
    private static final String USERNAME_CHECK_QUERY = "SELECT username from users where username=?";
    private static final String LOGIN_QUERY = "SELECT username,birthday,email,country " +
            "FROM users " +
            "WHERE username=? AND password=?";
    private static final String IS_ADMIN_QUERY = "SELECT isAdmin FROM users WHERE username=?";

    private static final String CHANGE_ADMIN_STATE_QUERY = "UPDATE users SET isAdmin = ? WHERE username=?";

    private static final String FIND_ALL_USERS_QUERY = "SELECT username, birthday, email, country, isAdmin FROM users";

    private static final String DELETE_QUERY = "DELETE FROM users WHERE username = ?";

    private static final String GET_PASSWORD = "SELECT password FROM users WHERE username = ?";
    private static final String UPDATE_PASSWORD_QUERY = "UPDATE users SET password =? WHERE username=?";
    private static final String UPDATE_USER = "UPDATE users SET birthday=?, email=?, country =? WHERE username=?";
    private static final String FIND_USER_BY_USERNAME = "SELECT username, birthday, email, country, isAdmin from users WHERE username =?";

    @Override
    public boolean isUsernameExists(String username) {

        final Connection connection = getConnection();
        log.debug("checking is username exists: {}", username);
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            preparedStatement = connection.prepareStatement(USERNAME_CHECK_QUERY);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (SQLException ex) {

            log.error("An error occurred when we checking is username exists: {}, \n error message: {}, \n query is: {}"
                    , ex.getSQLState(), ex.getMessage(), USERNAME_CHECK_QUERY);
            throw new ServerSideException();

        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
        }
    }

    @Override
    public void register(RegistrationRequest registrationRequest) {
        log.debug("trying to register: {}", registrationRequest);
        if (isUsernameExists(registrationRequest.getUsername())) {
            throw new UsernameAlreadyExists();
        }
        final Connection connection = getConnection();
        PreparedStatement preparedStatement = null;

        try {

            preparedStatement = connection.prepareStatement(REGISTER_QUERY);
            preparedStatement.setString(1, registrationRequest.getUsername());
            preparedStatement.setString(2, registrationRequest.getPassword());
            preparedStatement.setDate(3, convertLocalDateToSqlDate(registrationRequest.getBirthday()));
            preparedStatement.setString(4, registrationRequest.getEmail());
            preparedStatement.setString(5, registrationRequest.getCountry());
            preparedStatement.execute();

            log.debug("successfully registered: {}", registrationRequest);
            connection.commit();

        } catch (SQLException e) {
            rollBackConnection(connection);
            log.error("An error occurred in registration process: {}, \n query is: {}, registrationRequest is: {}"
                    , e.getSQLState(), REGISTER_QUERY, registrationRequest);
            throw new ServerSideException(e);

        } finally {
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public User login(LoginRequest loginRequest) {
        log.debug("login: {}", loginRequest);

        final Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(LOGIN_QUERY);
            preparedStatement.setString(1, loginRequest.getUsername());
            preparedStatement.setString(2, loginRequest.getPassword());

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                final String username = resultSet.getString("username");
                final LocalDate birthday = DateConverter.convertDateToLocalDate(resultSet.getDate("birthday"));
                final String email = resultSet.getString("email");
                final String country = resultSet.getString("country");
                return new User(username, birthday, email, country);
            }

            throw new InvalidCredentialsException();
        } catch (SQLException e) {

            rollBackConnection(connection);
            log.error("An error occurred in login process: {}, query: {}", e.getSQLState(), LOGIN_QUERY);
            throw new ServerSideException();

        } finally {

            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);

        }
    }

    @Override
    public Boolean isAdmin(String username) {
        log.debug("checking, is user admin: {}", username);
        if (!isUsernameExists(username)) {
            throw new UsernameNotFountException();
        }

        final Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(IS_ADMIN_QUERY);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            log.debug("is user {} admin : {}", username, resultSet.getBoolean("isAdmin"));
            return resultSet.getBoolean("isAdmin");

        } catch (SQLException e) {

            log.error("An error occurred in login process: {}", e.getSQLState());
            throw new ServerSideException();

        } finally {
            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public void changeAdminState(String username, boolean adminState) {
        final Connection connection = getConnection();
        log.debug("changing admin state of user: {}", username);
        if (!isUsernameExists(username)) {
            throw new UsernameNotFountException();
        }

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(CHANGE_ADMIN_STATE_QUERY);
            preparedStatement.setBoolean(1, adminState);
            preparedStatement.setString(2, username);
            preparedStatement.execute();
            connection.commit();

        } catch (SQLException e) {

            rollBackConnection(connection);
            log.error("An error occurred in admin state changing process: {},query is: {}",
                    e.getSQLState(), CHANGE_ADMIN_STATE_QUERY);
            throw new ServerSideException(e);

        } finally {

            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public void updatePassword(LoginRequest userCredentials, String newPassword) {
        log.debug("changing password for {}", userCredentials.getUsername());

        if (!isUsernameExists(userCredentials.getUsername())) {
            throw new UsernameNotFountException();
        }

        final Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {

            preparedStatement = connection.prepareStatement(GET_PASSWORD);
            preparedStatement.setString(1, userCredentials.getUsername());
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            final String password = resultSet.getString("password");

            if (userCredentials.getPassword().equals(password)) {
                changePassword(userCredentials.getUsername(), newPassword, connection);

            } else {
                throw new InvalidCredentialsException();
            }

        } catch (SQLException ex) {

            rollBackConnection(connection);
            log.error("An error occurred in login process: {}", ex.getSQLState());
            throw new ServerSideException();

        } finally {

            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    private void changePassword(String username, String newPassword, Connection connection) {
        PreparedStatement updatePasswordStatement = null;
        try {
            updatePasswordStatement = connection.prepareStatement(UPDATE_PASSWORD_QUERY);
            updatePasswordStatement.setString(1, newPassword);
            updatePasswordStatement.setString(2, username);
            updatePasswordStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            log.error("An error accured in password changing process " +
                            ": {}, query is: {}, username: {} , new password:{} "
                    , e.getMessage(), UPDATE_PASSWORD_QUERY, username, newPassword);
        } finally {
            closePreparedStatement(updatePasswordStatement);
        }
    }

    @Override
    public void updateUserInfo(User user) {
        log.debug("updating user info: {}", user.getUsername());
        if (!isUsernameExists(user.getUsername())) {
            throw new UsernameNotFountException();
        }
        final Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {

            preparedStatement = connection.prepareStatement(UPDATE_USER);

            preparedStatement.setDate(1, convertLocalDateToSqlDate(user.getBirthday()));
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getCountry());
            preparedStatement.setString(4, user.getUsername());
            preparedStatement.executeUpdate();
            connection.commit();

        } catch (SQLException e) {

            rollBackConnection(connection);
            log.error("An error occurred in login process: {}", e.getSQLState());
            throw new ServerSideException();

        } finally {

            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
    }


    @Override
    public User findByUsername(String username) {
        log.debug("searching user by username: {}", username);
        if (!isUsernameExists(username)) {
            throw new UsernameNotFountException();
        }
        final Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            preparedStatement = connection.prepareStatement(FIND_USER_BY_USERNAME);
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            final String usernameFromDB = resultSet.getString("username");
            final LocalDate birthday = convertDateToLocalDate(resultSet.getDate("birthday"));
            final String email = resultSet.getString("email");
            final String country = resultSet.getString("country");
            final boolean isAdmin = resultSet.getBoolean("isAdmin");
            final User user = new User(usernameFromDB, birthday, email, country, isAdmin);

            log.debug("found user: {}", user);
            return user;

        } catch (SQLException e) {

            log.error("An error occurred in login process. SQL state: {}, errorMessage: {}, query: {}",
                    e.getSQLState(), e.getMessage(), FIND_USER_BY_USERNAME);
            throw new ServerSideException(e);

        } finally {

            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    @Override
    public List<User> findAll() {
        final Connection connection = getConnection();
        log.debug("find all users id db");
        final List<User> users = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(FIND_ALL_USERS_QUERY);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                final String username = resultSet.getString("username");
                final Date birthday = resultSet.getDate("birthday");
                final String email = resultSet.getString("email");
                final String country = resultSet.getString("country");
                final Boolean isAdmin = resultSet.getBoolean("isAdmin");
                users.add(new User(username, birthday.toLocalDate(), email, country, isAdmin));
            }

            log.debug("Found {} users", users.size());
        } catch (SQLException e) {
            log.error("An error occurred in findAll() method. SQL state: {}, errorMessage: {}, query: {}"
                    , e.getSQLState(), e.getMessage(), FIND_ALL_USERS_QUERY);
            throw new ServerSideException(e);
        } finally {

            closeResultSet(resultSet);
            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
        return users;
    }

    @Override
    public void remove(String username) {
        log.debug("deleting user: {}", username);
        if (!isUsernameExists(username)) {
            throw new UsernameNotFountException();
        }
        final Connection connection = getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(DELETE_QUERY);
            preparedStatement.setString(1, username);
            preparedStatement.execute();
            log.debug("user {} deleted", username);
            connection.commit();

        } catch (SQLException e) {
           rollBackConnection(connection);
            log.error("An error occurred in login process: {}", e.getSQLState());
            throw new ServerSideException();
        } finally {

            closePreparedStatement(preparedStatement);
            closeConnection(connection);
        }
    }

    private Connection getConnection() {
        log.debug("creating new connection");
        try {
            final Connection connection = mySqlDataSource.getConnection();
            connection.setAutoCommit(false);
            log.debug("connection created - {}", connection.toString());
            return connection;
        } catch (SQLException e) {
            log.debug("an exception threw in connection creating step - {1}", e);
            throw new ServerSideException(e);
        }
    }

    private void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                log.debug("closing preparedStatement {}", preparedStatement);
                preparedStatement.close();
            } catch (SQLException e) {
                log.error("error in closing preparedStatement {}, query is: {}", preparedStatement, REGISTER_QUERY);
            }
        }
    }

    private void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.error("error in closing resultSet {}, \t query is: {},\n sql state : \n {}"
                        , resultSet, USERNAME_CHECK_QUERY, e.getSQLState());
            }
        }
    }

    private void rollBackConnection(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ex) {
            log.error("error in transaction rollback process {}, \n query is: {}", ex.toString(), REGISTER_QUERY);
        }
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            try {
                log.error("error in connection closing process, connection is: {},\n DB meta data: {}"
                        , connection, connection.getMetaData());
            } catch (SQLException ex) {
                log.error("error in connection closing process, connection is: {}" +
                        "\n and error in DB meta data reading process. {}", connection, ex.getMessage());
            }
        }

    }
}
