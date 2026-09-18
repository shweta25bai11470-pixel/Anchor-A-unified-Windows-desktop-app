package com.studentdashboard.dao;

import com.studentdashboard.db.Database;
import com.studentdashboard.model.Task;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    public static void add(Task t) {
        String sql = "INSERT INTO tasks(title,due_date,priority,category,is_complete,created_date) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, t.title);
            ps.setString(2, t.dueDate);
            ps.setString(3, t.priority);
            ps.setString(4, t.category);
            ps.setInt(5, t.isComplete ? 1 : 0);
            ps.setString(6, t.createdDate);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static void update(Task t) {
        String sql = "UPDATE tasks SET title=?,due_date=?,priority=?,category=?,is_complete=? WHERE id=?";
        try (PreparedStatement ps = Database.get().prepareStatement(sql)) {
            ps.setString(1, t.title); ps.setString(2, t.dueDate);
            ps.setString(3, t.priority); ps.setString(4, t.category);
            ps.setInt(5, t.isComplete ? 1 : 0); ps.setInt(6, t.id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static void delete(int id) {
        try (PreparedStatement ps = Database.get().prepareStatement("DELETE FROM tasks WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static void setComplete(int id, boolean complete) {
        try (PreparedStatement ps = Database.get().prepareStatement("UPDATE tasks SET is_complete=? WHERE id=?")) {
            ps.setInt(1, complete ? 1 : 0); ps.setInt(2, id); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static List<Task> all() {
        List<Task> list = new ArrayList<>();
        try (Statement st = Database.get().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM tasks ORDER BY is_complete, due_date IS NULL, due_date, priority")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public static int completedOn(String date) {
        try (PreparedStatement ps = Database.get().prepareStatement(
                "SELECT COUNT(*) FROM tasks WHERE is_complete=1 AND (date(created_date)=? OR date(due_date)=?)")) {
            ps.setString(1, date); ps.setString(2, date);
            try (ResultSet rs = ps.executeQuery()) { return rs.getInt(1); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static int pendingCount() {
        try (Statement st = Database.get().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tasks WHERE is_complete=0")) {
            return rs.getInt(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private static Task map(ResultSet rs) throws SQLException {
        return new Task(rs.getInt("id"), rs.getString("title"), rs.getString("due_date"),
            rs.getString("priority"), rs.getString("category"),
            rs.getInt("is_complete") == 1, rs.getString("created_date"));
    }
}