import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Menu {

    /**
     * Creates the menu scene.
     * @param stage Stage the program is using.
     * @return Scene containing the menu GUI.
     */
    public Scene createScene(Stage stage) {
        // Create the parent layout and the scene
        VBox root = new VBox();

        // Create a title
        Label title = new Label("Menu");
        title.setFont(new Font("Verdana", 20));

        // Create a button for the reaction test
        Button reactionTestButton = new Button("Reaction test");
        reactionTestButton.setPrefSize(150, 40);
        reactionTestButton.setOnAction(event -> {
            stage.setScene(new ReactionGame().createScene());
        });

        // Create a button for the trace test
        Button traceTestButton = new Button("Trace test");
        traceTestButton.setPrefSize(150, 40);
        traceTestButton.setOnAction(event -> {
            stage.setScene(new TraceGame().createScene(stage));
        });

        // Center everything in the VBox, set the spacing between elements and add all the elements
        // to the layout.
        root.alignmentProperty().setValue(Pos.CENTER);
        root.setSpacing(10);
        root.getChildren().addAll(title, reactionTestButton, traceTestButton);

        return new Scene(root, 300, 300);
    }
}