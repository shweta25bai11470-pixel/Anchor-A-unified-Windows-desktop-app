package com.studentdashboard.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    public static String today() {
        return LocalDate.now().format(ISO);
    }

    public static String daysAgo(int n) {
        return LocalDate.now().minusDays(n).format(ISO);
    }

    public static String startOfMonth() {
        return LocalDate.now().withDayOfMonth(1).format(ISO);
    }

    public static String startOfWeek() {
        return LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).format(ISO);
    }
}