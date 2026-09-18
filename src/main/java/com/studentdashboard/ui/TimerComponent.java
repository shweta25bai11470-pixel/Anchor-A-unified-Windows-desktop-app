package com.studentdashboard.ui;

import com.studentdashboard.dao.StudyDAO;
import com.studentdashboard.model.StudySession;
import com.studentdashboard.util.DateUtil;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimerComponent {
    private final VBox root = new VBox(20);
    private int sessionLength = 25 * 60;
    private int breakLength = 5 * 60;
    private int remaining = sessionLength;
    private boolean running = false;
    private boolean onBreak = false;
    private final Label timeLabel = new Label();
    private final Label statusLabel = new Label("Ready to focus");
    private final Label todayTotal = new Label();
    private final TextField subjectField = new TextField();
    private Timeline timeline;
    private LocalTime startTime;

    public TimerComponent() {
        root.setPadding(new Insets(32));
        root.getStyleClass().add("module-root");
        root.setMaxWidth(Double.MAX_VALUE);
        root.setMaxHeight(Double.MAX_VALUE);
        root.setFillWidth(true);

        Label title = new Label("Study Timer");
        title.getStyleClass().add("h1");
        Label subtitle = new Label("Pomodoro sessions auto-log to your dashboard when they finish");
        subtitle.getStyleClass().add("subtle");
        VBox header = new VBox(4, title, subtitle);

        timeLabel.getStyleClass().add("timer-display");
        statusLabel.getStyleClass().add("timer-status");

        subjectField.setPromptText("What are you studying? (optional)");
        subjectField.getStyleClass().add("field");
        subjectField.setMaxWidth(320);

        Button startBtn = new Button("Start");
        Button pauseBtn = new Button("Pause");
        Button stopBtn = new Button("Stop & Save");
        Button resetBtn = new Button("Reset");

        startBtn.getStyleClass().add("primary-btn");
        pauseBtn.getStyleClass().add("secondary-btn");
        stopBtn.getStyleClass().add("secondary-btn");
        resetBtn.getStyleClass().add("ghost-btn");

        HBox controls = new HBox(10, startBtn, pauseBtn, stopBtn, resetBtn);
        controls.setAlignment(Pos.CENTER);

        Spinner<Integer> sessionSpin = new Spinner<>(5, 120, 25);
        Spinner<Integer> breakSpin = new Spinner<>(1, 60, 5);
        sessionSpin.setEditable(true);
        breakSpin.setEditable(true);
        sessionSpin.getStyleClass().add("field");
        breakSpin.getStyleClass().add("field");
        sessionSpin.valueProperty().addListener((o, a, b) -> {
            sessionLength = b * 60;
            if (!running && !onBreak) {
                remaining = sessionLength;
                updateDisplay();
            }
        });
        breakSpin.valueProperty().addListener((o, a, b) -> breakLength = b * 60);

        Label sessionCaption = new Label("Session (min)");
        sessionCaption.getStyleClass().add("subtle");
        Label breakCaption = new Label("Break (min)");
        breakCaption.getStyleClass().add("subtle");

        HBox settings = new HBox(12, sessionCaption, sessionSpin, breakCaption, breakSpin);
        settings.setAlignment(Pos.CENTER);

        VBox heroCard = new VBox(20, timeLabel, statusLabel, subjectField, controls, settings);
        heroCard.setAlignment(Pos.CENTER);
        heroCard.setPadding(new Insets(48, 32, 40, 32));
        heroCard.getStyleClass().add("card-hero");
        heroCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(heroCard, Priority.ALWAYS);

        todayTotal.getStyleClass().add("big-number");
        Label totalCaption = new Label("Studied Today");
        totalCaption.getStyleClass().add("subtle");

        VBox totalCard = new VBox(4);
        totalCard.getStyleClass().add("stat-card-lg");
        totalCard.setPadding(new Insets(18));
        totalCard.getChildren().addAll(totalCaption, todayTotal);
        totalCard.setMaxWidth(Double.MAX_VALUE);

        root.getChildren().addAll(header, heroCard, totalCard);

        startBtn.setOnAction(e -> start());
        pauseBtn.setOnAction(e -> pause());
        stopBtn.setOnAction(e -> stopAndSave());
        resetBtn.setOnAction(e -> reset());

        updateDisplay();
        updateTodayTotal();
    }

    private void start() {
        if (running)
            return;
        running = true;
        if (!onBreak && startTime == null)
            startTime = LocalTime.now();
        statusLabel.setText(onBreak ? "On break — breathe" : "Focusing…");

        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.2), timeLabel);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.02);
        pulse.setToY(1.02);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
        timeLabel.setUserData(pulse);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void pause() {
        running = false;
        if (timeline != null)
            timeline.stop();
        Object anim = timeLabel.getUserData();
        if (anim instanceof ScaleTransition st) {
            st.stop();
            timeLabel.setScaleX(1.0);
            timeLabel.setScaleY(1.0);
        }
        statusLabel.setText("Paused");
    }

    private void reset() {
        pause();
        onBreak = false;
        startTime = null;
        remaining = sessionLength;
        statusLabel.setText("Ready to focus");
        updateDisplay();
    }

    private void tick() {
        if (remaining > 0) {
            remaining--;
            updateDisplay();
        } else
            sessionEnded();
    }

    private void sessionEnded() {
        pause();
        if (!onBreak) {
            int minutes = sessionLength / 60;
            StudyDAO.log(new StudySession(0,
                    DateUtil.today(),
                    startTime != null
                            ? startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                            : "00:00",
                    minutes,
                    subjectField.getText().isBlank() ? null : subjectField.getText().trim()));

            AppleAlert.info(
                    root.getScene() == null ? null : root.getScene().getWindow(),
                    "Session complete",
                    "Nice work 🎉",
                    "Session complete. Logged " + minutes + " minutes.");

            onBreak = true;
            remaining = breakLength;
            statusLabel.setText("Break ready — press Start");
            updateTodayTotal();
        } else {
            onBreak = false;
            startTime = null;
            remaining = sessionLength;
            statusLabel.setText("Break done — ready for the next one");
        }
        updateDisplay();
    }

    private void stopAndSave() {
        if (startTime == null || onBreak) {
            reset();
            return;
        }
        int elapsed = sessionLength - remaining;
        int minutes = elapsed / 60;
        if (minutes > 0) {
            StudyDAO.log(new StudySession(0, DateUtil.today(),
                    startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    minutes,
                    subjectField.getText().isBlank() ? null : subjectField.getText().trim()));
            statusLabel.setText("Saved " + minutes + " min");
            updateTodayTotal();
        }
        reset();
    }

    private void updateDisplay() {
        int m = remaining / 60, s = remaining % 60;
        timeLabel.setText(String.format("%02d:%02d", m, s));
    }

    private void updateTodayTotal() {
        int min = StudyDAO.totalMinutesOn(DateUtil.today());
        todayTotal.setText(min >= 60
                ? (min / 60) + "h " + (min % 60) + "m"
                : min + " min");
    }

    public VBox getRoot() {
        return root;
    }
}