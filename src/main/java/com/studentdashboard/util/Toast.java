package com.studentdashboard.util;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Clean toast notification.
 *
 * <p>
 * A compact card with a small accent dot, message, and optional Undo
 * link. Slides up from the bottom of the window and fades away after 4
 * seconds. Only one toast is shown at a time.
 */
public final class Toast {

    private static Popup currentPopup;
    private static PauseTransition currentHold;
    private static HBox currentCard;

    private Toast() {
    }

    public static void show(Node contextNode, String message) {
        show(contextNode, message, null);
    }

    public static void show(Node contextNode, String message, Runnable onUndo) {
        if (contextNode == null || contextNode.getScene() == null) {
            System.out.println("[Toast] " + message);
            return;
        }
        Window owner = contextNode.getScene().getWindow();
        if (owner == null) {
            System.out.println("[Toast] " + message);
            return;
        }

        dismiss();

        // Small accent dot
        StackPane dot = new StackPane();
        dot.getStyleClass().add("toast-dot");
        dot.setMinSize(8, 8);
        dot.setPrefSize(8, 8);
        dot.setMaxSize(8, 8);

        Label msg = new Label(message);
        msg.setWrapText(true);
        msg.setMaxWidth(360);
        msg.getStyleClass().add("toast-label");

        HBox toast = new HBox(10, dot, msg);
        toast.getStyleClass().add("toast");
        toast.setAlignment(Pos.CENTER_LEFT);

        if (onUndo != null) {
            Button undoBtn = new Button("Undo");
            undoBtn.getStyleClass().add("toast-undo-btn");
            undoBtn.setFocusTraversable(false);
            undoBtn.setOnAction(e -> {
                dismiss();
                Platform.runLater(onUndo);
            });
            toast.getChildren().add(undoBtn);
        }

        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(false);
        popup.setHideOnEscape(false);
        popup.getContent().add(toast);

        currentPopup = popup;
        currentCard = toast;

        popup.show(owner);
        reposition(owner, toast);

        // Slide up + fade in
        toast.setOpacity(0);
        toast.setTranslateY(20);

        FadeTransition fade = new FadeTransition(Duration.millis(200), toast);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(220), toast);
        slide.setFromY(20);
        slide.setToY(0);

        new ParallelTransition(fade, slide).play();

        PauseTransition hold = new PauseTransition(Duration.seconds(4));
        hold.setOnFinished(e -> dismiss());
        hold.play();
        currentHold = hold;
    }

    public static void dismiss() {
        if (currentHold != null) {
            currentHold.stop();
            currentHold = null;
        }
        if (currentCard != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(160), currentCard);
            fade.setFromValue(currentCard.getOpacity());
            fade.setToValue(0);
            HBox card = currentCard;
            fade.setOnFinished(e -> {
                if (currentPopup != null) {
                    currentPopup.hide();
                    currentPopup = null;
                }
                currentCard = null;
                card.setOpacity(1);
            });
            fade.play();
        } else if (currentPopup != null) {
            currentPopup.hide();
            currentPopup = null;
        }
    }

    private static void reposition(Window owner, HBox toast) {
        Platform.runLater(() -> {
            if (currentPopup == null)
                return;
            double w = toast.getWidth() > 0 ? toast.getWidth() : toast.prefWidth(-1);
            double x = owner.getX() + (owner.getWidth() - w) / 2.0;
            double y = owner.getY() + owner.getHeight() - 90;
            currentPopup.setX(x);
            currentPopup.setY(y);
        });
    }
}