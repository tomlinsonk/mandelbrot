package mandelbrot;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import mandelbrot.brushes.SmoothBrush;

import java.util.Stack;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 *
 * This class stores the image of a fractal as well as its current parameters. The viewpoint can be moved around and the fractal image can be regenerated.
 */
public class Fractal {

    double width, height;
    double reCenter, imCenter;
    double zoom;
    double juliaReSeed, juliaImSeed;

    // Properties for UI input/display binding
    DoubleProperty zoomProperty;

    int maxIterations;
    double colorOffset;
    boolean rendering;
    boolean isJulia;
    Image image;
    Brush brush;

    ImageView imageView;
    ProgressIndicator indicator;

    Stack<FractalState> mandelbrotHistory;
    Stack<FractalState> juliaHistory;


    /**
     * Constructor
     *
     * @param width     the width in pixels of the fractal
     * @param height    the height in pixels of the fractal
     * @param imageView the view this fractal is displayed in
     * @param indicator the progress indicator
     */
    public Fractal(double width, double height, ImageView imageView, ProgressIndicator indicator) {

        mandelbrotHistory = new Stack<>();
        juliaHistory = new Stack<>();

        this.width = width;
        this.height = height;
        this.imageView = imageView;
        this.indicator = indicator;


        // Default values
        zoom = 400;
        zoomProperty = new SimpleDoubleProperty(1);
        colorOffset = 0;
        rendering = false;
        reCenter = -0.75;
        imCenter = 0;
        maxIterations = 1000;
        brush = new SmoothBrush(maxIterations);

        isJulia = false;
        juliaImSeed = 0;
        juliaReSeed = 0;

        // Create fractal
        generate();
    }


    /**
     * Set the max iterations of the fractal and the brush
     *
     * @param maxIterations
     */
    public void setMaxIterations(int maxIterations) {
        if (rendering) return;
        this.maxIterations = maxIterations;
        brush.maxIterations = maxIterations;
        generate();
    }

    /**
     * Change the brush being used to paint the fractal
     *
     * @param brush
     */
    public void setBrush(Brush brush) {
        if (rendering) return;
        this.brush = brush;
        generate();
    }

    /**
     * Set the color offset used by the brush
     *
     * @param colorOffset
     */
    public void setColorOffset(double colorOffset) {
        if (rendering) return;
        this.colorOffset = colorOffset;
        generate();
    }

    /**
     * These methods move the view of the fractal.
     * These methods will not run while a fractal is rendering.
     */

    public void moveRight() {
        if (rendering) return;
        reCenter += width / zoom / 10.0;
        generate();
    }

    public void moveLeft() {
        if (rendering) return;
        reCenter -= width / zoom / 10.0;
        generate();
    }

    public void moveUp() {
        if (rendering) return;
        imCenter -= height / zoom / 10.0;
        generate();
    }

    public void moveDown() {
        if (rendering) return;
        imCenter += height / zoom / 10.0;
        generate();
    }

    public void backToLastState() {
        if (rendering) return;

        if (isJulia) {
            if (juliaHistory.empty()) return;
            setValuesToState(juliaHistory.pop());
        } else {
            if (mandelbrotHistory.empty()) return;
            setValuesToState(mandelbrotHistory.pop());
        }

        generate();
    }

    /**
     * Zoom in based on the position of the zoom rectangle
     * @param xPixel top left x coordinate on the image
     * @param yPixel top left y coordinate on the image
     * @param pixelWidth width of the zoom rectangle
     * @param pixelHeight height of the zoom rectangle
     */
    public void zoomIn(double xPixel, double yPixel, double pixelWidth, double pixelHeight) {
        if (rendering) return;

        if (isJulia) {
            juliaHistory.push(new FractalState(this));
        } else {
            mandelbrotHistory.push(new FractalState(this));
        }

        // Find the new center and zoom level
        reCenter = getRealComponent(xPixel + pixelWidth / 2);
        imCenter = - getImaginaryComponent(yPixel + pixelHeight / 2);
        zoom *= width / pixelWidth;
        zoomProperty.set(zoomProperty.get() * width / pixelWidth);

        generate();
    }


    /**
     * Method to generate the fractal based on current state.
     * Uses a task so it runs in the background and sends progress to the indicator.
     */
    private void generate() {

        Task generateFractal = new Task<Void>() {

            @Override
            protected Void call() {

                // Don't allow other updates while rendering
                Platform.runLater(() -> rendering = true);

                // Create image
                WritableImage newImage = new WritableImage((int) width, (int) height);
                PixelWriter pixels = newImage.getPixelWriter();

                // Iterate over every pixel on the screen, figure out if it's in the set, and color it
                for (int xPixel = 0; xPixel < width; xPixel++) {
                    for (int yPixel = 0; yPixel < height; yPixel++) {

                        double re0, im0, re, im;

                        if (isJulia) {
                            re0 = juliaReSeed;
                            im0 = juliaImSeed;
                            re = getRealComponent(xPixel);
                            im = getImaginaryComponent(yPixel);
                        } else {
                            re0 = getRealComponent(xPixel);
                            im0 = getImaginaryComponent(yPixel);
                            re = 0;
                            im = 0;
                        }

                        double reSqr = re * re;
                        double imSqr = im * im;

                        double reLast = 0;
                        double imLast = 0;

                        int iteration = 0;

                        while (reSqr + imSqr < 4 && iteration < maxIterations) {
                            im = 2 * (re * im) + im0;
                            re = reSqr - imSqr + re0;
                            reSqr = re * re;
                            imSqr = im * im;

                            if (re == reLast && im == imLast) {
                                iteration = maxIterations;
                                break;
                            }

                            reLast = re;
                            imLast = im;
                            iteration++;
                        }

                        double escapeMagnitude = Math.sqrt(reSqr + imSqr);

                        // Use the brush to pick a color
                        pixels.setColor(xPixel, yPixel, brush.getColor(iteration, escapeMagnitude, colorOffset));
                    }

                    // Update the progress of this task
                    updateProgress(xPixel, width);
                }

                // Send the new image to the view
                image = newImage;
                Platform.runLater(() -> {
                    imageView.setImage(image);
                    rendering = false;
                });

                return null;
            }
        };


        // Bind the indicator to the task and start it
        indicator.progressProperty().bind(generateFractal.progressProperty());
        new Thread(generateFractal).start();
    }

    /**
     * Converts an x coordinate on the image into the real component of that point
     *
     * @param xPixel
     * @return
     */
    public double getRealComponent(double xPixel) {
        return (reCenter - width / zoom / 2.0) + (xPixel / zoom);
    }

    /**
     * Converts a y coordinate on the image into the imaginary component of that point.
     * Note that the signs are swapped from above because y values get bigger lower on the screen.
     * This is not important for the set (it is symmetrical about the real axis), but it makes more sense for displaying coordinates
     *
     * @param yPixel
     * @return
     */
    public double getImaginaryComponent(double yPixel) {
        return (height / zoom / 2.0 - imCenter) - (yPixel / zoom);
    }


    /**
     * Turn julia set generation on and reset view parameters.
     * @param xPixel
     * @param yPixel
     * @return A string of the re, im seed of this julia set
     */
    public String enableJulia(double xPixel, double yPixel) {
        // Save the current state
        mandelbrotHistory.push(new FractalState(this));

        this.juliaReSeed = getRealComponent(xPixel);
        this.juliaImSeed = getImaginaryComponent(yPixel);
        this.isJulia = true;
        zoom = 400;
        zoomProperty.setValue(1);
        reCenter = 0;
        imCenter = 0;

        generate();

        return "Seed: " + String.format("%.3f", juliaReSeed) + (juliaImSeed >= 0 ? " + " : " - ") + String.format("%.3f", Math.abs(juliaImSeed)) + "i";
    }

    /**
     * Return to the mandelbrot set where we left off
     */
    public void disableJulia() {
        this.isJulia = false;
        setValuesToState(mandelbrotHistory.pop());
        juliaHistory = new Stack<>();
        generate();
    }


    /**
     * Set this fractal's values to those stored in a FractalState
     * @param state
     */
    private void setValuesToState(FractalState state) {
        zoom = state.zoom;
        reCenter = state.reCenter;
        imCenter = state.imCenter;
        zoomProperty.setValue(state.readableZoom);
    }

}
