package com.studentdashboard.model;

public class Habit {
    public int id;
    public String name;
    public String createdDate;
    public boolean doneToday;
    public int currentStreak;
    public int longestStreak;

    public Habit(int id, String name, String createdDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
    }
}