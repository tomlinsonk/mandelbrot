package mandelbrot.brushes;

import mandelbrot.core.Brush;

import java.awt.*;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 */
public class SmoothBrush extends Brush {

    public SmoothBrush(int maxIterations) {
        super(maxIterations);
    }

    @Override
    public int getColor(int iteration, double escapeMagnitude, float offset) {

        if (iteration == maxIterations) {
            return 0;
        }

        double smooth = iteration + 1 - Math.log(Math.log(escapeMagnitude)) / Math.log(2.0);


        return Color.getHSBColor(offset + (float)(smooth / maxIterations), 0.6f, 1.0f).getRGB();
    }
}
