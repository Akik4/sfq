package fr.coding.sfq.util;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;

public class CountdownTimerUtil extends Thread {

    // Timer duration in seconds (25 minutes)
    private static int value = 25 * 60;

    private final Label label;

    // var to indicate if the "Create Order" button should be disabled
    private static final BooleanProperty shouldDisableOrderButton = new SimpleBooleanProperty(false);

    public CountdownTimerUtil(Label label) {
        this.label = label;
    }

    public static int getRemainingTime() {
        return value;
    }

    public static BooleanProperty shouldDisableOrderButtonProperty() {
        return shouldDisableOrderButton;
    }

    @Override
    public void run() {
        // Timer loop that runs until value >= 0
        while (value >= 0) {
            int minutes = value / 60;
            int seconds = value % 60;

            Platform.runLater(() -> {
                label.setText(String.format("%02d:%02d", minutes, seconds));
                shouldDisableOrderButton.set(value < 15 * 60);
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }

            value--; // Decrement the timer
        }

        Platform.runLater(() -> label.setText("Terminé !"));
    }
}
