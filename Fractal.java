package mandelbrot;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import mandelbrot.brushes.ElegantBrush;

/**
 * Created by kiran on 8/25/16.
 *
 * This class stores the image of a fractal as well as its current parameters. The viewpoint can be moved around and the fractal image can be regenerated.
 */
public class Fractal {

    double width, height;
    double xCenter, yCenter;
    double zoom;

    int maxIterations;
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
        rendering = false;
        xCenter = -0.75;
        yCenter = 0;
        maxIterations = 1000;
        brush = new ElegantBrush(maxIterations);

        // Create fractal
        generate();
    }

    public void setMaxIterations(int maxIterations) {
        if (rendering) return;
        this.maxIterations = maxIterations;
        brush.maxIterations = maxIterations;
        generate();
    }

    public void setBrush(Brush brush) {
        if (rendering) return;
        this.brush = brush;
        generate();
    }

    /**
     * These methods move the view of the fractal.
     * These methods will not run while a fractal is rendering.
     */

    public void moveRight() {
        if (rendering) return;
        xCenter +=  width / zoom / 10.0;
        generate();
    }

    public void moveLeft() {
        if (rendering) return;
        xCenter -=  width / zoom / 10.0;
        generate();
    }

    public void moveUp() {
        if (rendering) return;
        yCenter -=  height / zoom / 10.0;
        generate();
    }

    public void moveDown() {
        if (rendering) return;
        yCenter +=  height / zoom / 10.0;
        generate();
    }

    public void zoomIn() {
        if (rendering) return;
        zoom *= 2;
        generate();
    }

    public void zoomOut() {
        if (rendering) return;
        zoom /= 2;
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

                        double x0 = (xCenter - width / zoom / 2.0) + (xPixel / zoom);
                        double y0 = (yCenter - height / zoom / 2.0) + (yPixel / zoom);

                        double x = 0;
                        double y = 0;

                        // TODO julia sets?
//			         	double x0 = 0.0315;
//				        double y0 = -0.121;
//
//				        double x = (xCenter - width / zoom / 2.0) + (xPixel / zoom);
//                      double y = (yCenter - height / zoom / 2.0) + (yPixel / zoom);

                        double xSqr = x * x;
                        double ySqr = y * y;

                        double xLast = 0;
                        double yLast = 0;

                        int iteration = 0;

                        while (xSqr + ySqr < 4 && iteration < maxIterations) {
                            y *= x;
                            y += y;
                            y += y0;
                            x = xSqr - ySqr + x0;
                            xSqr = x * x;
                            ySqr = y * y;

                            if (x == xLast && y == yLast) {
                                iteration = maxIterations;
                                break;
                            }

                            xLast = x;
                            yLast = y;
                            iteration++;
                        }

                        // Use the brush to pick a color
                        pixels.setColor(xPixel, yPixel, brush.getColor(iteration));
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
}
