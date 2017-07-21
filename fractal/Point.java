package mandelbrot.fractal;

/**
 * Created by kiran on 7/20/17.
 */
public class Point {
    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distanceTo(Point other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }
}
