import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Class to display the results after completing a play through of {@link TraceTest}.
 */
public class TraceTestResults {

    // Constants
    private static final Label TITLE = new Label("YOUR RESULTS");
    private static final Label TIME_ON_BALL_LABEL = new Label("Time on ball: ");
    private static final Label TOTAL_RUNTIME_LABEL = new Label("Total runtime: ");
    private static final Label BALL_SPEED_LABEL = new Label("Ball speed: ");
    private static final Label BALL_THICKNESS_LABEL = new Label("Ball thickness ");
    private static final Font TITLE_FONT = new Font("Verdana", 20);
    private static final Font LABEL_FONT = new Font("Verdana", 16);
    private static final int WINDOW_WIDTH = 250;
    private static final int WINDOW_HEIGHT = 250;

    // Properties
    private Button back;

    /**
     * Creates a scene displaying time spent on ball, total runtime, ball speed and ball thickness.
     * @param timeOnBall Time spent over the ball.
     * @param totalTime Total runtime of the scene.
     * @param ballSpeed Speed the ball was set to.
     * @param ballThickness Thickness of the ball (radius)
     * @param stage Stage to change the scene of when finished here.
     * @return Scene displaying time spent on ball, total runtime, ball speed and ball thickness.
     */
    public Scene createScene(String timeOnBall, String totalTime, String ballSpeed, String ballThickness, Stage stage) {
        // Populate the labels with data
        TIME_ON_BALL_LABEL.setText("Time on ball: " + timeOnBall + "s");
        TOTAL_RUNTIME_LABEL.setText("Total runtime: " + totalTime + "s");
        BALL_SPEED_LABEL.setText("Ball speed: " + ballSpeed);
        BALL_THICKNESS_LABEL.setText("Ball thickness: " + ballThickness);

        // Set the font of all the labels
        TITLE.setFont(TITLE_FONT);
        TIME_ON_BALL_LABEL.setFont(LABEL_FONT);
        TOTAL_RUNTIME_LABEL.setFont(LABEL_FONT);
        BALL_SPEED_LABEL.setFont(LABEL_FONT);
        BALL_THICKNESS_LABEL.setFont(LABEL_FONT);

        // Create and style the back button
        back = new Button("Try again");
        back.setPrefWidth(Double.MAX_VALUE);
        back.setOnAction(event -> stage.setScene(new TraceTest().createScene(stage)));
        back.setDefaultButton(true);
        back.setFont(LABEL_FONT);

        // Create and style the parent layout
        VBox parent = new VBox();
        parent.setAlignment(Pos.CENTER);
        parent.setSpacing(10);
        parent.setPadding(new Insets(10));
        parent.getChildren().addAll(
                TITLE,
                TIME_ON_BALL_LABEL,
                TOTAL_RUNTIME_LABEL,
                BALL_SPEED_LABEL,
                BALL_THICKNESS_LABEL,
                back
        );

        // Return the created scene
        return new Scene(parent, WINDOW_WIDTH, WINDOW_HEIGHT);
    }
}
