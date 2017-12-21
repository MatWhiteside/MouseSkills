import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Menu extends Application {

    /**
     * Entry point for the application.
     * @param primaryStage Main stage.
     * @throws Exception JavaFX exception.
     */
    @Override
    public void start(Stage primaryStage) throws Exception  {
        // Create the parent layout and the scene
        VBox root = new VBox();
        Scene s = new Scene(root, 300, 300);

        // Create a title
        Label title = new Label("Menu");
        title.setFont(new Font("Verdana", 20));

        // Create a button for the reaction test
        Button reactionTestButton = new Button("Reaction test");
        reactionTestButton.setPrefSize(150, 40);
        reactionTestButton.setOnAction(event -> {
            primaryStage.setScene(new ReactionTest().createScene());
        });

        // Create a button for the trace test
        Button traceTestButton = new Button("Trace test");
        traceTestButton.setPrefSize(150, 40);
        traceTestButton.setOnAction(event -> {
            primaryStage.setScene(new TraceTest().createScene());
        });

        // Center everything in the VBox, set the spacing between elements and add all the elements
        // to the layout.
        root.alignmentProperty().setValue(Pos.CENTER);
        root.setSpacing(10);
        root.getChildren().addAll(title, reactionTestButton, traceTestButton);

        // Set the primary stage to the scene we've made
        primaryStage.setTitle("Mouse skills");
        primaryStage.setScene(s);
        primaryStage.show();
    }

    /**
     * Backup entry point for the application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        System.out.println("One Printy boi");
        launch(args);
    }
}