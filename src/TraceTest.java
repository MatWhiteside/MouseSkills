import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Random;

public class TraceTest {

    private static int MIN_DIRECT_TIME = 500000000; // 500 ms
    private static int MAX_DIRECT_TIME = 2000000000;// 2000 ms
    private int ballXSpeed = 5;
    private int ballYSpeed = 5;
    private int ballX = 100;
    private int ballY = 100;
    private double circleSize = -1;

    private long time = -1;

    public Scene createScene() {
        // Create a group, to hold objects
        Group root = new Group();

        // Creates a scene
        Scene s = new Scene(root, 800, 600);

        BallHoverTimer timer = new BallHoverTimer();

        // Creates a cirlce
        Circle circle = new Circle();
        circle.setCenterX(ballX);
        circle.setCenterY(ballY);
        circle.setRadius(40);
        circleSize = circle.getLayoutBounds().getWidth();
        circle.setFill(Color.RED);

        circle.setOnMouseEntered(event -> {
            timer.start();
            circle.setFill(Color.GREEN);
            timer.updateValue();
        });
        circle.setOnMouseExited(event -> {
            circle.setFill(Color.RED);
            timer.stop();
        });
        root.getChildren().add(circle);


        // Main animation loop.
        // Note: max 60fps
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                System.out.println(String.format("%.2f", timer.getTotalTime() / 1000.0));
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
