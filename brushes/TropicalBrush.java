package mandelbrot.brushes;

import javafx.scene.paint.Color;
import mandelbrot.Brush;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 */
public class TropicalBrush extends Brush {

    public TropicalBrush(int maxIterations) {
        super(maxIterations);
    }

    @Override
    public Color getColor(int iteration, double escapeMagnitude) {

        if (iteration == maxIterations) {
            return Color.BLACK;
        }

        return Color.hsb(iteration % 360, 0.8, 0.8);
    }
}
