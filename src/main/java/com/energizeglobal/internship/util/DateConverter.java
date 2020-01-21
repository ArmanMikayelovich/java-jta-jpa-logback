package com.energizeglobal.internship.util;

import java.time.LocalDate;

public class DateConverter {
    private DateConverter() {
    }

    public static LocalDate convertDateToLocalDate(java.sql.Date dateToConvert) {
        return dateToConvert.toLocalDate();
    }

    public static java.sql.Date convertLocalDateToSqlDate(LocalDate dateToConvert) {
        return java.sql.Date.valueOf(dateToConvert);
    }

}
