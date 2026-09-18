package com.studentdashboard.ui;

import com.studentdashboard.util.UndoManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;

public class MainWindow {
    private final BorderPane root = new BorderPane();
    private final StackPane content = new StackPane();
    private final VBox sidebar = new VBox(6);
    private Button activeNavBtn;
    private final UndoManager undoManager = new UndoManager();

    public MainWindow() {
        root.getStyleClass().add("app-root");

        MenuBar menuBar = buildMenuBar();
        root.setTop(menuBar);

        content.getStyleClass().add("content-area");
        content.setMaxWidth(Double.MAX_VALUE);
        content.setMaxHeight(Double.MAX_VALUE);

        buildSidebar();
        root.setLeft(sidebar);
        root.setCenter(content);

        showDashboard();
    }

    private MenuBar buildMenuBar() {
        MenuBar bar = new MenuBar();
        bar.getStyleClass().add("menu-bar");

        Menu file = new Menu("File");
        MenuItem newExpense = new MenuItem("New Expense");
        newExpense.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        newExpense.setOnAction(e -> showExpenses());
        MenuItem newTask = new MenuItem("New Task");
        newTask.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
        newTask.setOnAction(e -> showTodos());
        MenuItem newHabit = new MenuItem("New Habit");
        newHabit.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
        newHabit.setOnAction(e -> showHabits());
        MenuItem quit = new MenuItem("Quit");
        quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        quit.setOnAction(e -> javafx.application.Platform.exit());
        file.getItems().addAll(newExpense, newTask, newHabit,
                new SeparatorMenuItem(), quit);

        Menu edit = new Menu("Edit");
        MenuItem undo = new MenuItem("Undo");
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        undo.setOnAction(e -> undoManager.undo());
        edit.getItems().add(undo);

        Menu view = new Menu("View");
        MenuItem dashboard = new MenuItem("Dashboard");
        dashboard.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.CONTROL_DOWN));
        dashboard.setOnAction(e -> showDashboard());
        MenuItem timer = new MenuItem("Timer");
        timer.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.CONTROL_DOWN));
        timer.setOnAction(e -> showTimer());
        MenuItem todos = new MenuItem("To-Dos");
        todos.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT3, KeyCombination.CONTROL_DOWN));
        todos.setOnAction(e -> showTodos());
        MenuItem expenses = new MenuItem("Expenses");
        expenses.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT4, KeyCombination.CONTROL_DOWN));
        expenses.setOnAction(e -> showExpenses());
        MenuItem habits = new MenuItem("Habits");
        habits.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT5, KeyCombination.CONTROL_DOWN));
        habits.setOnAction(e -> showHabits());
        view.getItems().addAll(dashboard, timer, todos, expenses, habits);

        Menu help = new Menu("Help");
        MenuItem about = new MenuItem("About Anchor");
        about.setOnAction(e -> showAbout());
        help.getItems().add(about);

        bar.getMenus().addAll(file, edit, view, help);
        return bar;
    }

    private void buildSidebar() {
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.setPrefWidth(220);
        sidebar.setMinWidth(190);
        sidebar.setMaxWidth(260);

        Label logo = new Label("⚓  Anchor");
        logo.getStyleClass().add("logo");
        sidebar.getChildren().add(logo);
        sidebar.getChildren().add(spacer(16));

        sidebar.getChildren().addAll(
                navBtn("Dashboard", this::showDashboard),
                navBtn("Timer", this::showTimer),
                navBtn("To-Dos", this::showTodos),
                navBtn("Expenses", this::showExpenses),
                navBtn("Habits", this::showHabits));
    }

    private Button navBtn(String text, Runnable action) {
        Button b = new Button(text);
        b.getStyleClass().add("nav-btn");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setOnAction(e -> {
            if (activeNavBtn != null)
                activeNavBtn.getStyleClass().remove("nav-btn-active");
            b.getStyleClass().add("nav-btn-active");
            activeNavBtn = b;
            action.run();
        });
        return b;
    }

    private Region spacer(double h) {
        Region r = new Region();
        r.setPrefHeight(h);
        return r;
    }

    private void setContent(Node n) {
        if (n instanceof Region r) {
            r.setMaxWidth(Double.MAX_VALUE);
            r.setMaxHeight(Double.MAX_VALUE);
        }
        content.getChildren().setAll(n);
        StackPane.setAlignment(n, Pos.TOP_LEFT);

        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(180), n);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    private void showDashboard() {
        setContent(new DashboardView().getRoot());
        highlightNav("Dashboard");
    }

    private void showTimer() {
        setContent(new TimerComponent().getRoot());
        highlightNav("Timer");
    }

    private void showTodos() {
        setContent(new TodoComponent(undoManager).getRoot());
        highlightNav("To-Dos");
    }

    private void showExpenses() {
        setContent(new ExpenseComponent(undoManager).getRoot());
        highlightNav("Expenses");
    }

    private void showHabits() {
        setContent(new HabitComponent(undoManager).getRoot());
        highlightNav("Habits");
    }

    private void highlightNav(String label) {
        for (Node n : sidebar.getChildren()) {
            if (n instanceof Button b && b.getText().equals(label)) {
                if (activeNavBtn != null)
                    activeNavBtn.getStyleClass().remove("nav-btn-active");
                b.getStyleClass().add("nav-btn-active");
                activeNavBtn = b;
            }
        }
    }

    private void showAbout() {
        AppleAlert.info(
                root.getScene() == null ? null : root.getScene().getWindow(),
                "About Anchor",
                "Anchor — Student Life Dashboard",
                "A unified view of study, tasks, expenses, and habits.\n\n" +
                        "Version 1.0\nLocal-only. No cloud. No accounts.\n\n" +
                        "Press F11 to toggle full-screen.");
    }

    public BorderPane getRoot() {
        return root;
    }
}