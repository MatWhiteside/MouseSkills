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
import javafx.stage.Stage;
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
    private static final Font LABEL_FONT = new Font("Verdana", 16);
    private static final double MILLIS_TO_SECOND_DIVIDER = 1000.0;
    private static final double RGB_CHANGE_PERIOD = 0.25;
    private static final double SCENE_RGB_DELAY = 0.25;
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
    private static final Label RGB_SCENE_LABEL = new Label("RGB Scene: ");
    private static final Insets SETTINGS_PADDING = new Insets(0, 10, 0, 10);
    // Formula = (windowHeight / X) * (windowHeight / Y) = top margin for settings elements
    private static final int SETTINGS_MARGIN_FORMULA_X = 100;
    private static final int SETTINGS_MARGIN_FORMULA_Y = 200;


    // Application properties
    private int windowWidth = 800;
    private int windowHeight = 650;
    private int ballAreaLeftBoundary = windowWidth /4;
    private final Label timeInBallLabel = new Label("Time in ball: 0.00");
    private final Label timeLeftLabel = new Label("Time left: ");
    private Scene scene;
    private boolean mouseInCircle = false;
    private long waitTillTime = -1;
    private SimpleTimer timeTaken;
    private SimpleTimer timeOnBall;
    private int runtime = 30000;
    private Timeline RGBBallTimeline = null;
    private Timeline RGBSceneTimeline = null;
    private boolean isBallRGB = false;

    // Settings bar properties
    private ColorPicker offBallColourPicker;
    private ColorPicker onBallColourPicker;
    private Slider ballSpeedSlider;
    private Slider ballThicknessSlider;
    private TextField runtimeTextField;
    private Button applyButton;
    private Button backButton;
    private CheckBox RGBBall;
    private CheckBox RGBScene;
    private Insets settingsInsets;

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
    public Scene createScene(Stage stage) {
        // Create a group, to hold objects
        Group root = new Group();

        stage.setMinHeight(650);
        stage.setMinWidth(800);

        // VBox holds the timer labels
        VBox settings = createSettingsGUI(stage);

        timeInBallLabel.setFont(LABEL_FONT);
        timeLeftLabel.setFont(LABEL_FONT);

        // Creates a scene
        scene = new Scene(root, windowWidth, windowHeight);

        timeOnBall = new SimpleTimer();
        timeTaken = new SimpleTimer();

        // Creates a targetBall
        targetBall = createBall();

        // Start the ball in the centre of the window
        ballX = (int) ((windowWidth / 2) - (targetBall.getLayoutBounds().getWidth() / 2));
        ballY = (int) ((windowHeight / 2) - (targetBall.getLayoutBounds().getWidth() / 2));

        // Add the target ball and time labels to the scene
        root.getChildren().addAll(targetBall, settings);


        // Resize the GUI when the window is resized
        scene.widthProperty().addListener((observable, oldValue, newValue) -> windowWidth = newValue.intValue());
        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
            windowHeight = newValue.intValue();
            updateSettingsGUI(settings, newValue);
            double ballSize = targetBall.getLayoutBounds().getWidth();

            if (ballY + ballSize / 2 >= windowHeight)
                ballY = (int)(windowHeight - (ballSize/2) - 20);

            if (ballY - ballSize / 2 <= 0)
                ballY = (int)((ballSize/2) + 20);

            if (ballX + ballSize / 2 >= windowWidth)
                ballX = (int)(windowWidth - (ballSize/2) - 20);

            if (ballX - ballSize / 2 <= ballAreaLeftBoundary)
                ballX = (int)(ballAreaLeftBoundary + (ballSize/2) + 20);
        });

        // Create main animation loop.
        AnimationTimer animationTimer = createGameLoop(stage);

        // Start the overall time elapsed
        timeTaken.start();

        // Start the animation loop.
        animationTimer.start();

        return scene;
    }

    private void updateSettingsGUI(VBox settings, Number newHeight) {
        settings.setPrefHeight(newHeight.intValue());
        int newTop = (windowHeight / SETTINGS_MARGIN_FORMULA_X) * (windowHeight / SETTINGS_MARGIN_FORMULA_Y);
        settingsInsets = new Insets(newTop, 0, 0, 0);

        VBox.setMargin(OFF_BALL_COLOUR_LABEL, settingsInsets);
        VBox.setMargin(ON_BALL_COLOUR_LABEL, settingsInsets);
        VBox.setMargin(BALL_SPEED_LABEL, settingsInsets);
        VBox.setMargin(BALL_THICKNESS_LABEL, settingsInsets);
        VBox.setMargin(RUNTIME_LABEL, settingsInsets);
        VBox.setMargin(RGB_BALL_LABEL, settingsInsets);
        VBox.setMargin(RGB_SCENE_LABEL, settingsInsets);
        VBox.setMargin(applyButton, settingsInsets);
        VBox.setMargin(backButton, settingsInsets);
    }

    /*
    Creates a VBox containing the whole settings side bar.
    This side bar contains:
     - Time mouse was in ball
     - Time left
     - Colour picker for when mouse isn't on the ball
     - Colour picker for when mouse is on the ball
     - Slider for ball speed
     - Slider for ball thickness
     - Text box for runtime
     */
    private VBox createSettingsGUI(Stage stage) {
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

        RGBBall = new CheckBox();
        RGBScene = new CheckBox();

        // Create apply button that updates variables about the scene and resets the scene.
        applyButton = new Button("Apply & reset");
        applyButton.setPrefWidth(Double.MAX_VALUE);
        applyButton.setOnAction(event -> {
            circleColour = offBallColourPicker.getValue();
            circleActivationColour = onBallColourPicker.getValue();
            ballSpeed = (int) ballSpeedSlider.getValue();
            circleRadius = (int) ballThicknessSlider.getValue();
            runtime = (int) (Integer.valueOf(runtimeTextField.getText()) * MILLIS_TO_SECOND_DIVIDER);

            stopRGB();

            if(RGBBall.isSelected()) {
                makeRGB(targetBall);
                isBallRGB = true;
            }
            if(RGBScene.isSelected()) {
                makeRGB(scene);
            }

            reset();
        });

        backButton = new Button("Back");
        backButton.setPrefWidth(Double.MAX_VALUE);
        backButton.setOnAction(event -> stage.setScene(new Menu().createScene(stage)));

        // Changing the fonts of the labels and the buttons
        OFF_BALL_COLOUR_LABEL.setFont(LABEL_FONT);
        ON_BALL_COLOUR_LABEL.setFont(LABEL_FONT);
        BALL_SPEED_LABEL.setFont(LABEL_FONT);
        BALL_THICKNESS_LABEL.setFont(LABEL_FONT);
        RUNTIME_LABEL.setFont(LABEL_FONT);
        RGB_BALL_LABEL.setFont(LABEL_FONT);
        RGB_SCENE_LABEL.setFont(LABEL_FONT);
        applyButton.setFont(LABEL_FONT);
        backButton.setFont(LABEL_FONT);

        int newTop = (windowHeight / SETTINGS_MARGIN_FORMULA_X) * (windowHeight / SETTINGS_MARGIN_FORMULA_Y);
        settingsInsets = new Insets(newTop, 0, 0, 0);

        // Adding 20px padding above all the labels and 30px above the button
        VBox.setMargin(OFF_BALL_COLOUR_LABEL, settingsInsets);
        VBox.setMargin(ON_BALL_COLOUR_LABEL, settingsInsets);
        VBox.setMargin(BALL_SPEED_LABEL, settingsInsets);
        VBox.setMargin(BALL_THICKNESS_LABEL, settingsInsets);
        VBox.setMargin(RUNTIME_LABEL, settingsInsets);
        VBox.setMargin(RGB_BALL_LABEL, settingsInsets);
        VBox.setMargin(RGB_SCENE_LABEL, settingsInsets);
        VBox.setMargin(applyButton, settingsInsets);
        VBox.setMargin(backButton, settingsInsets);

        // Setting styles for the VBox and adding all the elements to it
        parent.setAlignment(Pos.CENTER_LEFT);
        parent.setPadding(SETTINGS_PADDING);
        parent.setStyle("-fx-background-color: mintcream");
        parent.setPrefSize(ballAreaLeftBoundary, windowHeight);
        parent.getChildren().addAll(
                timeInBallLabel, timeLeftLabel,
                OFF_BALL_COLOUR_LABEL, offBallColourPicker,
                ON_BALL_COLOUR_LABEL, onBallColourPicker,
                BALL_SPEED_LABEL, ballSpeedSlider,
                BALL_THICKNESS_LABEL, ballThicknessSlider,
                RUNTIME_LABEL, runtimeTextField,
                RGB_BALL_LABEL, RGBBall,
                RGB_SCENE_LABEL, RGBScene,
                applyButton, backButton
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
        makeRGB(c::getFill, c::setFill, true);
    }

    /*
    Given a scene s makes the scene change in the sequence red-green-blue every RGB_CHANGE_PERIOD ms.
     */
    private void makeRGB(Scene s) {
        makeRGB(s::getFill, s::setFill, false);
    }

    /*
    Changes the updater value every RGB_CHANGE_PERIOD ms to the relevant colour in the NEXT_COLOR_MAP.
    Third param is true iff the ball is being made RGB, not the scene.
     */
    private void makeRGB(Supplier<Paint> currentFill, Consumer<Paint> updater, boolean ball) {
        if(ball) {
            RGBBallTimeline = new Timeline(new KeyFrame(Duration.seconds(RGB_CHANGE_PERIOD),
                    event -> updater.accept(NEXT_COLOR_MAP.get(currentFill.get()))));
            RGBBallTimeline.setCycleCount(Timeline.INDEFINITE);
            RGBBallTimeline.play();
        } else {
            RGBSceneTimeline = new Timeline(new KeyFrame(Duration.seconds(RGB_CHANGE_PERIOD),
                    event -> updater.accept(NEXT_COLOR_MAP.get(currentFill.get()))));
            RGBSceneTimeline.setCycleCount(Timeline.INDEFINITE);
            RGBSceneTimeline.setDelay(Duration.seconds(SCENE_RGB_DELAY));
            RGBSceneTimeline.play();
        }
    }

    /*
    Stops both RGB timelines and sets them to null.
     */
    private void stopRGB() {
        if(RGBBallTimeline != null) {
            RGBBallTimeline.stop();
            RGBBallTimeline = null;
            isBallRGB = false;
        }
        if(RGBSceneTimeline != null) {
            RGBSceneTimeline.stop();
            RGBSceneTimeline = null;
            scene.setFill(Color.WHITE);
        }
    }

    /*
    Creates the main game loop.
     */
    private AnimationTimer createGameLoop(Stage stage) {
        // Note: target and therefore max fps = 60
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Check we're still under the set number of seconds
                timeTaken.updateValue();
                if(timeTaken.getTotalTime() >= runtime) {
                    stage.setScene(new TraceTestResults().createScene(
                            millisToSeconds(timeOnBall.getTotalTime()),
                            millisToSeconds(runtime),
                            String.valueOf(ballSpeed),
                            String.valueOf(circleRadius),
                            stage
                    ));
                    this.stop();
                } else {
                    timeLeftLabel.setText("Time left: " + millisToSeconds(runtime - timeTaken.getTotalTime()));
                }


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
            if(!isBallRGB) ball.setFill(circleActivationColour);
            mouseInCircle = true;
        });

        // When the mouse exits the ball, stop the timeOnBall timer and set the colour to red
        ball.setOnMouseExited(event -> {
            if(!isBallRGB) ball.setFill(circleColour);
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
        if (ballX + ballSize / 2 >= windowWidth || ballX - ballSize / 2 <= ballAreaLeftBoundary) {
            right = !right;
            waitTillTime += MIN_DIRECT_TIME;
        }

        // If the ball has hit a vertical wall
        if (ballY + ballSize / 2 >= windowHeight || ballY - ballSize / 2 <= 0) {
            down = !down;
            waitTillTime += MIN_DIRECT_TIME;
        }
    }
}
