package mandelbrot.gui;

import javafx.scene.layout.BorderPane;
import mandelbrot.fractal.Fractal;

/**
 * Created by kiran on 7/21/17.
 */
public class MasterPane extends BorderPane {
    MasterPane(Mandelbrot mandelbrot) {
        super();

        FractalPane fractalPane = new FractalPane(mandelbrot);
        SideBar sideBar = new SideBar(mandelbrot);

        this.setCenter(fractalPane);
        this.setRight(sideBar);
    }
}
