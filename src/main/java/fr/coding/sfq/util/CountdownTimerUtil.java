package fr.coding.sfq.util;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;

public class CountdownTimerUtil extends Thread {

    private static int value = 15 * 61;
    private final Label label;

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

            value--;
        }

        Platform.runLater(() -> label.setText("Terminé !"));
    }
}
