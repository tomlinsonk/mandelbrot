package mandelbrot;

import javafx.scene.paint.Color;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 *
 * This interface defines the structure of the Brush classes.
 * A brush translates an iteration into a color
 */
public abstract class Brush {

    protected int maxIterations;

    public Brush(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public abstract Color getColor(int iteration, double escapeMagnitude);
}
