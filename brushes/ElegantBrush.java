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
    public Color getColor(int iteration,  double escapeMagnitude) {
        if (iteration == maxIterations) {
            return Color.BLACK;
        }

        int lum = iteration % 255;
        if ((iteration / 255) % 2 != 0) {
            lum = 255 - (iteration % 255);
        }

        return Color.rgb(lum, lum, lum);
    }
}
