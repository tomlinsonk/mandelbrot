package mandelbrot;

import javafx.scene.image.Image;
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
    Image image;
    Brush brush;

    /**
     * Constructor
     * @param width the width in pixels of the fractal
     * @param height the height in pixels of the fractal
     */
    public Fractal(int width, int height) {

        this.width = width;
        this.height = height;

        // Default values
        zoom = 400;
        xCenter = -0.75;
        yCenter = 0;
        maxIterations = 1000;
        brush = new ElegantBrush(maxIterations);

        // Create fractal
        generate();
    }

    public void setBrush(Brush brush) {
        this.brush = brush;
        generate();
    }



    public Image getImage() {
        return image;
    }


    /**
     * These methods move the view of the fractal.
     */

    public void moveRight() {
        xCenter +=  width / zoom / 10.0;
        generate();
    }

    public void moveLeft() {
        xCenter -=  width / zoom / 10.0;
        generate();
    }

    public void moveUp() {
        yCenter -=  height / zoom / 10.0;
        generate();
    }

    public void moveDown() {
        yCenter +=  height / zoom / 10.0;
        generate();
    }

    public void zoomIn() {
        zoom *= 2;
        generate();
    }

    public void zoomOut() {
        zoom /= 2;
        generate();
    }



    /**
     * Method to generate the fractal based on current state.
     */
    private void generate() {
        long startTime = System.currentTimeMillis();

        WritableImage newImage = new WritableImage((int)width, (int)height);
        PixelWriter pixels = newImage.getPixelWriter();

        for (int xPixel = 0; xPixel < width; xPixel++) {
            for (int yPixel = 0; yPixel < height; yPixel++) {

                double x0 = xScale(xPixel);
                double y0 = yScale(yPixel);

                double x = 0;
                double y = 0;

                // TODO julia sets?
                // Julia Sets
//				double x0 = 0.0315;
//				double y0 = -0.121;
//
//				double x = xScale(pixelX, xCenter, xZoom);
//				double y = yScale(pixelY, yCenter, yZoom);
//
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
        }

        image = newImage;

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        System.out.println("Completed in: " + duration + "ms");

    }



    /**
     * These methods adjust coordinate values based on the screen, current position, and zoom level
     */

    private double xScale(int x) {
        return (xCenter - width / zoom / 2.0) + (x / zoom);
    }

    private double yScale(int y) {
        return (yCenter - height / zoom / 2.0) + (y / zoom);
    }
}
