package fr.coding.sfq.util;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class CountdownTimerUtil extends Thread {

    private int value = 1 * 60;
    private final Label label;

    public CountdownTimerUtil(Label label) {
        this.value = value;
        this.label = label;

    }

    @Override
    public void run() {
        while (value >= 0) { // boucle tant que le chrono n'est pas fini
            int minutes = value / 60; // pour determiner les minutes qui reste pour l'affichage
            int seconds = value % 60; // pour determiner les secondes qui reste pour l'affichage

            // affichage formaté
            Platform.runLater(() -> label.setText(String.format("%02d:%02d", minutes, seconds)));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }

            value--;
        }

        // Quand c’est terminé
        Platform.runLater(() -> label.setText("Terminé !"));
    }
}
