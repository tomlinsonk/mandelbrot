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

/**
 * Created by Kiran Tomlinson on 8/25/16.
 *
 * This class stores the image of a fractal as well as its current parameters. The viewpoint can be moved around and the fractal image can be regenerated.
 */
public class Fractal {

    double width, height;
    double reCenter, imCenter;
    double zoom;

    // Properties for UI input/display binding
    DoubleProperty zoomProperty;

    int maxIterations;
    double colorOffset;
    boolean rendering;
    Image image;
    Brush brush;

    ImageView imageView;
    ProgressIndicator indicator;



    /**
     * Constructor
     * @param width the width in pixels of the fractal
     * @param height the height in pixels of the fractal
     * @param imageView the view this fractal is displayed in
     * @param indicator the progress indicator
     */
    public Fractal(int width, int height, ImageView imageView, ProgressIndicator indicator) {

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

        // Create fractal
        generate();
    }


    /**
     * Set the max iterations of the fractal and the brush
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
     * @param brush
     */
    public void setBrush(Brush brush) {
        if (rendering) return;
        this.brush = brush;
        generate();
    }

    /**
     * Set the color offset used by the brush
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
        reCenter +=  width / zoom / 10.0;
        generate();
    }

    public void moveLeft() {
        if (rendering) return;
        reCenter -=  width / zoom / 10.0;
        generate();
    }

    public void moveUp() {
        if (rendering) return;
        imCenter -=  height / zoom / 10.0;
        generate();
    }

    public void moveDown() {
        if (rendering) return;
        imCenter +=  height / zoom / 10.0;
        generate();
    }

    public void zoomIn() {
        if (rendering) return;
        zoom *= 2;
        zoomProperty.set(zoomProperty.get() * 2);
        generate();
    }

    public void zoomOut() {
        if (rendering) return;
        zoom /= 2;
        zoomProperty.set(zoomProperty.get() / 2);
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
                WritableImage newImage = new WritableImage((int)width, (int)height);
                PixelWriter pixels = newImage.getPixelWriter();

                // Iterate over every pixel on the screen, figure out if it's in the set, and color it
                for (int xPixel = 0; xPixel < width; xPixel++) {
                    for (int yPixel = 0; yPixel < height; yPixel++) {

                        double re0 = getRealComponent(xPixel);
                        double im0 = getImaginaryComponent(yPixel);

                        double re = 0;
                        double im = 0;

                        // TODO julia sets?
//			         	double re0 = -0.8;
//				        double im0 = 0.156;
//
//				        double re = getRealComponent(xPixel);
//                      double im = getImaginaryComponent(yPixel);

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
     * @param xPixel
     * @return
     */
    public double getRealComponent(int xPixel) {
        return (reCenter - width / zoom / 2.0) + (xPixel / zoom);
    }

    /**
     * Converts a y coordinate on the image into the imaginary component of that point.
     * Note that the signs are swapped from above because y values get bigger lower on the screen.
     * This is not important for the set (it is symmetrical about the real axis), but it makes more sense for displaying coordinates
     * @param yPixel
     * @return
     */
    public double getImaginaryComponent(int yPixel) {
        return (height / zoom / 2.0 - imCenter) - (yPixel / zoom);
    }
}
