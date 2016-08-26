package mandelbrot.brushes;

import javafx.scene.paint.Color;
import mandelbrot.Brush;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 */
public class BinaryBrush extends Brush {

    public BinaryBrush(int maxIterations) {
        super(maxIterations);
    }


    @Override
    public Color getColor(int iteration, double escapeMagnitude) {

        if (iteration == maxIterations) {
            return Color.BLACK;
        }

        return Color.WHITE;
    }
}
