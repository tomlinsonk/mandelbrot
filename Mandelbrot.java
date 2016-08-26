package mandelbrot;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mandelbrot.brushes.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
        ComboBox<String> brushList = new ComboBox<String>();
        brushList.getItems().addAll("Elegant", "Binary", "Smooth", "Rainbow", "Random");
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

        // Create save button
        Button saveButton = new Button("Save Image");
        final FileChooser fileChooser = new FileChooser();
        saveButton.setOnAction(event -> {
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) saveToFile(imageView.getImage(), file);
        });

        // Create new fractal
        fractal = new Fractal(SCREEN_WIDTH * 7 / 8, SCREEN_HEIGHT, imageView, indicator);

        toolbar.getChildren().addAll(indicator, brushList, sliderPane, saveButton);

        // Create event handlers
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        brushList.valueProperty().addListener((observable, oldValue, newValue) -> updateBrush(newValue));
        slider.setOnMouseReleased(event -> fractal.setMaxIterations((int)slider.getValue()));
    }

    private void saveToFile(Image image, File file) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);

        try {
            ImageIO.write(bImage, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            case "Rainbow":
                fractal.setBrush(new RainbowBrush(fractal.maxIterations));
                break;
            case "Random":
                fractal.setBrush(new RandomBrush(fractal.maxIterations));
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
