package mandelbrot.util;

import java.util.Observable;

/**
 * Created by kiran on 7/22/17.
 */
public class Coordinate extends Observable {
    private double x, y;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
        setChanged();
        notifyObservers();
    }

    public void setY(double y) {
        this.y = y;
        setChanged();
        notifyObservers();
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
        setChanged();
        notifyObservers(new double[] {x, y});
    }
}
