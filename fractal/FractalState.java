package mandelbrot.fractal;

import javafx.scene.image.Image;
import mandelbrot.brush.Brush;

/**
 * Created by kiran on 10/25/16.
 *
 * This class stores a snapshot of a fractal that can be loaded back into a fractal object.
 */
class FractalState {
    double reCenter, imCenter;
    double zoom;
    double readableZoom;

    private Brush brush;
    private double colorOffset;
    private int maxIterations;

    Image image;

    FractalState(Fractal fractal, double reCenter, double imCenter, double zoom, Brush brush, double colorOffset, int maxIterations, Image image) {
        this.reCenter = reCenter;
        this.imCenter = imCenter;
        this.zoom = zoom;
        this.readableZoom = fractal.zoomProperty.doubleValue();
        this.brush = brush;
        this.colorOffset = colorOffset;
        this.maxIterations = maxIterations;
        this.image = image;
    }

    /**
     * Check if a fractal is compatible with this state, ie they have the same parameters that affect the image.
     * @param brush
     * @param colorOffset
     * @param maxIterations
     * @return true if they match, else false
     */
    boolean isCompatible(Brush brush, double colorOffset, int maxIterations) {
        return brush.getClass().equals(this.brush.getClass()) && colorOffset == this.colorOffset && maxIterations == this.maxIterations;
    }
}
