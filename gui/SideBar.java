package mandelbrot.gui;

import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import mandelbrot.brush.BandedBrush;
import mandelbrot.brush.BinaryBrush;
import mandelbrot.brush.ElegantBrush;
import mandelbrot.brush.SmoothBrush;
import mandelbrot.fractal.Fractal;
import mandelbrot.util.Coordinate;
import mandelbrot.util.MandelbrotState;
import mandelbrot.util.State;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by kiran on 7/21/17.
 */
public class SideBar extends VBox implements Observer {

    // Constants
    private final String[] BRUSH_LIST = {"Smooth", "Elegant", "Banded", "Binary"};
    private final String DEFAULT_BRUSH = "Smooth";
    private final String INSTRUCTIONS = "WASD to move\ndrag mouse to zoom\nescape to cancel zoom\n+/- also zooms\nbackspace to go back";

    private Mandelbrot mandelbrot;
    private Fractal fractal;
    private Fractal juliaPreview;

    private Label coordReadout;
    private Label seedReadout;
    private Label instructions;
    private Slider colorSlider;
    private Button juliaButton;
    private Button animationButton;
    private ImageView juliaView;


    SideBar(Mandelbrot mandelbrot) {
        super();

        this.mandelbrot = mandelbrot;
        mandelbrot.mouseCoords.addObserver(this);
        mandelbrot.state.addObserver(this);

        fractal = mandelbrot.getFractal();
        int width = mandelbrot.getWidth();
        int height = mandelbrot.getHeight();

        this.setPrefWidth(width / 8);
        this.setSpacing(height / 20);
        this.setAlignment(Pos.CENTER);

        // Create Julia preview
        juliaView = new ImageView();
        juliaPreview = new Fractal(width * 1 / 8, height / 5, 0, 0, true, 80);
        juliaPreview.setMaxIterations(100);
        juliaView.imageProperty().bind(juliaPreview.imageProperty);

        // Create instructions label
        instructions = new Label(INSTRUCTIONS);

        // Create brush picker
        VBox brushPane  = new VBox();
        brushPane.setAlignment(Pos.CENTER);
        Label brushLabel = new Label("Brush");
        ComboBox<String> brushPicker = new ComboBox<>();
        brushPicker.getItems().addAll(BRUSH_LIST);
        brushPicker.setValue(DEFAULT_BRUSH);
        brushPane.getChildren().addAll(brushLabel, brushPicker);

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
        iterationSlider.setMaxWidth(width / 9);
        iterationSlider.setValue(1000);
        Label iterationLabel = new Label();
        iterationLabel.textProperty().bind(Bindings.format("Max Iterations: %.0f", iterationSlider.valueProperty()));
        iterationPane.getChildren().addAll(iterationLabel, iterationSlider);

        // Create color slider
        VBox colorPane = new VBox();
        colorPane.setAlignment(Pos.CENTER);
        colorSlider = new Slider();
        colorSlider.setBlockIncrement(0);
        colorSlider.setMin(0);
        colorSlider.setMax(1);
        colorSlider.setMaxWidth(width / 9);
        colorSlider.setValue(0);
        Label colorLabel = new Label("Color");
        colorPane.getChildren().addAll(colorLabel, colorSlider);

        // Create save button
        Button saveButton = new Button("Save Image");
        Image saveIcon = new Image(getClass().getResourceAsStream("/mandelbrot/resources/save.png"), 20, 20, false, false);
        saveButton.setGraphic(new ImageView(saveIcon));


        // Create info panel
        VBox infoPane = new VBox();
        infoPane.setAlignment(Pos.CENTER);
        Label renderReadout = new Label("");
        renderReadout.setVisible(true);
        renderReadout.textProperty().bind(fractal.renderingProperty);
        Label zoomReadout = new Label();
        zoomReadout.textProperty().bind(Bindings.format("Zoom: %.2G", fractal.zoomProperty));
        coordReadout = new Label();
        seedReadout = new Label();
        seedReadout.setVisible(false);
        infoPane.getChildren().addAll(renderReadout, zoomReadout, coordReadout, seedReadout);

        // Create Julia button
        juliaButton = new Button("Generate Julia Set");

        // Create animation button
        animationButton = new Button("Create Julia Animation");

        this.getChildren().addAll(instructions, infoPane, juliaButton, animationButton, brushPane, colorPane, iterationPane, saveButton);

        brushPicker.valueProperty().addListener((observable, oldValue, newValue) -> updateBrush(newValue));
        iterationSlider.setOnMouseReleased(event -> fractal.setMaxIterations((int)iterationSlider.getValue()));
        colorSlider.setOnMouseReleased(event -> fractal.setColorOffset((float)colorSlider.getValue()));
        colorSlider.setOnMouseReleased(event -> updateColorOffset());
        saveButton.setOnAction(event -> saveFractal());
        juliaButton.setOnAction(event -> juliaButtonClicked());

        animationButton.setOnAction(event -> animationButtonClicked());
    }

    private void animationButtonClicked() {
        if (mandelbrot.state.get() != MandelbrotState.MANDELBROT) return;
        mandelbrot.state.set(MandelbrotState.CREATING_PATH);
    }



    private void updateColorOffset() {
        fractal.setColorOffset((float)colorSlider.getValue());
        juliaPreview.setColorOffset((float)colorSlider.getValue());
    }

    /**
     * Sets a new brush to repaint the fractal with
     * @param brushName the name of the new brush
     */
    private void updateBrush(String brushName) {
        int maxIterations = fractal.getMaxIterations();
        switch (brushName) {
            case "Binary":
                fractal.setBrush(new BinaryBrush(maxIterations));
                juliaPreview.setBrush(new BinaryBrush(maxIterations));
                break;
            case "Elegant":
                fractal.setBrush(new ElegantBrush(maxIterations));
                juliaPreview.setBrush(new ElegantBrush(maxIterations));
                break;
            case "Banded":
                fractal.setBrush(new BandedBrush(maxIterations));
                juliaPreview.setBrush(new BandedBrush(maxIterations));
                break;
            case "Smooth":
                fractal.setBrush(new SmoothBrush(maxIterations));
                juliaPreview.setBrush(new SmoothBrush(maxIterations));
                break;
        }
    }

    private void juliaButtonClicked() {
        if (mandelbrot.state.get() == MandelbrotState.JULIA) {
            juliaButton.setText("Generate Julia Set");
            fractal.disableJulia();
            seedReadout.setVisible(false);
            animationButton.setVisible(true);
            mandelbrot.state.set(MandelbrotState.MANDELBROT);
        } else if (mandelbrot.state.get() == MandelbrotState.PICKING_JULIA_POINT) {
            mandelbrot.state.set(MandelbrotState.MANDELBROT);
        } else if (mandelbrot.state.get() == MandelbrotState.MANDELBROT){
            juliaButton.setText("Click to pick a seed...");
            getChildren().add(1, juliaView);
            getChildren().remove(instructions);
            animationButton.setVisible(false);
            mandelbrot.state.set(MandelbrotState.PICKING_JULIA_POINT);
        }
    }

    @Override
    public void update(Observable o, Object args) {
        if (o instanceof Coordinate) {
            double x = ((double[]) args)[0];
            double y = ((double[]) args)[1];
            coordReadout.setText(getMouseCoordinateString(x, y));
            if (mandelbrot.state.get() == MandelbrotState.PICKING_JULIA_POINT) {
                juliaPreview.setJuliaSeed(fractal.getRealComponent(x), fractal.getImaginaryComponent(y));
            }
        } else if (o instanceof State) {
            MandelbrotState oldState = ((MandelbrotState[]) args)[0];
            MandelbrotState newState = ((MandelbrotState[]) args)[1];

            if (oldState == MandelbrotState.PICKING_JULIA_POINT && newState == MandelbrotState.MANDELBROT) {
                getChildren().add(0, instructions);
                getChildren().remove(juliaView);
                juliaButton.setText("Generate Julia Set");
                animationButton.setVisible(true);
            } else if (oldState == MandelbrotState.PICKING_JULIA_POINT && newState == MandelbrotState.JULIA) {
                juliaButton.setText("Back to Mandelbrot");
                seedReadout.setText(fractal.enableJulia(mandelbrot.juliaCoords.getX(), mandelbrot.juliaCoords.getY()));
                seedReadout.setVisible(true);
                getChildren().remove(juliaView);
                getChildren().add(0, instructions);
            }


        }

    }

    /**
     * Turns an x, y coordinate on the imageview into a coordinate string that shows the complex point at those coordinates
     * @param x
     * @param y
     * @return a string displaying the mouse coordinates
     */
    private String getMouseCoordinateString(double x, double y) {
        double re = fractal.getRealComponent(x);
        double im = fractal.getImaginaryComponent(y);

        return "Mouse: " + String.format("%.3f", re) + (im >= 0 ? " + " : " - ") + String.format("%.3f", Math.abs(im)) + "i";
    }


    /**
     * Saves the fractal image to a file
     */
    private void saveFractal() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(mandelbrot.getStage());
        if (file == null) return;

        BufferedImage bImage = SwingFXUtils.fromFXImage(fractal.imageProperty.get(), null);
        try {
            ImageIO.write(bImage, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
