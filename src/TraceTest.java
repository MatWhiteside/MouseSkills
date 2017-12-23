import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Class that models a ball somewhat randomly moving around a window, the aim is to hover the mouse
 * over the ball for as long as possible in the given time.
 * @author Matthew Whiteside
 */
public class TraceTest {
    // Application constants
    private static final int MIN_DIRECT_TIME = 500000000; // 500 ms
    private static final int MAX_DIRECT_TIME = 2000000000;// 2000 ms
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int BALL_AREA_LEFT_BOUNDARY = WINDOW_WIDTH/4;
    private static final Font labelFont = new Font("Verdana", 16);
    private static final double MILLIS_TO_SECOND_DIVIDER = 1000.0;
    private static final double RGB_CHANGE_PERIOD = 0.25;
    private static final Map<Color, Color> NEXT_COLOR_MAP = new HashMap<>() {{
        put(Color.WHITE, Color.RED);
        put(Color.RED, Color.GREEN);
        put(Color.GREEN, Color.BLUE);
        put(Color.BLUE, Color.RED);
    }};

    // Settings bar constants
    private static final Label BALL_SPEED_LABEL = new Label("Ball speed");
    private static final Label BALL_THICKNESS_LABEL = new Label("Ball thickness");
    private static final Label RUNTIME_LABEL = new Label("Runtime");
    private static final Label ON_BALL_COLOUR_LABEL = new Label("On ball colour");
    private static final Label OFF_BALL_COLOUR_LABEL = new Label("Off ball colour");
    private static final Label RGB_BALL_LABEL = new Label("RGB Ball: ");
    private static final Insets SETTINGS_LABEL_INSETS = new Insets(20, 0, 0, 0);
    private static final Insets SETTINGS_BUTTON_INSETS = new Insets(30, 0, 0, 0);
    private static final Insets SETTINGS_PADDING = new Insets(0, 10, 0, 10);

    // Application properties
    private final Label timeInBallLabel = new Label("Time in ball: 0.00");
    private final Label timeLeftLabel = new Label("Time left: ");
    private boolean mouseInCircle = false;
    private long waitTillTime = -1;
    private BallHoverTimer timeTaken;
    private BallHoverTimer timeOnBall;
    private int runtime = 30000;

    // Settings bar properties
    private ColorPicker offBallColourPicker;
    private ColorPicker onBallColourPicker;
    private Slider ballSpeedSlider;
    private Slider ballThicknessSlider;
    private TextField runtimeTextField;
    private Button applyButton;
    private CheckBox RGBBall;

    // Ball properties
    private int ballSpeed = 3;
    private Circle targetBall;
    private int ballX, ballY;
    private boolean right, down;
    private int circleRadius = 40;
    private Color circleColour = Color.RED;
    private Color circleActivationColour = Color.GREEN;

    /**
     * Creates the main scene used for the class.
     * @return {@link Scene} containing the ball trace application.
     */
    public Scene createScene() {
        // Create a group, to hold objects
        Group root = new Group();

        // VBox holds the timer labels
        VBox timers = createSettingsGUI();

        timeInBallLabel.setFont(labelFont);
        timeLeftLabel.setFont(labelFont);

        // Creates a scene
        Scene s = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        timeOnBall = new BallHoverTimer();
        timeTaken = new BallHoverTimer();

        // Creates a targetBall
        targetBall = createBall();

        // Start the ball in the centre of the window
        ballX = (int) ((WINDOW_WIDTH / 2) - (targetBall.getLayoutBounds().getWidth() / 2));
        ballY = (int) ((WINDOW_HEIGHT / 2) - (targetBall.getLayoutBounds().getWidth() / 2));

        // Add the target ball and time labels to the scene
        root.getChildren().addAll(targetBall, timers);

        // Create main animation loop.
        AnimationTimer animationTimer = createGameLoop();

        // Start the overall time elapsed
        timeTaken.start();

        // Start the animation loop.
        animationTimer.start();

        return s;
    }

    /*
    Creates a VBox containing the whole settings side bar.
    This side bar contains:
     - Time mouse was in ball
     - Time left
     - Colour picker for when mouse isn't on the ball
     - Colour picker fo when mouse is on the ball
     - Slider for ball speed
     - Slider for ball thickness
     - Text box for runtime
     */
    private VBox createSettingsGUI() {
        VBox parent = new VBox();

        // Create colour picker and set its value to the current one
        offBallColourPicker = new ColorPicker();
        offBallColourPicker.setValue(circleColour);

        // Create colour picker and set its value to the current one
        onBallColourPicker = new ColorPicker();
        onBallColourPicker.setValue(circleActivationColour);

        // Create ball speed slider and set its value to the current one
        ballSpeedSlider = new Slider();
        ballSpeedSlider.setMin(0);
        ballSpeedSlider.setMax(10);
        ballSpeedSlider.setShowTickMarks(true);
        ballSpeedSlider.setShowTickLabels(true);
        ballSpeedSlider.setValue(ballSpeed);

        // Create ball thickness slider and set its value to the current one
        ballThicknessSlider = new Slider();
        ballThicknessSlider.setMin(0);
        ballThicknessSlider.setMax(100);
        ballThicknessSlider.setShowTickMarks(true);
        ballThicknessSlider.setShowTickLabels(true);
        ballThicknessSlider.setValue(circleRadius);

        // Create the textfield that takes the runtime input
        // Note: the text property means that only numbers can be entered into the text field
        runtimeTextField = new TextField();
        runtimeTextField.setText(String.valueOf((int) (runtime / MILLIS_TO_SECOND_DIVIDER)));
        runtimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")) {
                runtimeTextField.setText(oldValue);
            }
        });

        // Create apply button that updates variables about the scene and resets the scene.
        applyButton = new Button("Apply & reset");
        applyButton.setPrefWidth(Double.MAX_VALUE);
        applyButton.setOnAction(event -> {
            circleColour = offBallColourPicker.getValue();
            circleActivationColour = onBallColourPicker.getValue();
            ballSpeed = (int) ballSpeedSlider.getValue();
            circleRadius = (int) ballThicknessSlider.getValue();
            runtime = (int) (Integer.valueOf(runtimeTextField.getText()) * MILLIS_TO_SECOND_DIVIDER);
            reset();
        });

        // Changing the fonts of the labels and the buttons
        OFF_BALL_COLOUR_LABEL.setFont(labelFont);
        ON_BALL_COLOUR_LABEL.setFont(labelFont);
        BALL_SPEED_LABEL.setFont(labelFont);
        BALL_THICKNESS_LABEL.setFont(labelFont);
        RUNTIME_LABEL.setFont(labelFont);
        applyButton.setFont(labelFont);

        // Adding 20px padding above all the labels and 30px above the button
        VBox.setMargin(OFF_BALL_COLOUR_LABEL, SETTINGS_LABEL_INSETS);
        VBox.setMargin(ON_BALL_COLOUR_LABEL, SETTINGS_LABEL_INSETS);
        VBox.setMargin(BALL_SPEED_LABEL, SETTINGS_LABEL_INSETS);
        VBox.setMargin(BALL_THICKNESS_LABEL, SETTINGS_LABEL_INSETS);
        VBox.setMargin(RUNTIME_LABEL, SETTINGS_LABEL_INSETS);
        VBox.setMargin(applyButton, SETTINGS_BUTTON_INSETS);

        // Setting styles for the VBox and adding all the elements to it
        parent.setAlignment(Pos.CENTER_LEFT);
        parent.setPadding(SETTINGS_PADDING);
        parent.setStyle("-fx-background-color: mintcream");
        parent.setPrefSize(BALL_AREA_LEFT_BOUNDARY, WINDOW_HEIGHT);
        parent.getChildren().addAll(
                timeInBallLabel, timeLeftLabel,
                OFF_BALL_COLOUR_LABEL, offBallColourPicker,
                ON_BALL_COLOUR_LABEL, onBallColourPicker,
                BALL_SPEED_LABEL, ballSpeedSlider,
                BALL_THICKNESS_LABEL, ballThicknessSlider,
                RUNTIME_LABEL, runtimeTextField,
                applyButton
        );

        return parent;
    }

    /*
    Resets all current progress, and applies the settings given in the settings panel.
     */
    private void reset() {
        // Change some target ball properties
        targetBall.setFill(offBallColourPicker.getValue());
        targetBall.setRadius(ballThicknessSlider.getValue());

        // Reset both the timers
        timeTaken.stop();
        timeTaken.reset();
        timeOnBall.stop();
        timeOnBall.reset();

        // Start the timer overall runtime timer, set the text to 0 for the other one.
        timeTaken.start();
        timeInBallLabel.setText("Time in ball: 0.00");
    }

    /*
    Given a circle c makes the ball change in the sequence red-green-blue every RGB_CHANGE_PERIOD ms.
     */
    private void makeRGB(Circle c) {
        makeRGB(c::getFill, c::setFill);
    }

    /*
    Given a scene s makes the scene change in the sequence red-green-blue every RGB_CHANGE_PERIOD ms.
     */
    private void makeRGB(Scene s) {
        makeRGB(s::getFill, s::setFill);
    }

    /*
    Changes the updater value every RGB_CHANGE_PERIOD ms to the relevant colour in the NEXT_COLOR_MAP.
     */
    private void makeRGB(Supplier<Paint> currentFill, Consumer<Paint> updater) {
        Timeline RGBTimeline = new Timeline(new KeyFrame(Duration.seconds(RGB_CHANGE_PERIOD),
                event -> updater.accept(NEXT_COLOR_MAP.get(currentFill.get()))));
        RGBTimeline.setCycleCount(Timeline.INDEFINITE);
        RGBTimeline.play();
    }

    /*
    Creates the main game loop.
     */
    private AnimationTimer createGameLoop() {
        // Note: target and therefore max fps = 60
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Check we're still under the set number of seconds
                timeTaken.updateValue();
                if(timeTaken.getTotalTime() >= runtime)
                    this.stop();
                else
                    timeLeftLabel.setText("Time left: " + millisToSeconds(runtime - timeTaken.getTotalTime()));

                // If the mouse is in the circle, update the time in ball label.
                if(mouseInCircle) {
                    timeOnBall.updateValue();
                    timeInBallLabel.setText("Time in ball: " + millisToSeconds(timeOnBall.getTotalTime()));
                }

                // If the current time has surpassed the wait till time, change the direction of the ball.
                if (now >= waitTillTime) {
                    waitTillTime = now + genRandTime(MIN_DIRECT_TIME, MAX_DIRECT_TIME);
                    changeDirection();
                }

                // If the ball is going out of bounds, stop it!
                stopBallOOB();

                // Change the balls coordinates according to new values
                moveBall();

                // Render the new ball into the scene
                targetBall.setCenterX(ballX);
                targetBall.setCenterY(ballY);
            }
        };
    }

    /*
    Converts milliseconds into a formatted string to two decimal places.
     */
    private String millisToSeconds(double millis) {
        return String.format("%.2f", millis / MILLIS_TO_SECOND_DIVIDER);
    }

    /*
    Creates the ball object that is traced.
     */
    private Circle createBall() {
        // Create the ball, set position and style
        Circle ball = new Circle();
        ball.setCenterX(ballX);
        ball.setCenterY(ballY);
        ball.setRadius(circleRadius);
        ball.setFill(circleColour);

        // When the mouse enters the ball, start the timeOnBall timer and set the colour to green
        ball.setOnMouseEntered(event -> {
            timeOnBall.start();
            ball.setFill(circleActivationColour);
            mouseInCircle = true;
        });

        // When the mouse exits the ball, stop the timeOnBall timer and set the colour to red
        ball.setOnMouseExited(event -> {
            ball.setFill(circleColour);
            timeOnBall.stop();
            mouseInCircle = false;
        });
        return ball;
    }

    /*
    Generates a random time between min and max.
     */
    private int genRandTime(int min, int max) {
        // Generates an integer between min (inclusive) and max (exclusive)
        return new Random().nextInt(max - min) + min;
    }

    /*
    Change the direction of the ball by randomly generating booleans for right and down.
     */
    private void changeDirection() {
        // Randomly select if ball is going up, down, left, right
        Random r = new Random();
        right = r.nextBoolean();
        down = r.nextBoolean();
    }

    /*
    Moves the ball in the given direction according to booleans right and left, by ballSpeed px.
     */
    private void moveBall() {
        // Move the ball on the X axis
        if (right) ballX += ballSpeed;
        else ballX -= ballSpeed;

        // Move the ball on the right axis
        if (down) ballY += ballSpeed;
        else ballY -= ballSpeed;
    }

    /*
    Stops the ball going out of bounds. Detects if the ball is outside of the window, if is the direction
    is changed and the ball is made to travel in the new direction for at least MIN_DIRECT_TIME ms.
     */
    private void stopBallOOB() {
        // Size of the ball
        double ballSize = targetBall.getLayoutBounds().getWidth();

        // If the ball has hit a horizontal wall
        if (ballX + ballSize / 2 >= WINDOW_WIDTH || ballX - ballSize / 2 <= BALL_AREA_LEFT_BOUNDARY) {
            right = !right;
            waitTillTime += MIN_DIRECT_TIME;
        }

        // If the ball has hit a vertical wall
        if (ballY + ballSize / 2 >= WINDOW_HEIGHT || ballY - ballSize / 2 <= 0) {
            down = !down;
            waitTillTime += MIN_DIRECT_TIME;
        }
    }
}
