package mandelbrot.brush;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 *
 * This interface defines the structure of the Brush classes.
 * A brush translates an iteration into a color
 */
public abstract class Brush {

    int maxIterations;

    public Brush(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public abstract int getColor(int iteration, double escapeMagnitude, float offset);

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
}
