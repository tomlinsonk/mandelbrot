package mandelbrot;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by kiran on 8/24/16.
 *
 * This is the main class of the application. Calculates what pixels are in the set
 * and draws the set.
 */
public class Mandelbrot extends Application {

    // TODO: Smart screen size
    final int SCREEN_WIDTH = 1920;
    final int SCREEN_HEIGHT = 1105;



    /**
     * Method to start the application. Opens a window
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        // Create all JavaFX objects
        BorderPane pane = new BorderPane();
        ImageView imageView = new ImageView();


        // Create the scene
        Scene scene = new Scene(pane, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Add the canvas to the scene
        pane.setCenter(imageView);


        // Set all stage attributes
        stage.setScene(scene);
        stage.setTitle("Mandelbrot");
        stage.setResizable(false);
        stage.show();

        // Create new fractal and display it
        Fractal fractal = new Fractal(SCREEN_WIDTH, SCREEN_HEIGHT);
        Image image = fractal.getImage();

        imageView.setImage(image);


    }


    public static void main(String[] args) {
        launch(args);
    }
}
