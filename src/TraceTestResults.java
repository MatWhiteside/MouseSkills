import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TraceTestResults {

    private static final Label TITLE = new Label("YOUR RESULTS");
    private static final Label TIME_ON_BALL_LABEL = new Label("Time on ball: ");
    private static final Label TOTAL_RUNTIME_LABEL = new Label("Total runtime: ");
    private static final Label BALL_SPEED_LABEL = new Label("Ball speed: ");
    private static final Label BALL_THICKNESS_LABEL = new Label("Ball thickness ");
    private static final Font TITLE_FONT = new Font("Verdana", 20);
    private static final Font LABEL_FONT = new Font("Verdana", 16);

    private Button back;

    public Scene createScene(String timeOnBall, String totalTime, String ballSpeed, String ballThickness, Stage stage) {
        VBox parent = new VBox();
        parent.setAlignment(Pos.CENTER);
        parent.setSpacing(10);
        parent.setPadding(new Insets(10));

        TIME_ON_BALL_LABEL.setText("Time on ball: " + timeOnBall + "s");
        TOTAL_RUNTIME_LABEL.setText("Total runtime: " + totalTime + "s");
        BALL_SPEED_LABEL.setText("Ball speed: " + ballSpeed);
        BALL_THICKNESS_LABEL.setText("Ball thickness: " + ballThickness);

        back = new Button("Try again");
        back.setPrefWidth(Double.MAX_VALUE);
        back.setOnAction(event -> stage.setScene(new TraceTest().createScene(stage)));
        back.setDefaultButton(true);

        TITLE.setFont(TITLE_FONT);
        TIME_ON_BALL_LABEL.setFont(LABEL_FONT);
        TOTAL_RUNTIME_LABEL.setFont(LABEL_FONT);
        BALL_SPEED_LABEL.setFont(LABEL_FONT);
        BALL_THICKNESS_LABEL.setFont(LABEL_FONT);
        back.setFont(LABEL_FONT);

        parent.getChildren().addAll(
                TITLE,
                TIME_ON_BALL_LABEL,
                TOTAL_RUNTIME_LABEL,
                BALL_SPEED_LABEL,
                BALL_THICKNESS_LABEL,
                back
        );

        return new Scene(parent, 800, 600);
    }
}
