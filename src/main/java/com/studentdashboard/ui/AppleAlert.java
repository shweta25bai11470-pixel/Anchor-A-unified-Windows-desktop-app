package com.studentdashboard.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.ParallelTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Custom modal dialog styled to match Anchor's dark theme.
 * Apple HIG-inspired: soft rounded card, centered, dim scrim, tappable
 * icon circle, primary action on the right, cancel on the left.
 */
public final class AppleAlert {

    public enum Kind {
        INFO, WARNING, ERROR
    }

    private AppleAlert() {
    }

    public static void info(Window owner, String title, String header, String body) {
        show(owner, title, header, body, Kind.INFO, "OK", null, null);
    }

    public static void warn(Window owner, String title, String header, String body) {
        show(owner, title, header, body, Kind.WARNING, "OK", null, null);
    }

    public static void confirm(Window owner, String title, String header, String body,
            String confirmLabel, Runnable onConfirm) {
        show(owner, title, header, body, Kind.WARNING, confirmLabel, onConfirm, null);
    }

    public static void destructive(Window owner, String title, String header, String body,
            String confirmLabel, Runnable onConfirm) {
        show(owner, title, header, body, Kind.ERROR, confirmLabel, onConfirm, "destructive");
    }

    private static void show(Window owner, String title, String header, String body,
            Kind kind, String confirmLabel, Runnable onConfirm,
            String variant) {
        Stage dialog = new Stage();
        if (owner != null)
            dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.setTitle(title);

        // Icon circle
        Label iconLabel = new Label(kindGlyph(kind));
        iconLabel.getStyleClass().addAll("alert-icon", "alert-icon-" + kind.name().toLowerCase());

        // Text block
        Label headerLabel = new Label(header);
        headerLabel.getStyleClass().add("alert-header");
        headerLabel.setWrapText(true);

        Label bodyLabel = new Label(body);
        bodyLabel.getStyleClass().add("alert-body");
        bodyLabel.setWrapText(true);
        bodyLabel.setMaxWidth(420);

        VBox textBox = new VBox(6, headerLabel, bodyLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox contentRow = new HBox(16, iconLabel, textBox);
        contentRow.setAlignment(Pos.TOP_LEFT);

        // Buttons
        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("alert-secondary-btn");
        cancelBtn.setCancelButton(true);

        Button confirmBtn = new Button(confirmLabel);
        boolean isDestructive = "destructive".equals(variant);
        confirmBtn.getStyleClass().add(isDestructive
                ? "alert-destructive-btn"
                : "alert-primary-btn");
        confirmBtn.setDefaultButton(true);

        AtomicBoolean confirmed = new AtomicBoolean(false);
        confirmBtn.setOnAction(e -> {
            confirmed.set(true);
            dialog.close();
        });
        cancelBtn.setOnAction(e -> dialog.close());

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER_RIGHT);
        if (onConfirm != null) {
            buttonBar.getChildren().addAll(cancelBtn, confirmBtn);
        } else {
            buttonBar.getChildren().add(confirmBtn);
        }

        VBox card = new VBox(20, contentRow, buttonBar);
        card.getStyleClass().add("alert-card");
        card.setPadding(new Insets(24));
        card.setMaxWidth(520);

        StackPane alertRoot = new StackPane(card);
        alertRoot.getStyleClass().add("alert-scrim");
        alertRoot.setPadding(new Insets(40));

        Scene scene = new Scene(alertRoot);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(
                AppleAlert.class.getResource("/styles.css").toExternalForm());

        dialog.setScene(scene);

        // Fade + scale in
        card.setOpacity(0);
        card.setScaleX(0.96);
        card.setScaleY(0.96);

        FadeTransition fade = new FadeTransition(Duration.millis(160), card);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(160), card);
        scale.setFromX(0.96);
        scale.setFromY(0.96);
        scale.setToX(1.0);
        scale.setToY(1.0);

        new ParallelTransition(fade, scale).play();

        dialog.showAndWait();

        if (confirmed.get() && onConfirm != null) {
            javafx.application.Platform.runLater(onConfirm);
        }
    }

    private static String kindGlyph(Kind kind) {
        return switch (kind) {
            case INFO -> "i";
            case WARNING -> "!";
            case ERROR -> "×";
        };
    }
}