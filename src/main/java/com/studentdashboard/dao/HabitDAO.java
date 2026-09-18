package com.studentdashboard.dao;

import com.studentdashboard.db.Database;
import com.studentdashboard.model.Habit;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class HabitDAO {
    public static void add(String name, String createdDate) {
        try (PreparedStatement ps = Database.get().prepareStatement(
                "INSERT INTO habits(name,created_date) VALUES(?,?)")) {
            ps.setString(1, name);
            ps.setString(2, createdDate);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(int id) {
        try (PreparedStatement ps = Database.get().prepareStatement("DELETE FROM habits WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setDone(int habitId, String date, boolean done) {
        try (PreparedStatement ps = Database.get().prepareStatement(
                "INSERT INTO habit_logs(habit_id,date,is_done) VALUES(?,?,?) " +
                        "ON CONFLICT(habit_id,date) DO UPDATE SET is_done=excluded.is_done")) {
            ps.setInt(1, habitId);
            ps.setString(2, date);
            ps.setInt(3, done ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Habit> allWithStats(String today) {
        List<Habit> list = new ArrayList<>();
        try (Statement st = Database.get().createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM habits ORDER BY name")) {
            while (rs.next()) {
                Habit h = new Habit(rs.getInt("id"), rs.getString("name"), rs.getString("created_date"));
                h.doneToday = isDone(h.id, today);
                h.currentStreak = currentStreak(h.id, today);
                h.longestStreak = longestStreak(h.id);
                list.add(h);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static boolean isDone(int habitId, String date) {
        try (PreparedStatement ps = Database.get().prepareStatement(
                "SELECT is_done FROM habit_logs WHERE habit_id=? AND date=?")) {
            ps.setInt(1, habitId);
            ps.setString(2, date);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int currentStreak(int habitId, String today) {
        int streak = 0;
        LocalDate d = LocalDate.parse(today);
        while (isDone(habitId, d.toString())) {
            streak++;
            d = d.minusDays(1);
        }
        return streak;
    }

    public static int longestStreak(int habitId) {
        List<String> dates = new ArrayList<>();
        try (PreparedStatement ps = Database.get().prepareStatement(
                "SELECT date FROM habit_logs WHERE habit_id=? AND is_done=1 ORDER BY date")) {
            ps.setInt(1, habitId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    dates.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int longest = 0, cur = 0;
        LocalDate prev = null;
        for (String ds : dates) {
            LocalDate d = LocalDate.parse(ds);
            if (prev != null && prev.plusDays(1).equals(d))
                cur++;
            else
                cur = 1;
            longest = Math.max(longest, cur);
            prev = d;
        }
        return longest;
    }

    public static int completedOn(String date) {
        try (PreparedStatement ps = Database.get().prepareStatement(
                "SELECT COUNT(*) FROM habit_logs WHERE date=? AND is_done=1")) {
            ps.setString(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static int totalHabits() {
        try (Statement st = Database.get().createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM habits")) {
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}