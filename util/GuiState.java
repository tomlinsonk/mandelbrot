package mandelbrot.util;

import java.util.Observable;


/**
 * Created by kiran on 7/22/17.
 */
public class GuiState extends Observable {
    private State state;

    public GuiState() {
        state = State.MANDELBROT;
    }

    public State get() {
        return state;
    }

    public void set(State state) {
        setChanged();
        notifyObservers(new State[] {this.state, state});
        this.state = state;
    }
}
