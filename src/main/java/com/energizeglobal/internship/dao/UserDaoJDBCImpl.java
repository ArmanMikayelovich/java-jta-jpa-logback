package com.energizeglobal.internship.dao;

import com.energizeglobal.internship.model.LoginRequest;
import com.energizeglobal.internship.model.RegistrationRequest;
import com.energizeglobal.internship.model.User;
import com.energizeglobal.internship.util.DateConverter;
import com.energizeglobal.internship.util.Properties;
import com.energizeglobal.internship.util.exception.InvalidCredentialsException;
import com.energizeglobal.internship.util.exception.ServerSideException;
import com.energizeglobal.internship.util.exception.UsernameAlreadyExists;
import com.energizeglobal.internship.util.exception.UsernameNotFountException;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.energizeglobal.internship.util.DateConverter.convertDateToLocalDate;
import static com.energizeglobal.internship.util.DateConverter.convertLocalDateToSqlDate;

@Slf4j
public class UserDaoJDBCImpl implements UserDao {
    private static UserDaoJDBCImpl userDaoJDBC = new UserDaoJDBCImpl();

    private UserDaoJDBCImpl() {
    }

    public static UserDaoJDBCImpl getInstance() {
        return userDaoJDBC;
    }

    private static final String DB_URL = "db.url";
    private static final String USERNAME = "db.username";
    private static final String PASSWORD = "db.password";
    private static final String JDBC_DRIVER = "db.driver";


    static {
        try {
            Class.forName(Properties.get(JDBC_DRIVER));
        } catch (ClassNotFoundException e) {
            log.error("Class not found {}", JDBC_DRIVER);
        }
    }
    private static final String REGISTER_QUERY = "INSERT INTO users " +
            "(username, password, birthday, email, country) " +
            "values (?,?,?,?,?)";
    private static final String USERNAME_CHECK_QUERY = "SELECT username from users where username=?";
    private static final String LOGIN_QUERY = "SELECT username,birthday,email,country " +
            "FROM users " +
            "WHERE username=? AND password=?";
    private static final String IS_ADMIN_QUERY = "SELECT isAdmin FROM users WHERE username=?";

    private static final String CHANGE_ADMIN_QUERY = "UPDATE users SET isAdmin = ? WHERE username=?";

    private static final String FIND_ALL_USERS = "SELECT username, birthday, email, country, isAdmin FROM users";

    private static final String DELETE_QUERY = "DELETE FROM users WHERE username = ?";

    private static final String GET_PASSWORD = "SELECT password FROM users WHERE username = ?";
    private static final String UPDATE_PASSWORD = "UPDATE users SET password =? WHERE username=?";
    private static final String UPDATE_USER = "UPDATE users SET birthday=?, email=?, country =? WHERE username=?";
    private static final String FIND_USER_BY_USERNAME = "SELECT username, birthday, email, country, isAdmin from users WHERE username =?";

    @Override
    public boolean isUsernameExists(String username) {
        log.debug("checking is username exists: {}", username);
        try (final Connection connection = getConnection()) {
            final PreparedStatement preparedStatement = connection.prepareStatement(USERNAME_CHECK_QUERY);

            preparedStatement.setString(1, username);
            @Cleanup final ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();

        } catch (SQLException ex) {
            log.error("An error occurred when we checking is username exists: {}", ex.getSQLState());
            throw new ServerSideException();
        }
    }

    @Override
    public void register(RegistrationRequest registrationRequest) {
        log.debug("trying to register: {}", registrationRequest);
        if (isUsernameExists(registrationRequest.getUsername())) {
            throw new UsernameAlreadyExists();
        }

        try (final Connection connection =getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(REGISTER_QUERY);) {

            preparedStatement.setString(1, registrationRequest.getUsername());
            preparedStatement.setString(2, registrationRequest.getPassword());
            preparedStatement.setDate(3, convertLocalDateToSqlDate(registrationRequest.getBirthday()));
            preparedStatement.setString(4, registrationRequest.getEmail());
            preparedStatement.setString(5, registrationRequest.getCountry());
            preparedStatement.execute();
            log.debug("successfully registered: {}", registrationRequest);
        } catch (SQLException e) {
            log.error("An error occurred in registration process: {}", e.getSQLState());
            throw new ServerSideException();

        }
    }

    @Override
    public User login(LoginRequest loginRequest) throws InvalidCredentialsException {
        log.debug("login: {}", loginRequest);
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(LOGIN_QUERY)) {

            preparedStatement.setString(1, loginRequest.getUsername());
            preparedStatement.setString(2, loginRequest.getPassword());

            @Cleanup final ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                final String username = resultSet.getString("username");
                final LocalDate birthday = DateConverter.convertDateToLocalDate(resultSet.getDate("birthday"));
                final String email = resultSet.getString("email");
                final String country = resultSet.getString("country");
                return new User(username, birthday, email, country);
            }

            throw new InvalidCredentialsException();
        } catch (SQLException e) {
            log.error("An error occurred in login process: {}", e.getSQLState());
            throw new ServerSideException();
        }
    }

    @Override
    public Boolean isAdmin(String username) {
        log.debug("checking, is user admin: {}", username);
        if (!isUsernameExists(username)) {
            throw new UsernameNotFountException();
        }

        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(IS_ADMIN_QUERY)) {

            preparedStatement.setString(1, username);
            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            log.debug("is user {} admin : {}", username, resultSet.getBoolean("isAdmin"));
            return resultSet.getBoolean("isAdmin");

        } catch (SQLException e) {
            log.error("An error occurred in login process: {}", e.getSQLState());
            throw new ServerSideException();
        }
    }

    @Override
    public void changeAdminState(String username, boolean adminState) {
        log.debug("changing admin state of user: {}", username);
        if (!isUsernameExists(username)) {
            throw new UsernameNotFountException();
        }
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(CHANGE_ADMIN_QUERY)) {

            preparedStatement.setBoolean(1, adminState);
            preparedStatement.setString(2, username);
            preparedStatement.execute();

        } catch (SQLException e) {
            log.error("An error occurred in admin state changing process: {}", e.getSQLState());
            throw new ServerSideException();
        }
    }

    @Override
    public void updatePassword(LoginRequest userCredentials, String newPassword) throws InvalidCredentialsException {
        log.debug("changing password for {}", userCredentials.getUsername());
        if (!isUsernameExists(userCredentials.getUsername())) {
            throw new UsernameNotFountException();
        }

        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(GET_PASSWORD)) {

            preparedStatement.setString(1, userCredentials.getUsername());
            final ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            final String password = resultSet.getString("password");

            if (userCredentials.getPassword().equals(password)) {

                @Cleanup final PreparedStatement updatePasswordStatement = connection.prepareStatement(UPDATE_PASSWORD);
                updatePasswordStatement.setString(1, newPassword);
                updatePasswordStatement.setString(2, userCredentials.getUsername());
                updatePasswordStatement.executeUpdate();

            } else {
                throw new InvalidCredentialsException();
            }
        } catch (SQLException ex) {
            log.error("An error occurred in login process: {}", ex.getSQLState());
            throw new ServerSideException();
        }
    }

    @Override
    public void updateUserInfo(User user) {
        log.debug("updating user info: {}", user.getUsername());
        if (!isUsernameExists(user.getUsername())) {
            throw new UsernameNotFountException();
        }
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER);) {


            preparedStatement.setDate(1, convertLocalDateToSqlDate(user.getBirthday()));
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getCountry());
            preparedStatement.setString(4, user.getUsername());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error("An error occurred in login process: {}", e.getSQLState());
            throw new ServerSideException();
        }
    }


    @Override
    public User findByUsername(String username) {
        log.debug("searching user by username: {}", username);
        if (!isUsernameExists(username)) {
            throw new UsernameNotFountException();
        }
        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_USERNAME)) {

            preparedStatement.setString(1, username);
            @Cleanup final ResultSet resultSet = preparedStatement.executeQuery();
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
            log.error("An error occurred in login process: {}", e.getSQLState());
            throw new ServerSideException();
        }
    }

    @Override
    public List<User> findAll() {
        log.debug("find all users id db");
        final List<User> users = new ArrayList<>();

        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_USERS)) {

            @Cleanup ResultSet resultSet = preparedStatement.executeQuery();

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
            throw new ServerSideException();
        }
        return users;
    }

    @Override
    public void remove(String username) {
        log.debug("deleting user: {}", username);
        if (!isUsernameExists(username)) {
            throw new UsernameNotFountException();
        }

        try (final Connection connection = getConnection();
             final PreparedStatement preparedStatement
                     = connection.prepareStatement(DELETE_QUERY)) {
            preparedStatement.setString(1, username);
            preparedStatement.execute();
            log.debug("user {} deleted", username);
        } catch (SQLException e) {
            log.error("An error occurred in login process: {}", e.getSQLState());
            throw new ServerSideException();
        }
    }


    private Connection getConnection() throws SQLException {
        final String dBURL = Properties.get(DB_URL);
        final String dBUsername = Properties.get(USERNAME);
        final String dBPassword = Properties.get(PASSWORD);
        return DriverManager.getConnection(dBURL, dBUsername, dBPassword);
    }
}
