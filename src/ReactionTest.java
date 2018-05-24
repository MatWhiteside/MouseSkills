import javafx.animation.PauseTransition;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Class that shows a red screen, followed by a green screen after a random amount of time. When
 * the green screen is shown the mouse is clicked and reaction time measured.
 * @author Matthew Whiteside
 */

public class ReactionTest {
    // Application Constants
    private static final Label CENTRE_LABEL = new Label();
    private static final Font LABEL_FONT = new Font("Verdana", 16);

    // Application properties
    private int windowWidth = 800;
    private int windowHeight = 600;
    private State state = State.INTRO;

    public Scene createScene() {
        // Create a group, to hold objects
        Group root = new Group();

        // Creates a scene
        Scene s = new Scene(root, windowWidth, windowHeight);
        setSceneColor(s, Color.RED);

        // Create a stack pane and add it to the scene
        StackPane sp = new StackPane();
        sp.setPrefSize(windowWidth, windowHeight);

        // Format centre label
        CENTRE_LABEL.setText("Click to start");
        CENTRE_LABEL.setFont(LABEL_FONT);
        CENTRE_LABEL.setTextFill(Paint.valueOf(Color.WHITE.toString()));

        // Add label to the centre of the stack pane
        root.getChildren().add(sp);
        sp.getChildren().add(CENTRE_LABEL);

        // Setup a timer to time how long it takes the user to react
        SimpleTimer timer = new SimpleTimer();

        // Handle certain events when the screen is clicked
        s.setOnMouseClicked(event -> {
            System.out.println(state);
            switch (state) {
                case INTRO:     // Change the label and start the test
                    state = State.RUNNING_RED;
                    CENTRE_LABEL.setText("Click when screen turns green...");
                    waitRandomTime(s, timer);
                    break;
                case RUNNING_GREEN: // If the background is green, record the click and move to results
                    state = State.RESULTS;
                    timer.updateValue();
                    timer.stop();
                    setSceneColor(s, Color.BLACK);
                    CENTRE_LABEL.setText("Reaction time: " + (int) timer.getTotalTime() + "ms\nClick to go again!");
                    break;
                case RESULTS:       // Clear the results screen and start the test
                    state = State.RUNNING_RED;
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
        PauseTransition pause = new PauseTransition(
                Duration.seconds(time)
        );
        // Change the scene colour, state and start the timer
        pause.setOnFinished(event -> {
            state = State.RUNNING_GREEN;
            setSceneColor(s, Color.GREEN);
            t.reset();
            t.start();
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
     * Program can be in three states:
     *  - Intro: the user is on the screen displaying "click to start"
     *  - RUNNING_RED: the user is waiting for the screen to turn green
     *  - RUNNING_GREEN: the user should click the screen now, it has gone green
     *  - RESULTS: the results page is currently showing
     */
    private enum State {
        INTRO, RUNNING_RED, RUNNING_GREEN, RESULTS
    }

}
