package mandelbrot.util;

import java.util.Observable;


/**
 * Created by kiran on 7/22/17.
 */
public class State extends Observable {
    private MandelbrotState state;

    public State() {
        state = MandelbrotState.MANDELBROT;
    }

    public MandelbrotState get() {
        return state;
    }

    public void set(MandelbrotState state) {
        setChanged();
        notifyObservers(new MandelbrotState[] {this.state, state});
        this.state = state;
    }
}
