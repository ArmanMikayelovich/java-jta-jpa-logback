package com.energizeglobal.internship.util;

import com.mysql.cj.jdbc.MysqlDataSource;
import lombok.SneakyThrows;

import javax.sql.DataSource;

public class DSUtil {
    private static DataSource mysqlDS = getDataSource();

    public static DataSource getInstance() {
        return mysqlDS;
    }

    @SneakyThrows
    public static DataSource getDataSource() {

        try {
            mysqlDS = new MysqlDataSource();

            Class.forName("com.mysql.cj.jdbc.Driver");
            ((MysqlDataSource) mysqlDS).setURL("jdbc:mysql://localhost/web?createDatabaseIfNotExist=true");
            ((MysqlDataSource) mysqlDS).setUser("root");
            ((MysqlDataSource) mysqlDS).setPassword("root");
            return mysqlDS;
        } finally {

        }
    }
}
