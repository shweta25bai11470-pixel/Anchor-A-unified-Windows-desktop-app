package com.studentdashboard.ui;

import com.studentdashboard.dao.*;
import com.studentdashboard.util.ConsistencyScore;
import com.studentdashboard.util.DateUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.Map;

public class DashboardView {
    private final VBox root = new VBox(20);

    public DashboardView() {
        root.setPadding(new Insets(32, 32, 24, 32));
        root.getStyleClass().add("module-root");
        root.setMaxWidth(Double.MAX_VALUE);
        root.setMaxHeight(Double.MAX_VALUE);
        root.setFillWidth(true);

        Label title = new Label("Dashboard");
        title.getStyleClass().add("h1");
        Label subtitle = new Label("Your consistency at a glance");
        subtitle.getStyleClass().add("subtle");
        VBox header = new VBox(4, title, subtitle);

        String today = DateUtil.today();

        int studyMin = StudyDAO.totalMinutesOn(today);
        int tasksDone = TaskDAO.completedOn(today);
        int tasksPend = TaskDAO.pendingCount();
        double spent = ExpenseDAO.totalOn(today);
        int habitsDone = HabitDAO.completedOn(today);
        int totalHabit = HabitDAO.totalHabits();
        int score = ConsistencyScore.forDate(today);

        HBox cards = new HBox(14,
                stat("Study", studyMin + " min", "today"),
                stat("Tasks", tasksDone + " done", tasksPend + " pending"),
                stat("Spent", String.format("₹%.0f", spent), "today"),
                stat("Habits", habitsDone + " / " + totalHabit, "complete"),
                stat("Consistency", score + "", "out of 100"));
        cards.setAlignment(Pos.CENTER_LEFT);
        cards.setMaxWidth(Double.MAX_VALUE);
        for (var n : cards.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);

        CategoryAxis x1 = new CategoryAxis();
        NumberAxis y1 = new NumberAxis();
        BarChart<String, Number> studyChart = new BarChart<>(x1, y1);
        studyChart.setTitle("Study minutes — last 7 days");
        studyChart.setPrefHeight(280);
        studyChart.setLegendVisible(false);
        studyChart.setAnimated(false);
        studyChart.getStyleClass().add("card");
        studyChart.setMaxWidth(Double.MAX_VALUE);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Integer> studyMap = DashboardDAO.studyByDay(DateUtil.daysAgo(6), today);
        for (int i = 6; i >= 0; i--) {
            String d = LocalDate.now().minusDays(i).toString();
            series.getData().add(new XYChart.Data<>(d.substring(5), studyMap.getOrDefault(d, 0)));
        }
        studyChart.getData().add(series);

        CategoryAxis x2 = new CategoryAxis();
        NumberAxis y2 = new NumberAxis();
        LineChart<String, Number> spendChart = new LineChart<>(x2, y2);
        spendChart.setTitle("Spending — last 7 days");
        spendChart.setPrefHeight(280);
        spendChart.setLegendVisible(false);
        spendChart.setAnimated(false);
        spendChart.getStyleClass().add("card");
        spendChart.setMaxWidth(Double.MAX_VALUE);

        XYChart.Series<String, Number> spendSeries = new XYChart.Series<>();
        Map<String, Double> spendMap = DashboardDAO.spendByDay(DateUtil.daysAgo(6), today);
        for (int i = 6; i >= 0; i--) {
            String d = LocalDate.now().minusDays(i).toString();
            spendSeries.getData().add(new XYChart.Data<>(d.substring(5), spendMap.getOrDefault(d, 0.0)));
        }
        spendChart.getData().add(spendSeries);

        HBox charts = new HBox(14, studyChart, spendChart);
        charts.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(studyChart, Priority.ALWAYS);
        HBox.setHgrow(spendChart, Priority.ALWAYS);

        Label heatTitle = new Label("Activity — last 60 days");
        heatTitle.getStyleClass().add("h3");

        VBox heatCard = new VBox(12, heatTitle, buildHeatmap(60));
        heatCard.getStyleClass().add("card");
        heatCard.setPadding(new Insets(18));
        heatCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(heatCard, Priority.ALWAYS);

        root.getChildren().addAll(header, cards, charts, heatCard);

        boolean hasAnyData = DashboardDAO.anyActivity(today) > 0
                || StudyDAO.totalMinutes(today, today) > 0
                || TaskDAO.pendingCount() > 0
                || HabitDAO.totalHabits() > 0;

        if (!hasAnyData) {
            Label empty = new Label(
                    "Welcome to Anchor.\n\n" +
                            "Start by logging a study session, adding a task, or creating a habit.\n" +
                            "Your dashboard will fill in as you go.");
            empty.getStyleClass().add("empty-state");
            empty.setWrapText(true);
            root.getChildren().add(empty);
        }
    }

    private VBox stat(String label, String value, String sub) {
        Label l = new Label(label);
        l.getStyleClass().add("subtle");
        Label v = new Label(value);
        v.getStyleClass().add("card-value");
        Label s = new Label(sub);
        s.getStyleClass().add("subtle");
        VBox box = new VBox(4, l, v, s);
        box.getStyleClass().add("stat-card");
        box.setPadding(new Insets(16));
        box.setPrefWidth(180);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private GridPane buildHeatmap(int days) {
        GridPane grid = new GridPane();
        grid.setHgap(4);
        grid.setVgap(4);

        LocalDate start = LocalDate.now().minusDays(days - 1);
        for (int i = 0; i < days; i++) {
            LocalDate d = start.plusDays(i);
            int activity = DashboardDAO.anyActivity(d.toString());

            Region cell = new Region();
            cell.setPrefSize(18, 18);
            String color;
            if (activity == 0)
                color = "#1a1f2b";
            else if (activity <= 1)
                color = "#2a5a3a";
            else if (activity <= 3)
                color = "#3f8a4f";
            else if (activity <= 5)
                color = "#6ce67d";
            else
                color = "#a0f0b0";

            cell.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
            Tooltip.install(cell, new Tooltip(d.toString() + " — " + activity + " activities"));
            cell.setAccessibleText(d.toString() + ", " + activity + " activities");
            cell.setAccessibleRole(AccessibleRole.TEXT);
            cell.setFocusTraversable(true);

            grid.add(cell, i % 15, i / 15);
        }
        return grid;
    }

    public VBox getRoot() {
        return root;
    }
}