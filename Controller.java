package mandelbrot;

import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import mandelbrot.brushes.*;


/**
 * Created by kiran on 8/25/16.
 *
 * This controller handles input from the user and adjusts the model and view according to this input.
 */
public class Controller {

    Fractal fractal;
    ImageView imageView;

    public Controller(Fractal fractal, ImageView imageView) {
        this.fractal = fractal;
        this.imageView = imageView;
    }


    /**
     * Handles keyboard input to and adjusts the model and view accordingly
     * @param key the key that was pressed
     */
    public void handleKeyPress(KeyCode key) {

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

        imageView.setImage(fractal.getImage());

    }


    public void updateBrush(String brushName) {

        switch (brushName) {
            case "Default":
                fractal.setBrush(new DefaultBrush(fractal.maxIterations));
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

        imageView.setImage(fractal.getImage());
    }
}
