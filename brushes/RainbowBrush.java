package mandelbrot.brushes;

import javafx.scene.paint.Color;
import mandelbrot.Brush;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 */
public class RainbowBrush extends Brush {

    Color[] rainbow = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.INDIGO, Color.VIOLET};

    public RainbowBrush(int maxIterations) {
        super(maxIterations);
    }

    @Override
    public Color getColor(int iteration) {

        if (iteration == maxIterations) {
            return Color.WHITE;
        }

        return rainbow[(iteration - 1) % 7];
    }
}
