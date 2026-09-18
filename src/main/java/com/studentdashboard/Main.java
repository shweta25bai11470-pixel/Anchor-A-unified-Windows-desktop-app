package com.studentdashboard;

import com.studentdashboard.db.Database;
import com.studentdashboard.ui.MainWindow;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Database.initialize();
        MainWindow window = new MainWindow();

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        Scene scene = new Scene(window.getRoot(),
                Math.min(1400, bounds.getWidth() * 0.95),
                Math.min(900, bounds.getHeight() * 0.95));
        scene.getStylesheets().add(
                getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Anchor — Student Life Dashboard");
        stage.setScene(scene);
        stage.setMinWidth(1000);
        stage.setMinHeight(650);

        // Center on primary screen.
        stage.setX(bounds.getMinX() + (bounds.getWidth() - scene.getWidth()) / 2);
        stage.setY(bounds.getMinY() + (bounds.getHeight() - scene.getHeight()) / 2);

        // Show the window FIRST — this is the key. Windows needs a valid
        // on-screen window before it will accept a fullscreen transition.
        stage.show();

        // Then, after the window is definitely visible, go fullscreen.
        PauseTransition delay = new PauseTransition(Duration.millis(250));
        delay.setOnFinished(e -> stage.setFullScreen(true));
        delay.play();

        // Hide the default "Press ESC to exit full screen" hint.
        stage.setFullScreenExitHint("");
        // Keep Esc as an exit key — standard behavior, users expect it.
        stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("ESC"));

        // F11 toggles fullscreen (standard Windows behavior).
        scene.getAccelerators().put(
                new javafx.scene.input.KeyCodeCombination(KeyCode.F11),
                () -> stage.setFullScreen(!stage.isFullScreen()));

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}