package fr.coding.sfq;

import fr.coding.sfq.util.CountdownTimerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TimerController {

    @FXML private Label timerLabel;
    private static boolean alreadyStarted = false;

    @FXML
    public void initialize() {
        if (!alreadyStarted) {
            alreadyStarted = true;
            CountdownTimerUtil timer = new CountdownTimerUtil(timerLabel);
            timer.start();
        }
    }
}
