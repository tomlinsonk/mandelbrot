package mandelbrot;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import mandelbrot.brushes.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Mandelbrot v1.2
 *
 * Created by Kiran Tomlinson on 8/24/16.
 *
 * This is the main class of the application.
 */
public class Mandelbrot extends Application {

    // Constants
    final String[] BRUSH_LIST = {"Smooth", "Elegant", "Banded", "Binary"};
    final String DEFAULT_BRUSH = "Smooth";
    final String INSTRUCTIONS = "WASD to move\ndrag mouse to zoom\nescape to cancel zoom\nbackspace to go back";


    double SCREEN_WIDTH;
    double SCREEN_HEIGHT;

    Fractal fractal;

    Rectangle zoomRect;
    double zoomRectStartX, zoomRectStartY;

    boolean pickingJuliaPoint;
    boolean selectingZoom;



    /**
     * Method to start the application. Opens a window and creates all view objects
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        pickingJuliaPoint = false;
        selectingZoom = false;
        zoomRectStartX = 0;
        zoomRectStartY = 0;

        // Get screen dimensions, and set window size appropriately
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        SCREEN_WIDTH = screenBounds.getWidth();
        SCREEN_HEIGHT = screenBounds.getHeight();

        // Create all JavaFX objects
        BorderPane windowPane = new BorderPane();
        AnchorPane fractalPane = new AnchorPane();
        ImageView imageView = new ImageView();
        VBox toolbar = new VBox();
        fractalPane.getChildren().add(imageView);

        // Create the scene
        Scene scene = new Scene(windowPane, SCREEN_WIDTH, SCREEN_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("resources/stylesheet.css").toExternalForm());

        windowPane.setCenter(fractalPane);
        windowPane.setRight(toolbar);

        // Set all stage attributes
        stage.setScene(scene);
        stage.setTitle("Mandelbrot");
        stage.setResizable(false);
        stage.show();

        // Create new fractal
        fractal = new Fractal(SCREEN_WIDTH * 7 / 8, SCREEN_HEIGHT);
        imageView.imageProperty().bind(fractal.imageProperty);

        // Create toolbar
        toolbar.setPrefWidth(SCREEN_WIDTH / 8);
        toolbar.setSpacing(SCREEN_HEIGHT / 20);
        toolbar.setAlignment(Pos.CENTER);

        // Create instructions label
        Label instructions = new Label(INSTRUCTIONS);

        // Create brush picker
        VBox brushPane  = new VBox();
        brushPane.setAlignment(Pos.CENTER);
        Label brushLabel = new Label("Brush");
        ComboBox<String> brushPicker = new ComboBox<>();
        brushPicker.getItems().addAll(BRUSH_LIST);
        brushPicker.setValue(DEFAULT_BRUSH);
        brushPane.getChildren().addAll(brushLabel, brushPicker);

        // Create progress indicator
        ProgressIndicator renderIndicator = new ProgressIndicator(0);
        renderIndicator.setPrefSize(SCREEN_WIDTH / 10, SCREEN_HEIGHT / 10);
        renderIndicator.setVisible(true);
        renderIndicator.progressProperty().bind(fractal.renderProgressProperty);

        // Create iteration slider
        VBox iterationPane = new VBox();
        iterationPane.setAlignment(Pos.CENTER);
        Slider iterationSlider = new Slider();
        iterationSlider.setBlockIncrement(0);
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
        colorSlider.setBlockIncrement(0);
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

        // Create info panel
        VBox infoPane = new VBox();
        infoPane.setAlignment(Pos.CENTER);
        Label zoomReadout = new Label();
        zoomReadout.textProperty().bind(Bindings.format("Zoom: %.2G", fractal.zoomProperty));
        Label coordReadout = new Label();
        Label seedReadout = new Label();
        seedReadout.setVisible(false);
        infoPane.getChildren().addAll(zoomReadout, coordReadout, seedReadout);

        // Create Julia button
        Button juliaButton = new Button("Generate Julia Set");

        // Setup zoom rectangle
        zoomRect = new Rectangle();
        zoomRect.setStroke(Color.WHITE);
        zoomRect.setFill(null);
        zoomRect.setVisible(false);
        zoomRect.toFront();
        fractalPane.getChildren().add(zoomRect);

        // Add all items to toolbar
        toolbar.getChildren().addAll(instructions, renderIndicator, infoPane, juliaButton, brushPane, colorPane, iterationPane, saveButton);

        // Create event handlers
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        brushPicker.valueProperty().addListener((observable, oldValue, newValue) -> updateBrush(newValue));
        iterationSlider.setOnMouseReleased(event -> fractal.setMaxIterations((int)iterationSlider.getValue()));
        colorSlider.setOnMouseReleased(event -> fractal.setColorOffset(colorSlider.getValue()));
        imageView.setOnMouseMoved(event -> coordReadout.setText(getMouseCoordinateString(event.getX(), event.getY())));
        fractalPane.setOnMousePressed(event -> createNewZoomRect(event.getX(), event.getY()));
        fractalPane.setOnMouseDragged(event -> resizeZoomRect(fractalPane, event.getX(), event.getY()));
        fractalPane.setOnMouseReleased(event -> zoomFromRect());

        saveButton.setOnAction(event -> {
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) saveToFile(imageView.getImage(), file);
        });

        juliaButton.setOnAction(event -> {
            if (fractal.isJulia) {
                juliaButton.setText("Generate Julia Set");
                fractal.disableJulia();
                seedReadout.setVisible(false);
            } else {
                juliaButton.setText("Click to pick a seed...");
                pickingJuliaPoint = true;
                imageView.setOnMouseClicked(click -> {
                    juliaButton.setText("Back to Mandelbrot");
                    imageView.setOnMouseClicked(nextClick -> {});
                    seedReadout.setText(fractal.enableJulia(click.getX(), click.getY()));
                    seedReadout.setVisible(true);
                    pickingJuliaPoint = false;
                });
            }
        });
    }

    /**
     * Turns an x, y coordinate on the imageview into a coordinate string that shows the complex point at those coordinates
     * @param x
     * @param y
     * @return
     */
    private String getMouseCoordinateString(double x, double y) {
        double re = fractal.getRealComponent(x);
        double im = fractal.getImaginaryComponent(y);

        return "Mouse: " + String.format("%.3f", re) + (im >= 0 ? " + " : " - ") + String.format("%.3f", Math.abs(im)) + "i";
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
            case BACK_SPACE:
                fractal.backToLastState();
                break;
            case ESCAPE:
                selectingZoom = false;
                zoomRect.setVisible(false);
                zoomRect.setWidth(0);
                zoomRect.setHeight(0);
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
     * Starts a new zoom rectangle based on click coordinates
     * @param clickX
     * @param clickY
     */
    private void createNewZoomRect(double clickX, double clickY) {
        if (!pickingJuliaPoint) {
            zoomRect.setX(clickX);
            zoomRect.setY(clickY);
            zoomRectStartX = clickX;
            zoomRectStartY = clickY;

            zoomRect.setVisible(true);
            selectingZoom = true;
        }
    }

    /**
     * Resize the current zoom rectangle based on new mouse coordinates
     * @param newX new x coord of mouse
     * @param newY new y coord of mouse
     */
    private void resizeZoomRect(AnchorPane fractalPane, double newX, double newY) {
        if (selectingZoom && fractalPane.isHover()) {
            double desiredWidth = newX - zoomRectStartX;
            double desiredHeight = newY - zoomRectStartY;
            double fractalRatio = fractal.width / fractal.height;
            double aspectRatio = Math.abs(desiredWidth / desiredHeight);

            // Do we need to negate width or height?
            int widthSign = desiredWidth < 0 ? -1 : 1;
            int heightSign = desiredHeight < 0 ? -1 : 1;

            // Resize rectangle with aspect ratio constraint
            if (aspectRatio <= fractalRatio) {
                zoomRect.setWidth(widthSign * desiredWidth);
                zoomRect.setHeight(widthSign * desiredWidth / fractalRatio);
            } else {
                zoomRect.setWidth(heightSign * desiredHeight * fractalRatio);
                zoomRect.setHeight(heightSign * desiredHeight);
            }

            // Do we need to translate or set translate to 0?
            int moveX = desiredWidth < 0 ? -1 : 0;
            int moveY = desiredHeight < 0 ? -1 : 0;

            // Translate if necessary (thanks, ternary!)
            zoomRect.setX(zoomRectStartX + moveX * zoomRect.getWidth());
            zoomRect.setY(zoomRectStartY + moveY * zoomRect.getHeight());
        }
    }


    /**
     * Zooms in based on the current zoom rectangle
     */
    private void zoomFromRect() {
        if (selectingZoom) {
            selectingZoom = false;

            if (zoomRect.getWidth() > 0 && zoomRect.getHeight() > 0) {
                fractal.zoomIn(zoomRect.getX(), zoomRect.getY(), zoomRect.getWidth(), zoomRect.getHeight());
            }

            zoomRect.setVisible(false);
            zoomRect.setWidth(0);
            zoomRect.setHeight(0);
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
