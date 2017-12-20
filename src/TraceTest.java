import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Random;

public class TraceTest {

    private static final int MIN_DIRECT_TIME = 500000000; // 500 ms
    private static final int MAX_DIRECT_TIME = 2000000000;// 2000 ms
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private Label label = new Label("Time in ball: ");
    private int ballXSpeed, ballYSpeed = 3;
    private int ballX, ballY = -1;
    private double circleSize = -1;
    private boolean mouseInCircle = false;

    private long time = -1;

    public Scene createScene() {
        // Create a group, to hold objects
        Group root = new Group();

        // Creates a scene
        Scene s = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        BallHoverTimer timer = new BallHoverTimer();

        // Creates a circle
        Circle circle = new Circle();
        circle.setCenterX(ballX);
        circle.setCenterY(ballY);
        circle.setRadius(40);
        circleSize = circle.getLayoutBounds().getWidth();
        circle.setFill(Color.RED);

        ballX = (int) ((WINDOW_WIDTH / 2) - (circleSize / 2));
        ballY = (int) ((WINDOW_HEIGHT / 2) - (circleSize / 2));

        circle.setOnMouseEntered(event -> {
            timer.start();
            circle.setFill(Color.GREEN);
            mouseInCircle = true;
        });
        circle.setOnMouseExited(event -> {
            circle.setFill(Color.RED);
            timer.stop();
            mouseInCircle = false;
        });
        root.getChildren().addAll(label, circle);


        // Main animation loop.
        // Note: max 60fps
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(mouseInCircle) {
                    timer.updateValue();
                    label.setText("Time in ball: " + String.format("%.2f", timer.getTotalTime() / 1000.0));
                }
                if (now >= time) {
                    // 500ms and 2000ms
                    time = now + genRandTime(MIN_DIRECT_TIME, MAX_DIRECT_TIME);
                    changeDirection();
                }

                // Move the ball right by 1px
                moveBall(now);

                // Render the new ball in
                circle.setCenterX(ballX);
                circle.setCenterY(ballY);
            }
        };

        // Start the animation loop.
        animationTimer.start();

        return s;
    }

    private int genRandTime(int min, int max) {
        // Generates an integer between min (inclusive) and max (exclusive)
        return new Random().nextInt(max-min) + min;
    }

    private void changeDirection() {
        Random r = new Random();
        // Use these to change speed too
        int xChange = r.nextInt(10);
        int yChange = r.nextInt(10);
        ballXSpeed = xChange;
        ballYSpeed = yChange;
    }

    private void moveBall(long now) {
        stopBallOOB(now);
        ballX += ballXSpeed;
        ballY += ballYSpeed;
    }

    private void stopBallOOB(long now) {
        boolean speedsChanged = false;
        if (ballX + circleSize / 2 >= 800) {
            ballXSpeed = -Math.abs(ballXSpeed);
            speedsChanged = true;
        } else if (ballX - circleSize / 2 <= 0) {
            ballXSpeed = Math.abs(ballXSpeed);
            speedsChanged = true;
        }
        if (ballY + circleSize / 2 >= 600) {
            ballYSpeed = -Math.abs(ballYSpeed);
            speedsChanged = true;
        } else if (ballY - circleSize / 2 <= 0) {
            ballYSpeed = Math.abs(ballYSpeed);
            speedsChanged = true;
        }

        if (speedsChanged) time = now + genRandTime(MIN_DIRECT_TIME, MAX_DIRECT_TIME);
    }
}
