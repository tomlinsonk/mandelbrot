package mandelbrot;

import javafx.scene.paint.Color;

/**
 * Created by kiran on 8/25/16.
 *
 * This interface defines the structure of the Brush classes.
 * A brush translates an
 */
public abstract class Brush {

    protected int maxIterations;

    public Brush(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public abstract Color getColor(int iteration);
}
