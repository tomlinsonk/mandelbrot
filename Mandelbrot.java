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
        scene.getStylesheets().add(getClass().getResource("resources/stylesheet.css").toExternalForm());

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
        toolbar.setSpacing(SCREEN_HEIGHT / 15);
        toolbar.setAlignment(Pos.CENTER);

        // Create instructions label
        Label instructions = new Label("WASD to move\n+- to zoom");

        // Create brush picker
        VBox brushPane  = new VBox();
        brushPane.setAlignment(Pos.CENTER);
        Label brushLabel = new Label("Brush");
        ComboBox<String> brushList = new ComboBox<>();
        brushList.getItems().addAll("Smooth", "Elegant", "Binary", "Banded");
        brushList.setValue("Smooth");
        brushPane.getChildren().addAll(brushLabel, brushList);

        // Create progress indicator
        ProgressIndicator renderIndicator = new ProgressIndicator(0);
        renderIndicator.setPrefSize(SCREEN_WIDTH / 10, SCREEN_HEIGHT / 10);
        renderIndicator.setVisible(true);

        // Create iteration slider
        VBox iterationPane = new VBox();
        iterationPane.setAlignment(Pos.CENTER);
        Slider iterationSlider = new Slider();
        iterationSlider.setMin(0);
        iterationSlider.setMax(2000);
        iterationSlider.setMajorTickUnit(500);
        iterationSlider.setMinorTickCount(5);
        iterationSlider.setShowTickMarks(true);
        iterationSlider.setShowTickLabels(true);
        iterationSlider.setMaxWidth(SCREEN_WIDTH / 9);
        iterationSlider.setValue(1000);
        Label iterationLabel = new Label();
        iterationLabel.textProperty().bind(Bindings.format("Max Iterations: %.0f", iterationSlider.valueProperty()));
        iterationPane.getChildren().addAll(iterationLabel, iterationSlider);

        // Create iteration slider
        VBox colorPane = new VBox();
        colorPane.setAlignment(Pos.CENTER);
        Slider colorSlider = new Slider();
        colorSlider.setMin(0);
        colorSlider.setMax(1);
        colorSlider.setMaxWidth(SCREEN_WIDTH / 9);
        colorSlider.setValue(0);
        Label colorLabel = new Label("Color");
        colorPane.getChildren().addAll(colorLabel, colorSlider);

        // Create save button
        Button saveButton = new Button("Save Image");
        Image saveIcon = new Image(getClass().getResourceAsStream("resources/save.png"), 20, 20, false, false);
        saveButton.setGraphic(new ImageView(saveIcon));
        FileChooser fileChooser = new FileChooser();

        // Create new fractal
        fractal = new Fractal(SCREEN_WIDTH * 7 / 8, SCREEN_HEIGHT, imageView, renderIndicator);

        // Create zoom indicator
        Label zoomIndicator = new Label();
        zoomIndicator.textProperty().bind(Bindings.format("Zoom: %.2G", fractal.zoomProperty));

        // Add all items to toolbar
        toolbar.getChildren().addAll(instructions, renderIndicator, zoomIndicator, brushPane, colorPane, iterationPane, saveButton);

        // Create event handlers
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        brushList.valueProperty().addListener((observable, oldValue, newValue) -> updateBrush(newValue));
        iterationSlider.setOnMouseReleased(event -> fractal.setMaxIterations((int)iterationSlider.getValue()));
        colorSlider.setOnMouseReleased(event -> fractal.setColorOffset(colorSlider.getValue()));
        saveButton.setOnAction(event -> {
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) saveToFile(imageView.getImage(), file);
        });
    }

    /**
     * Saves an image to a file
     * @param image the image to save
     * @param file the file to save to
     */
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


    /**
     * Sets a new brush to repaint the fractal with
     * @param brushName the name of the new brush
     */
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
