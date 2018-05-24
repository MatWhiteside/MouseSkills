import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Class that shows a red screen, followed by a green screen after a random amount of time. When
 * the green screen is shown the mouse is clicked and reaction time measured.
 * @author Matthew Whiteside
 */

public class ReactionGame {
    // Application Constants
    private static final Label CENTRE_LABEL = new Label();
    private static final Label RESET_LABEL = new Label("Clicked too early! Timer reset");   // Label shown when clicking on the red screen

    private static final Font LABEL_FONT = new Font("Verdana", 16);
    private static final ReactionGameStates START_STATE = ReactionGameStates.INTRO;

    // Application properties
    private int windowWidth = 800;
    private int windowHeight = 600;
    private ReactionGameStates state;
    private PauseTransition pause;

    public Scene createScene() {
        // Create a group, to hold objects
        Group root = new Group();

        // Creates a scene
        Scene s = new Scene(root, windowWidth, windowHeight);
        setSceneColor(s, Color.RED);

        // Create a VBox to hold multiple labels
        VBox labelHolder = new VBox();
        labelHolder.setPrefSize(windowWidth, windowHeight);
        labelHolder.getChildren().add(CENTRE_LABEL);
        labelHolder.getChildren().add(RESET_LABEL);
        labelHolder.setAlignment(Pos.CENTER);

        // Format reset label
        RESET_LABEL.setFont(LABEL_FONT);
        RESET_LABEL.setTextFill(Paint.valueOf(Color.WHITE.toString()));
        RESET_LABEL.setOpacity(0);

        // Format centre label
        CENTRE_LABEL.setText("Click to start");
        CENTRE_LABEL.setFont(LABEL_FONT);
        CENTRE_LABEL.setTextFill(Paint.valueOf(Color.WHITE.toString()));

        // Add label to the centre of the stack pane
        root.getChildren().add(labelHolder);

        // Setup a timer to time how long it takes the user to react
        SimpleTimer timer = new SimpleTimer();

        // Set the start state
        state = START_STATE;

        // Handle certain events when the screen is clicked
        s.setOnMouseClicked(event -> {
            System.out.println(state);
            switch (state) {
                case INTRO:     // Change the label and start the test
                    state = ReactionGameStates.RUNNING_RED;
                    CENTRE_LABEL.setText("Click when screen turns green...");
                    waitRandomTime(s, timer);
                    break;
                case RUNNING_RED:   // If the red screen is clicked, reset the wait duration (so you can't cheat!)
                    displayResetText();
                    pause.stop();
                    waitRandomTime(s, timer);
                    break;
                case RUNNING_GREEN: // If the background is green, record the click and move to results
                    state = ReactionGameStates.RESULTS;
                    timer.updateValue();
                    timer.stop();
                    setSceneColor(s, Color.BLACK);
                    CENTRE_LABEL.setText("Reaction time: " + (int) timer.getTotalTime() + "ms\nClick to go again!");
                    break;
                case RESULTS:       // Clear the results screen and start the test
                    state = ReactionGameStates.RUNNING_RED;
                    CENTRE_LABEL.setText("Click when screen turns green...");
                    setSceneColor(s, Color.RED);
                    waitRandomTime(s, timer);
                    break;
            }
        });

        return s;
    }

    /**
     * Waits a random amount of time between 1 - 6 seconds, changes the scene to green and starts the timer.
     * @param s Current scene
     * @param t {@link SimpleTimer} timing how long the user takes to react
     */
    private void waitRandomTime(Scene s, SimpleTimer t) {
        // Generate a time between 1 and 6s
        double time = (Math.random() * 5) + 1;

        // Pause for the amount of time generated
        pause = new PauseTransition(
                Duration.seconds(time)
        );
        // Change the scene colour, state and start the timer
        pause.setOnFinished(event -> {
            // Check the timer has actually finished and not been force stopped
            if (Math.round(pause.getCurrentTime().toMillis()) >= Math.round(pause.getDuration().toMillis())) {
                state = ReactionGameStates.RUNNING_GREEN;
                setSceneColor(s, Color.GREEN);
                t.reset();
                t.start();
            }
        });
        // Start the pause
        System.out.println("pause playing");
        pause.play();
    }

    /**
     * Sets the scene s a given {@link Color} c
     * @param s Current scene
     * @param c {@link Color} to change the scene background to
     */
    private void setSceneColor(Scene s, Color c) {
        s.setFill(Paint.valueOf(c.toString()));
    }

    /**
     * Displays the RESET_LABEL for 3 seconds, appears instantly then fades out.
     */
    private void displayResetText() {
        FadeTransition ft = new FadeTransition(Duration.millis(3000), RESET_LABEL);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
    }

}
