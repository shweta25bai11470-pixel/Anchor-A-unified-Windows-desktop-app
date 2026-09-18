package com.studentdashboard.ui;

import com.studentdashboard.dao.TaskDAO;
import com.studentdashboard.model.Task;
import com.studentdashboard.util.DateUtil;
import com.studentdashboard.util.Toast;
import com.studentdashboard.util.UndoManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class TodoComponent {
    private final VBox root = new VBox(20);
    private final ListView<Task> listView = new ListView<>();
    private final ObservableList<Task> data = FXCollections.observableArrayList();
    private final Label summary = new Label();

    public TodoComponent(UndoManager undoManager) {
        root.setPadding(new Insets(32, 32, 24, 32));
        root.getStyleClass().add("module-root");
        root.setMaxWidth(Double.MAX_VALUE);
        root.setMaxHeight(Double.MAX_VALUE);
        root.setFillWidth(true);

        Label title = new Label("To-Dos");
        title.getStyleClass().add("h1");
        Label subtitle = new Label("Everything you need to do, sorted by urgency");
        subtitle.getStyleClass().add("subtle");
        VBox header = new VBox(4, title, subtitle);

        TextField titleField = new TextField();
        titleField.setPromptText("What needs doing?");
        titleField.getStyleClass().add("field");
        HBox.setHgrow(titleField, Priority.ALWAYS);

        DatePicker duePicker = new DatePicker();
        duePicker.setPromptText("Due");
        duePicker.getStyleClass().add("field");
        duePicker.setPrefWidth(140);

        ComboBox<String> priority = new ComboBox<>(
                FXCollections.observableArrayList("Low", "Medium", "High"));
        priority.setValue("Medium");
        priority.getStyleClass().add("field");
        priority.setPrefWidth(110);

        TextField category = new TextField();
        category.setPromptText("Category");
        category.getStyleClass().add("field");
        category.setPrefWidth(130);

        Button addBtn = new Button("Add Task");
        addBtn.getStyleClass().add("primary-btn");
        addBtn.setDefaultButton(true);

        HBox form = new HBox(10, titleField, duePicker, priority, category, addBtn);
        form.setAlignment(Pos.CENTER_LEFT);

        VBox addCard = new VBox(12);
        addCard.getStyleClass().add("card");
        addCard.setPadding(new Insets(18));
        addCard.setMaxWidth(Double.MAX_VALUE);
        Label addLabel = new Label("New Task");
        addLabel.getStyleClass().add("h3");
        addCard.getChildren().addAll(addLabel, form);

        summary.getStyleClass().add("h3");
        listView.setItems(data);
        listView.setPrefHeight(420);
        listView.setPlaceholder(new Label("No tasks yet. Add one above."));
        listView.getStyleClass().add("clean-list");
        listView.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(listView, Priority.ALWAYS);

        Button completeBtn = new Button("Mark Complete");
        completeBtn.getStyleClass().add("primary-btn");
        completeBtn.setDisable(true);

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("danger-btn");
        deleteBtn.setDisable(true);

        HBox actions = new HBox(10, completeBtn, deleteBtn);
        HBox listHeader = new HBox(10, summary, spacer(), actions);
        listHeader.setAlignment(Pos.CENTER_LEFT);
        listHeader.setMaxWidth(Double.MAX_VALUE);

        VBox listCard = new VBox(12, listHeader, listView);
        listCard.getStyleClass().add("card");
        listCard.setPadding(new Insets(18));
        listCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(listCard, Priority.ALWAYS);

        root.getChildren().addAll(header, addCard, listCard);

        listView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean none = n == null;
            completeBtn.setDisable(none);
            deleteBtn.setDisable(none);
        });

        addBtn.setOnAction(e -> {
            String t = titleField.getText().trim();
            if (t.isEmpty()) {
                warn("Task title cannot be empty.");
                return;
            }
            LocalDate d = duePicker.getValue();
            Task task = new Task(0, t,
                    d == null ? null : d.toString(),
                    priority.getValue(),
                    category.getText().isBlank() ? null : category.getText().trim(),
                    false, DateUtil.today());
            TaskDAO.add(task);
            titleField.clear();
            duePicker.setValue(null);
            category.clear();
            refresh();
            Toast.show(root, "Task added: " + t);
        });
        titleField.setOnAction(e -> addBtn.fire());

        completeBtn.setOnAction(e -> {
            Task sel = listView.getSelectionModel().getSelectedItem();
            if (sel == null)
                return;
            TaskDAO.setComplete(sel.id, !sel.isComplete);
            refresh();
        });

        deleteBtn.setOnAction(e -> {
            Task sel = listView.getSelectionModel().getSelectedItem();
            if (sel == null)
                return;

            final Task snapshot = new Task(0, sel.title, sel.dueDate, sel.priority,
                    sel.category, sel.isComplete, sel.createdDate);

            AppleAlert.destructive(
                    root.getScene() == null ? null : root.getScene().getWindow(),
                    "Delete task?",
                    "Delete \"" + sel.title + "\"?",
                    "This action can be undone.",
                    "Delete",
                    () -> {
                        TaskDAO.delete(sel.id);
                        refresh();
                        Toast.show(root, "Task deleted.", () -> {
                            TaskDAO.add(snapshot);
                            refresh();
                        });
                    });
        });

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Task t, boolean empty) {
                super.updateItem(t, empty);
                if (empty || t == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                CheckBox cb = new CheckBox();
                cb.setSelected(t.isComplete);
                cb.getStyleClass().add("big-check");
                cb.setOnAction(e -> {
                    TaskDAO.setComplete(t.id, cb.isSelected());
                    refresh();
                    if (cb.isSelected()) {
                        Toast.show(root, "Task complete ✓");
                    }
                });

                Label titleLbl = new Label(t.title);
                titleLbl.getStyleClass().add(t.isComplete ? "task-done" : "task-title");

                Label pri = new Label(t.priority);
                pri.getStyleClass().addAll("chip", "pri-" + t.priority.toLowerCase());

                HBox titleRow = new HBox(8, titleLbl, pri);
                titleRow.setAlignment(Pos.CENTER_LEFT);

                String dueText = (t.dueDate == null || t.dueDate.isBlank())
                        ? "no due date"
                        : "due " + t.dueDate;
                Label dueLabel = new Label(dueText);
                dueLabel.getStyleClass().add("subtle");

                if (!t.isComplete && t.dueDate != null && !t.dueDate.isBlank()) {
                    LocalDate dueDate = LocalDate.parse(t.dueDate);
                    LocalDate today = LocalDate.now();
                    if (dueDate.isBefore(today)) {
                        dueLabel.getStyleClass().add("overdue");
                    } else if (dueDate.isEqual(today) || dueDate.isEqual(today.plusDays(1))) {
                        dueLabel.getStyleClass().add("due-soon");
                    }
                }

                VBox left = new VBox(2, titleRow, dueLabel);
                HBox.setHgrow(left, Priority.ALWAYS);

                HBox row = new HBox(12, cb, left);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(6));
                setGraphic(row);
            }
        });

        refresh();
    }

    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    private void warn(String m) {
        AppleAlert.warn(
                root.getScene() == null ? null : root.getScene().getWindow(),
                "Invalid input",
                "Invalid input",
                m);
    }

    private void refresh() {
        data.setAll(TaskDAO.all());
        long done = data.stream().filter(t -> t.isComplete).count();
        summary.setText(done + " of " + data.size() + " complete");
    }

    public VBox getRoot() {
        return root;
    }
}