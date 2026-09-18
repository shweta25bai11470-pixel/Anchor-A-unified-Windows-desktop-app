package com.studentdashboard.model;

public class Task {
    public int id;
    public String title;
    public String dueDate;
    public String priority;
    public String category;
    public boolean isComplete;
    public String createdDate;

    public Task(int id, String title, String dueDate, String priority,
            String category, boolean isComplete, String createdDate) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.priority = priority;
        this.category = category;
        this.isComplete = isComplete;
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        String mark = isComplete ? "✔" : "☐";
        String due = (dueDate == null || dueDate.isBlank()) ? "no due date" : "due " + dueDate;
        return mark + "  " + title + "   [" + priority + "]   (" + due + ")";
    }
}