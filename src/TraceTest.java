import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.util.Random;

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
    private static final int RUNTIME = 30000;
    private static final Font labelFont = new Font("Verdana", 12);
    private static final double MILLIS_TO_SECOND_DIVIDER = 1000.0;
    private final Label timeInBallLabel = new Label("Time in ball: 0.00");
    private final Label timeLeftLabel = new Label("Time left: ");

    // Circle constants
    private static final int CIRCLE_RADIUS = 40;
    private static final Color CIRCLE_COLOUR = Color.RED;
    private static final Color CIRCLE_ACTIVATION_COLOUR = Color.GREEN;

    // Application properties
    private boolean mouseInCircle = false;
    private long waitTillTime = -1;
    private BallHoverTimer timeTaken;
    private BallHoverTimer timeOnBall;

    // Ball properties
    private int ballSpeed = 3;
    private Circle targetBall;
    private int ballX, ballY;
    private boolean right, down;

    /**
     * Creates the main scene used for the class.
     * @return {@link Scene} containing the ball trace application.
     */
    public Scene createScene() {
        // Create a group, to hold objects
        Group root = new Group();

        // VBox holds the timer labels
        VBox timers = new VBox();
        timers.getChildren().addAll(timeInBallLabel, timeLeftLabel);

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
    Creates the main game loop.
     */
    private AnimationTimer createGameLoop() {
        // Note: target and therefore max fps = 60
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Check we're still under the set number of seconds
                timeTaken.updateValue();
                if(timeTaken.getTotalTime() >= RUNTIME)
                    this.stop();
                else
                    timeLeftLabel.setText("Time left: " + millisToSeconds(RUNTIME - timeTaken.getTotalTime()));

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
        ball.setRadius(CIRCLE_RADIUS);
        ball.setFill(CIRCLE_COLOUR);

        // When the mouse enters the ball, start the timeOnBall timer and set the colour to green
        ball.setOnMouseEntered(event -> {
            timeOnBall.start();
            ball.setFill(CIRCLE_ACTIVATION_COLOUR);
            mouseInCircle = true;
        });

        // When the mouse exits the ball, stop the timeOnBall timer and set the colour to red
        ball.setOnMouseExited(event -> {
            ball.setFill(CIRCLE_COLOUR);
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
        if (ballX + ballSize / 2 >= WINDOW_WIDTH || ballX - ballSize / 2 <= 0) {
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
