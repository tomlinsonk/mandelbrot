package mandelbrot.brushes;

import javafx.scene.paint.Color;
import mandelbrot.Brush;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 */
public class ElegantBrush extends Brush {

    public ElegantBrush(int maxIterations) {
        super(maxIterations);
    }

    @Override
    public Color getColor(int iteration,  double escapeMagnitude, double offset) {
        if (iteration == maxIterations) {
            return Color.BLACK;
        }

        double brightness = 0.5 + 0.5 * Math.cos((iteration + offset * maxIterations / 10) * Math.PI / (double)maxIterations * 10f);

        return Color.hsb(0, 0, brightness);
    }
}
