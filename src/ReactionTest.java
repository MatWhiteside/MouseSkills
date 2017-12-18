import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class ReactionTest {

    private int ballX = 100;
    private int ballY = 100;

    public Scene createScene() {
        // Create a group, to hold objects
        Group root = new Group();

        // Creates a scene
        Scene s = new Scene(root, 800, 600);

        // Creates a cirlce
        Circle circle = new Circle();
        circle.setCenterX(ballX);
        circle.setCenterY(ballY);
        circle.setRadius(40);
        circle.setFill(Color.BLUE);
        root.getChildren().add(circle);

        // Main animation loop.
        // Note: max 60fps
        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Move the ball right by 1px
                ballX++;

                // Render the new ball in
                circle.setCenterX(ballX);
                circle.setCenterY(ballY);
            }
        };

        // Event listener for key presses
        s.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
            if (key.getCode() == KeyCode.DOWN) {
                ballY += 5;
            }
        });

        // Start the animation loop.
        animationTimer.start();

        return s;
    }

}
