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
    //Application Constants
    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 600;

    //Centre Circle Properties
    private final int CENTRE_X = WINDOW_WIDTH / 2;
    private final int CENTRE_Y = WINDOW_HEIGHT / 2;
    private Circle centreCircle;
    private Color circleStartColour = Color.RED;

    private int ballX = 100;
    private int ballY = 100;

    public Scene createScene() {
        // Create a group, to hold objects
        Group root = new Group();

        // Creates a scene
        Scene s = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        //Create centre circle
        centreCircle = new Circle();
        centreCircle.setCenterX(CENTRE_X);
        centreCircle.setCenterY(CENTRE_Y);
        centreCircle.setRadius(20);
        centreCircle.setFill(circleStartColour);
        root.getChildren().add(centreCircle);

        //Create ball timer
        BallHoverTimer ballTimer = new BallHoverTimer();

        ballTimer.start();

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                ballTimer.updateValue();

                //Set circle orange when user hovers over
                centreCircle.setOnMouseEntered(event -> {
                    //START A TIMER ON TOP/SIZE OF SCREEN
                    centreCircle.setFill(Color.ORANGERED);
                });

                centreCircle.setOnMouseExited(event -> {
                    if (centreCircle.getFill() == Color.ORANGERED){
                        centreCircle.setFill(circleStartColour);
                    }
                });

                //Set circle to green when user clicks
                centreCircle.setOnMouseClicked(event -> {
                    if (ballTimer.getTotalTime() >= 100){
                        centreCircle.setFill(Color.GREEN);
                    }
                });
                centreCircle.setCenterX(CENTRE_X);
                centreCircle.setCenterY(CENTRE_Y);
            }
        };

        // Main animation loop.
        // Note: max 60fps
        /*AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Move the ball right by 1px
                ballX++;

                // Render the new ball in
                circle.setCenterX(ballX);
                circle.setCenterY(ballY);
            }
        };
        */

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
