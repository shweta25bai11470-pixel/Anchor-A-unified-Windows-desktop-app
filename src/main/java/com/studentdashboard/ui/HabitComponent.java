package com.studentdashboard.ui;

import com.studentdashboard.dao.HabitDAO;
import com.studentdashboard.model.Habit;
import com.studentdashboard.util.DateUtil;
import com.studentdashboard.util.Toast;
import com.studentdashboard.util.UndoManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class HabitComponent {
    private final VBox root = new VBox(20);
    private final VBox habitList = new VBox(10);
    private final Label summary = new Label();

    public HabitComponent(UndoManager undoManager) {
        root.setPadding(new Insets(32, 32, 24, 32));
        root.getStyleClass().add("module-root");
        root.setMaxWidth(Double.MAX_VALUE);
        root.setMaxHeight(Double.MAX_VALUE);
        root.setFillWidth(true);

        Label title = new Label("Habits");
        title.getStyleClass().add("h1");
        Label subtitle = new Label(
                "Tick a habit every day. Streaks build automatically — don't break the chain.");
        subtitle.getStyleClass().add("subtle");
        subtitle.setWrapText(true);

        VBox header = new VBox(4, title, subtitle);

        TextField nameField = new TextField();
        nameField.setPromptText("e.g. Sleep before 11pm, Read 20 pages, Exercise");
        nameField.getStyleClass().add("field");
        HBox.setHgrow(nameField, Priority.ALWAYS);

        Button addBtn = new Button("Add Habit");
        addBtn.getStyleClass().add("primary-btn");
        addBtn.setDefaultButton(true);

        HBox form = new HBox(10, nameField, addBtn);
        form.setAlignment(Pos.CENTER_LEFT);

        VBox addCard = new VBox(12);
        addCard.getStyleClass().add("card");
        addCard.setPadding(new Insets(18));
        addCard.setMaxWidth(Double.MAX_VALUE);
        Label addLabel = new Label("New Habit");
        addLabel.getStyleClass().add("h3");
        addCard.getChildren().addAll(addLabel, form);

        summary.getStyleClass().add("h3");

        VBox listCard = new VBox(12);
        listCard.getStyleClass().add("card");
        listCard.setPadding(new Insets(18));
        listCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(listCard, Priority.ALWAYS);

        Label listLabel = new Label("Today's Habits");
        listLabel.getStyleClass().add("h3");

        habitList.setSpacing(10);
        ScrollPane scroll = new ScrollPane(habitList);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("clean-scroll");
        scroll.setPrefHeight(400);
        scroll.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        listCard.getChildren().addAll(listLabel, scroll);

        root.getChildren().addAll(header, addCard, summary, listCard);

        addBtn.setOnAction(e -> {
            String n = nameField.getText().trim();
            if (n.isEmpty()) {
                warn("Give the habit a name first.");
                return;
            }
            try {
                HabitDAO.add(n, DateUtil.today());
                nameField.clear();
                refresh();
                Toast.show(root, "Habit added: " + n);
            } catch (Exception ex) {
                warn("A habit with this name already exists.");
            }
        });

        nameField.setOnAction(e -> addBtn.fire());

        refresh();
    }

    private void refresh() {
        habitList.getChildren().clear();
        var habits = HabitDAO.allWithStats(DateUtil.today());

        int done = 0;
        for (Habit h : habits)
            if (h.doneToday)
                done++;

        if (habits.isEmpty()) {
            Label empty = new Label(
                    "No habits yet. Add one above — try \"Read 20 pages\" or \"Sleep before 11pm\".");
            empty.getStyleClass().add("subtle");
            empty.setWrapText(true);
            empty.setPadding(new Insets(24));
            habitList.getChildren().add(empty);
            summary.setText("");
            return;
        }

        summary.setText(String.format("Today: %d of %d complete", done, habits.size()));

        for (Habit h : habits) {
            habitList.getChildren().add(buildRow(h));
        }
    }

    private HBox buildRow(Habit h) {
        CheckBox cb = new CheckBox();
        cb.setSelected(h.doneToday);
        cb.getStyleClass().add("big-check");

        Label name = new Label(h.name);
        name.getStyleClass().add("habit-name");

        Label streak = new Label("🔥 " + h.currentStreak);
        streak.getStyleClass().add("streak-pill");
        streak.setVisible(h.currentStreak > 0);
        streak.setManaged(h.currentStreak > 0);

        Label best = new Label("best " + h.longestStreak);
        best.getStyleClass().add("subtle");

        HBox titleBox = new HBox(10, name, streak, best);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        VBox textBox = new VBox(2, titleBox);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Button del = new Button("Remove");
        del.getStyleClass().add("ghost-danger-btn");
        del.setOnAction(e -> {
            AppleAlert.destructive(
                    root.getScene() == null ? null : root.getScene().getWindow(),
                    "Remove habit?",
                    "Remove \"" + h.name + "\"?",
                    "This deletes the habit and all its history. This action cannot be undone.",
                    "Remove",
                    () -> {
                        HabitDAO.delete(h.id);
                        refresh();
                        Toast.show(root, "Habit removed: " + h.name);
                    });
        });

        HBox row = new HBox(14, cb, textBox, del);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 14, 10, 14));
        row.getStyleClass().add("habit-row");
        row.setMaxWidth(Double.MAX_VALUE);
        if (h.doneToday)
            row.getStyleClass().add("habit-row-done");

        cb.setOnAction(e -> {
            HabitDAO.setDone(h.id, DateUtil.today(), cb.isSelected());
            refresh();
            if (cb.isSelected()) {
                Toast.show(root, "Habit complete ✓");
            }
        });

        return row;
    }

    private void warn(String msg) {
        AppleAlert.warn(
                root.getScene() == null ? null : root.getScene().getWindow(),
                "Invalid input",
                "Invalid input",
                msg);
    }

    public VBox getRoot() {
        return root;
    }
}