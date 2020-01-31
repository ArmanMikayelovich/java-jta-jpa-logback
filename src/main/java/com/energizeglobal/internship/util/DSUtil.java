package com.energizeglobal.internship.util;

import com.energizeglobal.internship.util.exception.ServerSideException;
import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

@Slf4j
public class DSUtil {
    private static DataSource mysqlDS = getDataSource();
    public static final String MYSQL_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    public static DataSource getDataSource() {
        mysqlDS = new MysqlDataSource();

        try {
            Class.forName(MYSQL_DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            log.error("can't find class {}", MYSQL_DRIVER_CLASS);
            throw new ServerSideException(e);
        }
        ((MysqlDataSource) mysqlDS).setURL("jdbc:mysql://localhost/web?createDatabaseIfNotExist=true");
        ((MysqlDataSource) mysqlDS).setUser("root");
        ((MysqlDataSource) mysqlDS).setPassword("root");
        prepareDB(mysqlDS, "DDL.sql");
        return mysqlDS;
    }

    /**
     * Too long wrote method.
     *
     * @param dataSource
     */
    private static void prepareDB(DataSource dataSource, String sqlFileName) {
        InputStream ddlStream = null;
        ddlStream = DSUtil.class.getClassLoader().getResourceAsStream(sqlFileName);
        if (ddlStream == null) {
            throw new ServerSideException("Can't find file DDL.sql");
        }

        StringBuilder ddlBuilder = new StringBuilder();
        Scanner scanner = new Scanner(ddlStream);

        while (scanner.hasNextLine()) {
            ddlBuilder.append(scanner.nextLine());
        }
        scanner.close();
        try {
            ddlStream.close();
        } catch (IOException e) {
            log.error("Error occured in closing inputStream {}", ddlStream);
        }
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            log.error("Error in creating connection from DataSource - {}", dataSource);
            throw new ServerSideException(e);
        }

        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(ddlBuilder.toString());
        } catch (SQLException e) {
            log.debug("Error in preparing statent for query {} in connection {}",
                    ddlBuilder.toString(), connection);
            throw new ServerSideException(e);
        }

        try {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Error in execution prepareStatement {} with query {}", preparedStatement, ddlBuilder.toString());
            throw new ServerSideException(e);
        }
        try {
            preparedStatement.close();
        } catch (SQLException e) {
            log.error("Error occurred in closing prepareStatement {}", preparedStatement);
            throw new ServerSideException(e);
        }
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Error occurred in closing connection {}", connection);
            throw new ServerSideException(e);
        }

    }
}
