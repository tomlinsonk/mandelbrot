package mandelbrot.brushes;

import javafx.scene.paint.Color;
import mandelbrot.Brush;

/**
 * Created by kiran on 8/25/16.
 */
public class GraydientBrush extends Brush {


    public GraydientBrush(int maxIterations) {
        super(maxIterations);
    }


    @Override
    public Color getColor(int iteration) {

        if (iteration == maxIterations) {
            return Color.WHITE;
        }

        int lum = 255 * iteration / maxIterations;
        return Color.rgb(lum, lum, lum);
    }
}
