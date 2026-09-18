package com.studentdashboard.model;

public record Expense(int id, String date, double amount, String category, String note) {
}