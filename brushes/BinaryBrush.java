package mandelbrot.brushes;

import mandelbrot.core.Brush;

import java.awt.*;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 */
public class BinaryBrush extends Brush {

    public BinaryBrush(int maxIterations) {
        super(maxIterations);
    }


    @Override
    public int getColor(int iteration, double escapeMagnitude, float offset) {

        if (iteration == maxIterations) {
            return 0;
        }

        return Color.getHSBColor(offset * 360f, 0.6f, 1f).getRGB();
    }
}
