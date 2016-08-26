package mandelbrot.brushes;

import javafx.scene.paint.Color;
import mandelbrot.Brush;

import java.util.Random;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 */
public class BandedBrush extends Brush {

    Color[] randomColors;

    public BandedBrush(int maxIterations) {
        super(maxIterations);

        Random random = new Random();
        randomColors = new Color[maxIterations];

        for (int i = 0; i < maxIterations; i++) {
            randomColors[i] = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        }
    }

    @Override
    public Color getColor(int iteration, double escapeMagnitude) {
        if (iteration == maxIterations) {
            return Color.BLACK;
        }

        return randomColors[iteration];
    }
}
