import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Random;

public class TraceTest {

    // Application constants
    private static final int MIN_DIRECT_TIME = 500000000; // 500 ms
    private static final int MAX_DIRECT_TIME = 2000000000;// 2000 ms
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int RUNTIME = 30000;
    private static final double MILLIS_TO_SECOND_DIVIDER = 1000.0;
    private final Label timeInBallLabel = new Label("Time in ball: 0.00");

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

    public Scene createScene() {
        // Create a group, to hold objects
        Group root = new Group();

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
        root.getChildren().addAll(timeInBallLabel, targetBall);

        // Create main animation loop.
        AnimationTimer animationTimer = createGameLoop();

        // Start the overall time elapsed
        timeTaken.start();

        // Start the animation loop.
        animationTimer.start();

        return s;
    }

    private AnimationTimer createGameLoop() {
        // Note: target and therefore max fps = 60
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Check we're still under the set number of seconds
                timeTaken.updateValue();
                if(timeTaken.getTotalTime() >= RUNTIME) this.stop();

                // If the mouse is in the circle, update the time in ball label.
                if(mouseInCircle) {
                    timeOnBall.updateValue();
                    timeInBallLabel.setText("Time in ball: " + String.format(
                            "%.2f", timeOnBall.getTotalTime() / MILLIS_TO_SECOND_DIVIDER
                    ));
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

    private int genRandTime(int min, int max) {
        // Generates an integer between min (inclusive) and max (exclusive)
        return new Random().nextInt(max - min) + min;
    }

    private void changeDirection() {
        // Randomly select if ball is going up, down, left, right
        Random r = new Random();
        right = r.nextBoolean();
        down = r.nextBoolean();
    }

    private void moveBall() {
        // Move the ball on the X axis
        if (right) ballX += ballSpeed;
        else ballX -= ballSpeed;

        // Move the ball on the right axis
        if (down) ballY += ballSpeed;
        else ballY -= ballSpeed;
    }

    private void stopBallOOB() {
        // Size of the ball
        double ballSize = targetBall.getLayoutBounds().getWidth();

        // If the ball has hit a horizontal wall
        if (ballX + ballSize / 2 >= WINDOW_WIDTH || ballX - ballSize / 2 <= 0)
            right = !right;

        // If the ball has hit a vertical wall
        if (ballY + ballSize / 2 >= WINDOW_HEIGHT || ballY - ballSize / 2 <= 0)
            down = !down;
    }
}
