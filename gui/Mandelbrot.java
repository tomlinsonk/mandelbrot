package mandelbrot.gui;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Screen;
import javafx.stage.Stage;
import mandelbrot.fractal.Fractal;
import mandelbrot.util.Coordinate;
import mandelbrot.util.GuiState;

/**
 * Mandelbrot v1.4
 *
 * Created by Kiran Tomlinson on 8/24/16.
 *
 * This is the main class of the application.
 */
public class Mandelbrot extends Application {

    public GuiState state;
    public Coordinate mouseCoords;
    public Coordinate juliaCoords;

    private Stage stage;
    private Fractal fractal;
    private int width, height;

    /**
     * Method to start the application. Opens a window and creates all view objects
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        state = new GuiState();
        mouseCoords = new Coordinate(0, 0);
        juliaCoords = new Coordinate(0, 0);

        // Get screen dimensions, and set window size appropriately
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        width = (int)screenBounds.getWidth();
        height = (int)screenBounds.getHeight();

        fractal = new Fractal(width * 7 / 8, height);
        MasterPane windowPane = new MasterPane(this);

        // Create the scene
        Scene scene = new Scene(windowPane, width, height);
        scene.getStylesheets().add(getClass().getResource("/mandelbrot/resources/stylesheet.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Mandelbrot");
        stage.setResizable(false);
        stage.show();

        // Create event handlers
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));


    }

    public Fractal getFractal() {
        return fractal;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Stage getStage() {
        return stage;
    }


    /**
     * Handles keyboard input to and adjusts the model and view accordingly
     * @param key the key that was pressed
     */
    private void handleKeyPress(KeyCode key) {

        switch (key) {
            case A:
                fractal.moveLeft();
                break;
            case D:
                fractal.moveRight();
                break;
            case S:
                fractal.moveDown();
                break;
            case W:
                fractal.moveUp();
                break;
            case MINUS:
                fractal.zoomOutFixed();
                break;
            case EQUALS:
                fractal.zoomInFixed();
                break;
            case BACK_SPACE:
                fractal.backToLastState();
                break;
        }
    }





    /**
     * Main method. This is the entry point of the application.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
