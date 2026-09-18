package com.studentdashboard.dao;

import com.studentdashboard.db.Database;
import java.sql.*;
import java.util.*;

public class DashboardDAO {
    public static Map<String, Integer> studyByDay(String from, String to) {
        Map<String, Integer> map = new LinkedHashMap<>();
        try (PreparedStatement ps = Database.get().prepareStatement(
                "SELECT date, SUM(duration_minutes) s FROM study_sessions WHERE date BETWEEN ? AND ? GROUP BY date")) {
            ps.setString(1, from);
            ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    map.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static Map<String, Double> spendByDay(String from, String to) {
        Map<String, Double> map = new LinkedHashMap<>();
        try (PreparedStatement ps = Database.get().prepareStatement(
                "SELECT date, SUM(amount) s FROM expenses WHERE date BETWEEN ? AND ? GROUP BY date")) {
            ps.setString(1, from);
            ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    map.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static int anyActivity(String date) {
        int total = 0;
        total += count("SELECT COUNT(*) FROM study_sessions WHERE date=?", date);
        total += count("SELECT COUNT(*) FROM expenses WHERE date=?", date);
        total += count("SELECT COUNT(*) FROM habit_logs WHERE date=? AND is_done=1", date);
        total += count("SELECT COUNT(*) FROM tasks WHERE is_complete=1 AND date(created_date)=?", date);
        return total;
    }

    private static int count(String sql, String date) {
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}