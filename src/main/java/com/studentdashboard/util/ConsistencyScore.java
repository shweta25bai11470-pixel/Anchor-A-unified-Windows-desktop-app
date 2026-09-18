package com.studentdashboard.util;

import com.studentdashboard.dao.*;

/**
 * Weighted consistency score for a single day (0–100).
 * Study (target 60 min) → 30%
 * Tasks (target 3 completed) → 20%
 * Spending (under 500) → 20%
 * Habits (all done) → 30%
 */
public class ConsistencyScore {
    public static int forDate(String date) {
        int studyMin = StudyDAO.totalMinutesOn(date);
        int tasksDone = TaskDAO.completedOn(date);
        double spend = ExpenseDAO.totalOn(date);
        int totalHabits = HabitDAO.totalHabits();
        int habitsDone = HabitDAO.completedOn(date);

        double studyPct = Math.min(1.0, studyMin / 60.0);
        double tasksPct = Math.min(1.0, tasksDone / 3.0);
        double spendPct = spend <= 500 ? 1.0 : Math.max(0, 1.0 - (spend - 500) / 500.0);
        double habitPct = totalHabits == 0 ? 0 : Math.min(1.0, (double) habitsDone / totalHabits);

        double score = studyPct * 30 + tasksPct * 20 + spendPct * 20 + habitPct * 30;
        return (int) Math.round(score);
    }
}