import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(true);
        primaryStage.setTitle("Mouse skills");
        primaryStage.setScene(new Menu().createScene(primaryStage));
        primaryStage.show();
    }

    /**
     * Backup entry point for the application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
