package com.studentdashboard.model;

public record StudySession(int id, String date, String startTime,
        int durationMinutes, String subject) {
}