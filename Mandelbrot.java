package mandelbrot;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mandelbrot.brushes.*;

/**
 * Created by Kiran Tomlinson on 8/24/16.
 *
 * This is the main class of the application. Acts as the view in MVC
 * and draws the set.
 */
public class Mandelbrot extends Application {

    // TODO: Smart screen size
    final int SCREEN_WIDTH = 1920;
    final int SCREEN_HEIGHT = 1105;

    Fractal fractal;

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

        // Create toolbar
        toolbar.setPrefWidth(SCREEN_WIDTH / 8);
        toolbar.setSpacing(SCREEN_HEIGHT / 10);
        toolbar.setAlignment(Pos.CENTER);

        // Create brush picker
        ComboBox<String> brushList = new ComboBox<>();
        brushList.getItems().addAll("Elegant", "Smooth", "Binary", "Tropical", "Banded");
        brushList.setValue("Elegant");

        // Create progress indicator
        ProgressIndicator indicator = new ProgressIndicator(0);
        indicator.setPrefSize(SCREEN_WIDTH / 10, SCREEN_HEIGHT / 10);
        indicator.setVisible(true);

        // Create iteration slider
        VBox sliderPane = new VBox();
        sliderPane.setAlignment(Pos.CENTER);
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(2000);
        slider.setMajorTickUnit(500);
        slider.setMinorTickCount(5);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMaxWidth(SCREEN_WIDTH / 9);
        slider.setValue(1000);
        Label sliderLabel = new Label();
        sliderLabel.textProperty().bind(Bindings.format("Max Iterations: %.0f", slider.valueProperty()));
        sliderPane.getChildren().addAll(sliderLabel, slider);

        // Create new fractal
        fractal = new Fractal(SCREEN_WIDTH * 7 / 8, SCREEN_HEIGHT, imageView, indicator);

        toolbar.getChildren().addAll(indicator, brushList, sliderPane);

        // Create event handlers
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        brushList.valueProperty().addListener((observable, oldValue, newValue) -> updateBrush(newValue));
        slider.setOnMouseReleased(event -> fractal.setMaxIterations((int)slider.getValue()));
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
            case EQUALS:
                fractal.zoomIn();
                break;
            case MINUS:
                fractal.zoomOut();
                break;
            default:
                return;
        }
    }


    private void updateBrush(String brushName) {

        switch (brushName) {
            case "Binary":
                fractal.setBrush(new BinaryBrush(fractal.maxIterations));
                break;
            case "Elegant":
                fractal.setBrush(new ElegantBrush(fractal.maxIterations));
                break;
            case "Banded":
                fractal.setBrush(new BandedBrush(fractal.maxIterations));
                break;
            case "Tropical":
                fractal.setBrush(new TropicalBrush(fractal.maxIterations));
                break;
            case "Smooth":
                fractal.setBrush(new SmoothBrush(fractal.maxIterations));
                break;
            default:
                return;
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
