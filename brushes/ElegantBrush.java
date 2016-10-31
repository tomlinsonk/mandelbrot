package mandelbrot.brushes;

import mandelbrot.core.Brush;

import java.awt.*;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 */
public class ElegantBrush extends Brush {

    public ElegantBrush(int maxIterations) {
        super(maxIterations);
    }

    @Override
    public int getColor(int iteration,  double escapeMagnitude, float offset) {
        if (iteration == maxIterations) {
            return 0;
        }

        double brightness = 0.5 + 0.5 * Math.cos((iteration + offset * maxIterations / 10) * Math.PI / (double)maxIterations * 10f);

        return Color.getHSBColor(0, 0, (float)brightness).getRGB();
    }
}
