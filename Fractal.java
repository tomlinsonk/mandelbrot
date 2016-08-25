package mandelbrot;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Created by kiran on 8/25/16.
 */
public class Fractal {

    double width, height;
    double xCenter, yCenter;
    double zoom;

    int maxIterations;

    Image image;

    public Fractal(int width, int height) {

        this.width = width;
        this.height = height;

        // Default values
        zoom = 400;
        xCenter = -0.75;
        yCenter = 0;
        maxIterations = 1000;

        // Create fractal
        generate();
    }



    /**
     * get the fractal's current image
     * @return
     */
    public Image getImage() {
        return image;
    }



    /**
     * Method to generate the fractal based on current state.
     */
    private void generate() {
        WritableImage newImage = new WritableImage((int)width, (int)height);
        PixelWriter pixels = newImage.getPixelWriter();

        for (int xPixel = 0; xPixel < width; xPixel++) {
            for (int yPixel = 0; yPixel < height; yPixel++) {

                double x0 = xScale(xPixel);
                double y0 = yScale(yPixel);

                double x = 0;
                double y = 0;

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

//                System.out.println(xPixel + ", " + yPixel +  " (" + x0 + ", " + y0 + ") -- " + iteration);

                // TODO replace all this crap with brushes
                pixels.setColor(xPixel, yPixel, Color.rgb(iteration == maxIterations ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration + 0) % 225)),
                        iteration == maxIterations ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration + 0) % 225)),
                        iteration == maxIterations ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration +  0) % 225))));


                // Simple
//				image.setRGB(pixelX, pixelY, new Color(iteration == maxIteration ? 255 : 0,
//											   iteration == maxIteration ? 255 : 0,
//			       							   iteration == maxIteration ? 255 : 0).getRGB());

                // Interesting
//                image.setRGB(pixelX, pixelY, new Color(iteration == maxIteration ? 255 : (iteration + 70) % 255,
//                        iteration == maxIteration ? 255 : (iteration + 140) % 255,
//                        iteration == maxIteration ? 255 : (iteration + 210) % 255).getRGB());

                // Proper Rainbow
//				image.setRGB(pixelX, pixelY, iteration == maxIteration ? new Color(255, 255, 255).getRGB() : pallete[iteration]);

                // Black and white gradients
//				image.setRGB(pixelX, pixelY, new Color(iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration + 0) % 225)),
//						   					   iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration + 0) % 225)),
//						   					   iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration +  0) % 225))).getRGB());
//
                // Stripes
//				int[] colors = {Color.white.getRGB(), Color.black.getRGB(), Color.red.getRGB()};
//				image.setRGB(pixelX, pixelY, colors[iteration % 3]);

                // Black, Red, White
//				int outer = iteration % 255;
//				outer = outer < 50 ? 0 : 255;
//				image.setRGB(pixelX, pixelY, new Color(iteration == maxIteration ? 255 : outer,
//											   iteration == maxIteration ? 255 : 0,
//										       iteration == maxIteration ? 255 : 0).getRGB());


            }
        }

        image = newImage;

    }

    private double xScale(int x) {
        return (xCenter - width / zoom / 2.0) + (x / zoom);
    }

    private double yScale(int y) {
        return (yCenter - height / zoom / 2.0) + (y / zoom);
    }
}
