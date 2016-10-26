package mandelbrot;

import javafx.scene.image.Image;

/**
 * Created by kiran on 10/25/16.
 */
public class FractalState {
    double reCenter, imCenter;
    double zoom;
    double readableZoom;

    Brush brush;
    double colorOffset;
    int maxIterations;

    Image image;

    FractalState(Fractal fractal) {
        reCenter = fractal.reCenter;
        imCenter = fractal.imCenter;
        zoom = fractal.zoom;
        readableZoom = fractal.zoomProperty.doubleValue();

        brush = fractal.brush;
        colorOffset = fractal.colorOffset;
        maxIterations = fractal.maxIterations;

        image = fractal.image;
    }

    /**
     * Check if a fractal is compatible with this state, ie they have the same parameters that affect the image.
     * @param fractal
     * @return true if they match, else false
     */
    public boolean isCompatible(Fractal fractal) {
        return fractal.brush.getClass().equals(brush.getClass()) && fractal.colorOffset == colorOffset && fractal.maxIterations == maxIterations;
    }
}
