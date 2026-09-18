package com.studentdashboard.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_DIR = System.getProperty("user.home") + File.separator + ".anchor";
    private static final String DB_URL = "jdbc:sqlite:" + DB_DIR + File.separator + "anchor.db";
    private static Connection connection;

    public static void initialize() {
        try {
            File dir = new File(DB_DIR);
            if (!dir.exists())
                dir.mkdirs();
            connection = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to init DB", e);
        }
    }

    private static void createTables() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS study_sessions (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        date TEXT NOT NULL,
                        start_time TEXT NOT NULL,
                        duration_minutes INTEGER NOT NULL,
                        subject TEXT
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS tasks (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        due_date TEXT,
                        priority TEXT NOT NULL DEFAULT 'Medium',
                        category TEXT,
                        is_complete INTEGER NOT NULL DEFAULT 0,
                        created_date TEXT NOT NULL
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS expenses (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        date TEXT NOT NULL,
                        amount REAL NOT NULL,
                        category TEXT NOT NULL,
                        note TEXT
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS habits (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        created_date TEXT NOT NULL
                    )""");
            st.execute("""
                    CREATE TABLE IF NOT EXISTS habit_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        habit_id INTEGER NOT NULL,
                        date TEXT NOT NULL,
                        is_done INTEGER NOT NULL DEFAULT 0,
                        UNIQUE(habit_id, date),
                        FOREIGN KEY(habit_id) REFERENCES habits(id) ON DELETE CASCADE
                    )""");
        }
    }

    public static Connection get() {
        return connection;
    }
}