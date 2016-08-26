package mandelbrot;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Created by kiran on 8/24/16.
 *
 * This is the main class of the application. Acts as the view in MVC
 * and draws the set.
 */
public class Mandelbrot extends Application {

    // TODO: Smart screen size
    final int SCREEN_WIDTH = 1920;
    final int SCREEN_HEIGHT = 1105;


    /**
     * Method to start the application. Opens a window and creates all view objects
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Create all JavaFX objects
        BorderPane pane = new BorderPane();
        ImageView imageView = new ImageView();
        VBox toolbar = new VBox();

        // Create the scene
        Scene scene = new Scene(pane, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Add the canvas to the scene
        pane.setCenter(imageView);
        pane.setRight(toolbar);

        // Set all stage attributes
        stage.setScene(scene);
        stage.setTitle("Mandelbrot");
        stage.setResizable(false);
        stage.show();

        // Create new fractal and display it
        Fractal fractal = new Fractal(SCREEN_WIDTH * 4 / 5, SCREEN_HEIGHT);
        imageView.setImage(fractal.getImage());

        // Create toolbar
        toolbar.setPrefWidth(SCREEN_WIDTH / 5);

        // Create new controller and add event handlers
        Controller controller = new Controller(fractal, imageView);
        scene.setOnKeyPressed(event -> controller.handleKeyPress(event.getCode()));
    }


    /**
     * Main method. This is the entry point of the application.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
