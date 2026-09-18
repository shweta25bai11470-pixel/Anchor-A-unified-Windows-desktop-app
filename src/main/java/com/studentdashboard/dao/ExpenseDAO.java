package com.studentdashboard.dao;

import com.studentdashboard.db.Database;
import com.studentdashboard.model.Expense;
import java.sql.*;
import java.util.*;

public class ExpenseDAO {
    public static void add(Expense e) {
        try (PreparedStatement ps = Database.get().prepareStatement(
                "INSERT INTO expenses(date,amount,category,note) VALUES(?,?,?,?)")) {
            ps.setString(1, e.date()); ps.setDouble(2, e.amount());
            ps.setString(3, e.category()); ps.setString(4, e.note());
            ps.executeUpdate();
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }

    public static void delete(int id) {
        try (PreparedStatement ps = Database.get().prepareStatement("DELETE FROM expenses WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static List<Expense> forRange(String from, String to) {
        List<Expense> list = new ArrayList<>();
        try (PreparedStatement ps = Database.get().prepareStatement(
                "SELECT * FROM expenses WHERE date BETWEEN ? AND ? ORDER BY date DESC")) {
            ps.setString(1, from); ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new Expense(rs.getInt("id"), rs.getString("date"),
                    rs.getDouble("amount"), rs.getString("category"), rs.getString("note")));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    public static double total(String from, String to) {
        try (PreparedStatement ps = Database.get().prepareStatement(
                "SELECT COALESCE(SUM(amount),0) FROM expenses WHERE date BETWEEN ? AND ?")) {
            ps.setString(1, from); ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) { return rs.getDouble(1); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public static double totalOn(String date) { return total(date, date); }

    public static Map<String, Double> byCategory(String from, String to) {
        Map<String, Double> map = new LinkedHashMap<>();
        try (PreparedStatement ps = Database.get().prepareStatement(
                "SELECT category, SUM(amount) s FROM expenses WHERE date BETWEEN ? AND ? GROUP BY category ORDER BY s DESC")) {
            ps.setString(1, from); ps.setString(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) map.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return map;
    }
}