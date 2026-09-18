package com.studentdashboard.ui;

import com.studentdashboard.dao.ExpenseDAO;
import com.studentdashboard.model.Expense;
import com.studentdashboard.util.DateUtil;
import com.studentdashboard.util.Toast;
import com.studentdashboard.util.UndoManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class ExpenseComponent {
    private final VBox root = new VBox(20);
    private final ListView<Expense> listView = new ListView<>();
    private final ObservableList<Expense> data = FXCollections.observableArrayList();
    private final Label monthTotal = new Label();
    private final Label countLabel = new Label();
    private final PieChart pieChart = new PieChart();

    public ExpenseComponent(UndoManager undoManager) {
        root.setPadding(new Insets(32, 32, 24, 32));
        root.getStyleClass().add("module-root");
        root.setMaxWidth(Double.MAX_VALUE);
        root.setMaxHeight(Double.MAX_VALUE);
        root.setFillWidth(true);

        Label title = new Label("Expenses");
        title.getStyleClass().add("h1");
        Label subtitle = new Label("Log spending and see where your money goes");
        subtitle.getStyleClass().add("subtle");
        VBox header = new VBox(4, title, subtitle);

        TextField amountField = new TextField();
        amountField.setPromptText("0.00");
        amountField.setPrefWidth(110);
        amountField.getStyleClass().add("field");

        amountField.textProperty().addListener((obs, old, val) -> {
            if (val.isEmpty() || val.matches("\\d*\\.?\\d*")) {
                amountField.getStyleClass().remove("field-error");
            } else {
                if (!amountField.getStyleClass().contains("field-error"))
                    amountField.getStyleClass().add("field-error");
            }
        });

        ComboBox<String> category = new ComboBox<>(FXCollections.observableArrayList(
                "Food", "Transport", "Books", "Subscriptions", "Other"));
        category.setValue("Food");
        category.getStyleClass().add("field");
        category.setPrefWidth(150);

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.getStyleClass().add("field");
        datePicker.setPrefWidth(150);

        TextField noteField = new TextField();
        noteField.setPromptText("Optional note (e.g. Lunch with Sam)");
        noteField.getStyleClass().add("field");
        HBox.setHgrow(noteField, Priority.ALWAYS);

        Button addBtn = new Button("Add Expense");
        addBtn.getStyleClass().add("primary-btn");
        addBtn.setDefaultButton(true);

        HBox form = new HBox(10, amountField, category, datePicker, noteField, addBtn);
        form.setAlignment(Pos.CENTER_LEFT);

        VBox addCard = new VBox(12);
        addCard.getStyleClass().add("card");
        addCard.setPadding(new Insets(18));
        addCard.setMaxWidth(Double.MAX_VALUE);
        Label addLabel = new Label("New Expense");
        addLabel.getStyleClass().add("h3");
        addCard.getChildren().addAll(addLabel, form);

        monthTotal.getStyleClass().add("big-number");
        countLabel.getStyleClass().add("subtle");

        Label totalCaption = new Label("This Month");
        totalCaption.getStyleClass().add("subtle");

        VBox totalCard = new VBox(4);
        totalCard.getStyleClass().add("stat-card-lg");
        totalCard.setPadding(new Insets(18));
        totalCard.getChildren().addAll(totalCaption, monthTotal, countLabel);
        totalCard.setPrefWidth(220);

        pieChart.setPrefWidth(320);
        pieChart.setPrefHeight(280);
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(false);
        pieChart.setTitle(null);

        VBox chartCard = new VBox(10);
        chartCard.getStyleClass().add("card");
        chartCard.setPadding(new Insets(18));
        chartCard.setMaxWidth(Double.MAX_VALUE);
        Label chartLabel = new Label("By Category");
        chartLabel.getStyleClass().add("h3");
        chartCard.getChildren().addAll(chartLabel, pieChart);
        HBox.setHgrow(chartCard, Priority.ALWAYS);

        HBox stats = new HBox(16, totalCard, chartCard);
        stats.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(chartCard, Priority.ALWAYS);

        Label listLabel = new Label("Recent Expenses");
        listLabel.getStyleClass().add("h3");

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.getStyleClass().add("danger-btn");
        deleteBtn.setDisable(true);

        HBox listHeader = new HBox(10, listLabel, spacer(), deleteBtn);
        listHeader.setAlignment(Pos.CENTER_LEFT);
        listHeader.setMaxWidth(Double.MAX_VALUE);

        listView.setItems(data);
        listView.setPrefHeight(280);
        listView.setPlaceholder(new Label("No expenses yet. Add one above to get started."));
        listView.getStyleClass().add("clean-list");
        listView.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(listView, Priority.ALWAYS);

        listView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldV, newV) -> deleteBtn.setDisable(newV == null));

        VBox listCard = new VBox(12, listHeader, listView);
        listCard.getStyleClass().add("card");
        listCard.setPadding(new Insets(18));
        listCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(listCard, Priority.ALWAYS);

        root.getChildren().addAll(header, addCard, stats, listCard);

        addBtn.setOnAction(e -> handleAdd(amountField, category, datePicker, noteField));

        deleteBtn.setOnAction(e -> {
            Expense sel = listView.getSelectionModel().getSelectedItem();
            if (sel == null)
                return;

            final String date = sel.date();
            final double amount = sel.amount();
            final String cat = sel.category();
            final String note = sel.note();

            AppleAlert.destructive(
                    root.getScene() == null ? null : root.getScene().getWindow(),
                    "Delete expense?",
                    "Delete this expense?",
                    String.format("₹%.2f  ·  %s  ·  %s%nThis action can be undone.",
                            amount, cat, date),
                    "Delete",
                    () -> {
                        ExpenseDAO.delete(sel.id());
                        refresh();
                        Toast.show(root, "Expense deleted.", () -> {
                            ExpenseDAO.add(new Expense(0, date, amount, cat, note));
                            refresh();
                        });
                    });
        });

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Expense ex, boolean empty) {
                super.updateItem(ex, empty);
                if (empty || ex == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Label cat = new Label(ex.category());
                cat.getStyleClass().add("chip");
                Label note = new Label(ex.note() == null ? "" : ex.note());
                note.getStyleClass().add("subtle");
                Label date = new Label(ex.date());
                date.getStyleClass().add("subtle");
                Label amt = new Label(String.format("₹%.2f", ex.amount()));
                amt.getStyleClass().add("amount");

                HBox left = new HBox(10, cat, note);
                left.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(left, Priority.ALWAYS);

                HBox right = new HBox(12, date, amt);
                right.setAlignment(Pos.CENTER_RIGHT);

                HBox row = new HBox(10, left, right);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(4, 6, 4, 6));
                setGraphic(row);
            }
        });

        refresh();
    }

    private void handleAdd(TextField amountField, ComboBox<String> category,
            DatePicker datePicker, TextField noteField) {
        String raw = amountField.getText().trim();
        if (raw.isEmpty()) {
            errorAlert("Enter an amount first.");
            return;
        }
        double amt;
        try {
            amt = Double.parseDouble(raw);
        } catch (NumberFormatException ex) {
            errorAlert("Amount must be a number, e.g. 250 or 49.99");
            return;
        }
        if (amt <= 0) {
            errorAlert("Amount must be greater than zero.");
            return;
        }

        LocalDate d = datePicker.getValue() == null ? LocalDate.now() : datePicker.getValue();
        Expense ex = new Expense(0, d.toString(), amt, category.getValue(),
                noteField.getText().isBlank() ? null : noteField.getText().trim());
        ExpenseDAO.add(ex);
        amountField.clear();
        noteField.clear();
        refresh();
        Toast.show(root, String.format("Expense added: ₹%.2f", amt));
    }

    private void errorAlert(String msg) {
        AppleAlert.warn(
                root.getScene() == null ? null : root.getScene().getWindow(),
                "Invalid input",
                "Invalid input",
                msg);
    }

    private Region spacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    private void refresh() {
        String from = DateUtil.startOfMonth();
        String to = DateUtil.today();
        data.setAll(ExpenseDAO.forRange(from, to));

        double total = ExpenseDAO.total(from, to);
        monthTotal.setText(String.format("₹%.2f", total));
        countLabel.setText(data.size() + (data.size() == 1 ? " entry" : " entries"));

        pieChart.getData().clear();
        ExpenseDAO.byCategory(from, to).forEach((cat, amt) -> pieChart.getData().add(new PieChart.Data(
                cat + "  ₹" + String.format("%.0f", amt), amt)));
    }

    public VBox getRoot() {
        return root;
    }
}