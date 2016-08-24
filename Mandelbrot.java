package mandelbrot;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by kiran on 8/24/16.
 *
 * This is the main class of the application. Calculates what pixels are in the set
 * and draws the set.
 */
public class Mandelbrot extends Application {

    // TODO: Smart screen size
    final double X_SIZE = 3.5;
    final double Y_SIZE = 2;
    final double SCREEN_WIDTH = 1920;
    final double SCREEN_HEIGHT = 1105;

    double zoomLevel = 1;
    double xCenter = -0.75;
    double yCenter = 0;


    @Override
    public void start(Stage stage) throws Exception {

        // Create all JavaFX objects
        BorderPane pane = new BorderPane();
        Image fractal = getFractal(xCenter, yCenter, zoomLevel);
        ImageView imageView = new ImageView();

        imageView.setImage(fractal);

        // Create the scene
        Scene scene = new Scene(pane, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Add the canvas to the scene
        pane.setCenter(imageView);


        // Set all stage attributes
        stage.setScene(scene);
        stage.setTitle("Mandelbrot");
        stage.setResizable(false);
        stage.show();


    }

    public Image getFractal(double xCenter, double yCenter, double zoom) {
        WritableImage fractal = new WritableImage((int)SCREEN_WIDTH, (int)SCREEN_HEIGHT);
        PixelWriter pixels = fractal.getPixelWriter();

        double xZoom = X_SIZE / zoom;
        double yZoom = Y_SIZE / zoom;

        for (int Px = 0; Px < fractal.getWidth(); Px++) {
            for (int Py = 0; Py < fractal.getHeight(); Py++) {

                double x0 = xScale(Px, xCenter, xZoom);
                double y0 = yScale(Py, yCenter, yZoom);


                double x = 0;
                double y = 0;

                // Julia Sets
//				double x0 = 0.0315;
//				double y0 = -0.121;
//
//				double x = xScale(Px, xCenter, xZoom);
//				double y = yScale(Py, yCenter, yZoom);
//
                double xSqr = x * x;
                double ySqr = y * y;

                double xLast = 0;
                double yLast = 0;

                int iteration = 0;
                int maxIteration = 1_000;

                while (xSqr + ySqr < 4 && iteration < maxIteration) {
                    y *= x;
                    y += y;
                    y += y0;
                    x = xSqr - ySqr + x0;
                    xSqr = x * x;
                    ySqr = y * y;

                    if (x == xLast && y == yLast) {
                        iteration = maxIteration;
                        break;
                    }

                    xLast = x;
                    yLast = y;
                    iteration++;
                }

//                System.out.println(Px + ", " + Py + ": " + x0 + ", " + y0 + " -- " + iteration);


                pixels.setColor(Px, Py, Color.rgb(iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration + 0) % 225)),
						   					      iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration + 0) % 225)),
                                                  iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration +  0) % 225))));


                // Simple
//				image.setRGB(Px, Py, new Color(iteration == maxIteration ? 255 : 0,
//											   iteration == maxIteration ? 255 : 0,
//			       							   iteration == maxIteration ? 255 : 0).getRGB());

                // Interesting
//                image.setRGB(Px, Py, new Color(iteration == maxIteration ? 255 : (iteration + 70) % 255,
//                        iteration == maxIteration ? 255 : (iteration + 140) % 255,
//                        iteration == maxIteration ? 255 : (iteration + 210) % 255).getRGB());

                // Proper Rainbow
//				image.setRGB(Px, Py, iteration == maxIteration ? new Color(255, 255, 255).getRGB() : pallete[iteration]);

                // Black and white gradients
//				image.setRGB(Px, Py, new Color(iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration + 0) % 225)),
//						   					   iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration + 0) % 225)),
//						   					   iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration +  0) % 225))).getRGB());
//
                // Stripes
//				int[] colors = {Color.white.getRGB(), Color.black.getRGB(), Color.red.getRGB()};
//				image.setRGB(Px, Py, colors[iteration % 3]);

                // Black, Red, White
//				int outer = iteration % 255;
//				outer = outer < 50 ? 0 : 255;
//				image.setRGB(Px, Py, new Color(iteration == maxIteration ? 255 : outer,
//											   iteration == maxIteration ? 255 : 0,
//										       iteration == maxIteration ? 255 : 0).getRGB());


            }
        }

        return fractal;

    }

    private double xScale(int x, double xCenter, double zoom) {
        double fraction = x / SCREEN_WIDTH;
        return (xCenter - zoom / 2.0) + (fraction * zoom);
    }

    private  double yScale(int y, double yCenter, double zoom) {
        double fraction = y / SCREEN_HEIGHT;
        return (yCenter - zoom / 2.0) + (fraction * zoom);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
