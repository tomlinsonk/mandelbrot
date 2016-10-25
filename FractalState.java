package mandelbrot;

/**
 * Created by kiran on 10/25/16.
 */
public class FractalState {
    double reCenter, imCenter;
    double zoom;
    double readableZoom;

    FractalState(Fractal fractal) {
        reCenter = fractal.reCenter;
        imCenter = fractal.imCenter;
        zoom = fractal.zoom;
        readableZoom = fractal.zoomProperty.doubleValue();
    }
}
