package mandelbrot.gui;

import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import mandelbrot.fractal.Fractal;
import mandelbrot.fractal.Point;
import mandelbrot.util.State;
import mandelbrot.util.GuiState;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by kiran on 7/21/17.
 *
 * This class is the Pane that contains the fractal image.
 */
public class FractalPane extends AnchorPane implements Observer {

    private Fractal fractal;
    private Mandelbrot mandelbrot;

    private Rectangle zoomRect;
    private double zoomRectStartX, zoomRectStartY;
    private ImageView imageView;



    FractalPane(Mandelbrot mandelbrot) {
        super();

        this.mandelbrot = mandelbrot;
        fractal = mandelbrot.getFractal();
        zoomRectStartX = 0;
        zoomRectStartY = 0;

        mandelbrot.state.addObserver(this);

        imageView = new ImageView();
        getChildren().add(imageView);
        imageView.imageProperty().bind(fractal.imageProperty);

        // Setup zoom rectangle
        zoomRect = new Rectangle();
        zoomRect.setStroke(Color.WHITE);
        zoomRect.setFill(null);
        zoomRect.setVisible(false);
        zoomRect.toFront();
        getChildren().add(zoomRect);

        setOnMousePressed(event -> createNewZoomRect(event.getX(), event.getY()));
        setOnMouseDragged(event -> resizeZoomRect(event.getX(), event.getY()));
        setOnMouseReleased(event -> zoomFromRect());
        setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        imageView.setOnMouseMoved(event -> mandelbrot.mouseCoords.set(event.getX(), event.getY()));
    }


    @Override
    public void update(Observable o, Object args) {
        if (o instanceof GuiState) {
            State oldState = ((State[]) args)[0];
            State newState = ((State[]) args)[1];

            if (oldState == State.PICKING_JULIA_POINT && newState == State.MANDELBROT) {
                imageView.setOnMouseClicked(click -> {});
            } else if (oldState == State.MANDELBROT && newState == State.PICKING_JULIA_POINT) {
                imageView.setOnMouseClicked(click -> {
                    mandelbrot.juliaCoords.set(click.getX(), click.getY());
                    mandelbrot.state.set(State.JULIA);
                    imageView.setOnMouseClicked(next -> {});
                });
            } else if (oldState == State.MANDELBROT && newState == State.CREATING_PATH) {
                ArrayList<Point> path = new ArrayList();
                imageView.setOnMousePressed(click -> {
                    Point prevClick = new Point(fractal.getRealComponent(click.getX()), fractal.getImaginaryComponent(click.getY()));
                    path.add(prevClick);
                    imageView.setOnMousePressed(nextClick -> {});
                    imageView.setOnMouseDragged(nextClick -> {
                        Point newClick = new Point(fractal.getRealComponent(nextClick.getX()), fractal.getImaginaryComponent(nextClick.getY()));
                        path.add(newClick);

                        Line line = new Line(nextClick.getX(), nextClick.getY(), nextClick.getX(), nextClick.getY());
                        line.setFill(null);
                        line.setStroke(Color.WHITE);
                        line.setStrokeWidth(2);
                        getChildren().add(line);
                    });
                    imageView.setOnMouseReleased(nextClick -> {
                        imageView.setOnMouseDragged(c -> {});
                        imageView.setOnMouseReleased(c -> {});
                        mandelbrot.state.set(State.MANDELBROT);
                        System.out.println(path.size());
                        getChildren().removeIf(x -> x instanceof Line);
                    });
                });
            }

        }
    }


    /**
     * Handle any FractalPane-specific key presses
     * @param key
     */
    private void handleKeyPress(KeyCode key) {
        if (key == KeyCode.ESCAPE) {
            if (mandelbrot.state.get() == State.SELECTING_ZOOM) {
                mandelbrot.state.set(State.MANDELBROT);
            }

            zoomRect.setVisible(false);
            zoomRect.setWidth(0);
            zoomRect.setHeight(0);
        }
    }

    /**
     * Starts a new zoom rectangle based on click coordinates
     * @param clickX
     * @param clickY
     */
    private void createNewZoomRect(double clickX, double clickY) {
        if (mandelbrot.state.get() == State.MANDELBROT) {
            zoomRect.setX(clickX);
            zoomRect.setY(clickY);
            zoomRectStartX = clickX;
            zoomRectStartY = clickY;

            zoomRect.setVisible(true);
            mandelbrot.state.set(State.SELECTING_ZOOM);
        }
    }

    /**
     * Resize the current zoom rectangle based on new mouse coordinates
     * @param newX new x coord of mouse
     * @param newY new y coord of mouse
     */
    private void resizeZoomRect(double newX, double newY) {
        if (mandelbrot.state.get() == State.SELECTING_ZOOM && this.isHover()) {
            double desiredWidth = newX - zoomRectStartX;
            double desiredHeight = newY - zoomRectStartY;
            double fractalRatio = fractal.getWidth() / fractal.getHeight();
            double aspectRatio = Math.abs(desiredWidth / desiredHeight);

            // Do we need to negate width or height?
            int widthSign = desiredWidth < 0 ? -1 : 1;
            int heightSign = desiredHeight < 0 ? -1 : 1;

            // Resize rectangle with aspect ratio constraint
            if (aspectRatio <= fractalRatio) {
                zoomRect.setWidth(widthSign * desiredWidth);
                zoomRect.setHeight(widthSign * desiredWidth / fractalRatio);
            } else {
                zoomRect.setWidth(heightSign * desiredHeight * fractalRatio);
                zoomRect.setHeight(heightSign * desiredHeight);
            }

            // Do we need to translate or set translate to 0?
            int moveX = desiredWidth < 0 ? -1 : 0;
            int moveY = desiredHeight < 0 ? -1 : 0;

            // Translate if necessary (thanks, ternary!)
            zoomRect.setX(zoomRectStartX + moveX * zoomRect.getWidth());
            zoomRect.setY(zoomRectStartY + moveY * zoomRect.getHeight());
        }
    }


    /**
     * Zooms in based on the current zoom rectangle
     */
    private void zoomFromRect() {
        if (mandelbrot.state.get() == State.SELECTING_ZOOM) {
            mandelbrot.state.set(State.MANDELBROT);

            if (zoomRect.getWidth() > 0 && zoomRect.getHeight() > 0) {
                fractal.zoomIn(zoomRect.getX(), zoomRect.getY(), zoomRect.getWidth(), zoomRect.getHeight());
            }

            zoomRect.setVisible(false);
            zoomRect.setWidth(0);
            zoomRect.setHeight(0);
        }
    }

}
