package com.studentdashboard.dao;

import com.studentdashboard.db.Database;
import com.studentdashboard.model.StudySession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudyDAO {
    public static void log(StudySession s) {
        String sql = "INSERT INTO study_sessions(date,start_time,duration_minutes,subject) VALUES(?,?,?,?)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, s.date());
            ps.setString(2, s.startTime());
            ps.setInt(3, s.durationMinutes());
            ps.setString(4, s.subject());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static List<StudySession> forRange(String from, String to) {
        List<StudySession> list = new ArrayList<>();
        String sql = "SELECT * FROM study_sessions WHERE date BETWEEN ? AND ? ORDER BY date, start_time";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, from); ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new StudySession(rs.getInt("id"), rs.getString("date"),
                        rs.getString("start_time"), rs.getInt("duration_minutes"),
                        rs.getString("subject")));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public static int totalMinutes(String from, String to) {
        String sql = "SELECT COALESCE(SUM(duration_minutes),0) FROM study_sessions WHERE date BETWEEN ? AND ?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, from); ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) { return rs.getInt(1); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static int totalMinutesOn(String date) { return totalMinutes(date, date); }
}