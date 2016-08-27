package mandelbrot.brushes;

import javafx.scene.paint.Color;
import mandelbrot.Brush;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 */
public class SmoothBrush extends Brush {

    public SmoothBrush(int maxIterations) {
        super(maxIterations);
    }

    @Override
    public Color getColor(int iteration, double escapeMagnitude, double offset) {

        if (iteration == maxIterations) {
            return Color.BLACK;
        }

        double smooth = iteration + 1 - Math.log(Math.log(escapeMagnitude)) / Math.log(2);


        return Color.hsb((offset * 360) + smooth, 0.6f, 1.0f);
    }
}
