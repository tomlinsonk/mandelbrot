package mandelbrot.brush;

import java.awt.*;
import java.util.Random;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 */
public class BandedBrush extends Brush {

    private int[] randomColors;

    public BandedBrush(int maxIterations) {
        super(maxIterations);

        Random random = new Random();
        randomColors = new int[maxIterations];

        for (int i = 0; i < maxIterations; i++) {
            randomColors[i] = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB();
        }
    }

    @Override
    public int getColor(int iteration, double escapeMagnitude, float offset) {
        if (iteration == maxIterations) {
            return 0;
        }

        return randomColors[(iteration + (int)(maxIterations * offset)) % maxIterations];
    }
}
