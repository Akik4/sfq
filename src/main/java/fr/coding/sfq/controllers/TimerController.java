package fr.coding.sfq.controllers;

import fr.coding.sfq.util.CountdownTimerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the countdown timer.
 * This class manages the timer display and ensures the timer is started only once.
 */
public class TimerController {

    @FXML private Label timerLabel;
    private static boolean alreadyStarted = false;

    /**
     * Initializes the timer controller.
     * This method:
     * - Creates a new countdown timer
     * - Ensures the timer is started only once
     * - Links the timer to the display label
     */
    @FXML
    public void initialize() {
        if (!alreadyStarted) {
            alreadyStarted = true;
            CountdownTimerUtil timer = new CountdownTimerUtil(timerLabel);
            timer.start();
        }
    }
}
