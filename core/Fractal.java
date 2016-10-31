package mandelbrot.core;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import mandelbrot.brushes.SmoothBrush;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.*;

/**
 * Created by Kiran Tomlinson on 8/25/16.
 *
 * This class stores the image of a fractal as well as its current parameters. The viewpoint can be moved around and the fractal image can be regenerated.
 */
public class Fractal {

    double width, height;
    double reCenter, imCenter;
    double zoom;
    double juliaReSeed, juliaImSeed;

    DoubleProperty zoomProperty;
    ObjectProperty<Image> imageProperty;
    StringProperty renderingProperty;

    int maxIterations;
    float colorOffset;
    boolean rendering;
    boolean isJulia;
    Image image;
    Brush brush;

    Stack<FractalState> mandelbrotHistory;
    Stack<FractalState> juliaHistory;


    /**
     * Constructor
     *
     * @param width     the width in pixels of the fractal
     * @param height    the height in pixels of the fractal
     */
    public Fractal(double width, double height) {

        mandelbrotHistory = new Stack<>();
        juliaHistory = new Stack<>();

        this.width = width;
        this.height = height;

        imageProperty = new SimpleObjectProperty<>();
        renderingProperty = new SimpleStringProperty("");

        // Default values
        zoom = 400;
        zoomProperty = new SimpleDoubleProperty(1);
        colorOffset = 0;
        rendering = false;
        reCenter = -0.75;
        imCenter = 0;
        maxIterations = 1000;
        brush = new SmoothBrush(maxIterations);

        isJulia = false;
        juliaImSeed = 0;
        juliaReSeed = 0;

        // Create fractal
        generate();
    }


    /**
     * Set the max iterations of the fractal and the brush
     *
     * @param maxIterations
     */
    public void setMaxIterations(int maxIterations) {
        if (rendering) return;
        this.maxIterations = maxIterations;
        brush.maxIterations = maxIterations;
        generate();
    }

    /**
     * Change the brush being used to paint the fractal
     *
     * @param brush
     */
    public void setBrush(Brush brush) {
        if (rendering) return;
        this.brush = brush;
        generate();
    }

    /**
     * Set the color offset used by the brush
     *
     * @param colorOffset
     */
    public void setColorOffset(float colorOffset) {
        if (rendering) return;
        this.colorOffset = colorOffset;
        generate();
    }

    /**
     * Turn julia set generation on and reset view parameters.
     * @param xPixel
     * @param yPixel
     * @return A string of the re, im seed of this julia set
     */
    public String enableJulia(double xPixel, double yPixel) {
        // Save the current state
        mandelbrotHistory.push(new FractalState(this));

        this.juliaReSeed = getRealComponent(xPixel);
        this.juliaImSeed = getImaginaryComponent(yPixel);
        this.isJulia = true;
        zoom = 400;
        zoomProperty.setValue(1);
        reCenter = 0;
        imCenter = 0;

        generate();

        return "Seed: " + String.format("%.3f", juliaReSeed) + (juliaImSeed >= 0 ? " + " : " - ") + String.format("%.3f", Math.abs(juliaImSeed)) + "i";
    }

    /**
     * Return to the mandelbrot set where we left off
     */
    public void disableJulia() {
        this.isJulia = false;
        juliaHistory = new Stack<>();
        if (!goToState(mandelbrotHistory.pop())) {
            generate();
        }
    }


    /**
     * These methods move the view of the fractal.
     * These methods will not run while a fractal is rendering.
     */

    public void moveRight() {
        if (rendering) return;

        if (isJulia) {
            juliaHistory.push(new FractalState(this));
        } else {
            mandelbrotHistory.push(new FractalState(this));
        }

        reCenter += width / zoom / 5.0;
        generate();
    }

    public void moveLeft() {
        if (rendering) return;

        if (isJulia) {
            juliaHistory.push(new FractalState(this));
        } else {
            mandelbrotHistory.push(new FractalState(this));
        }

        reCenter -= width / zoom / 5.0;
        generate();
    }

    public void moveUp() {
        if (rendering) return;

        if (isJulia) {
            juliaHistory.push(new FractalState(this));
        } else {
            mandelbrotHistory.push(new FractalState(this));
        }

        imCenter -= height / zoom / 5.0;
        generate();
    }

    public void moveDown() {
        if (rendering) return;

        if (isJulia) {
            juliaHistory.push(new FractalState(this));
        } else {
            mandelbrotHistory.push(new FractalState(this));
        }

        imCenter += height / zoom / 5.0;
        generate();
    }

    public void zoomInFixed() {
        if (rendering) return;

        if (isJulia) {
            juliaHistory.push(new FractalState(this));
        } else {
            mandelbrotHistory.push(new FractalState(this));
        }

        zoom *= 2;
        zoomProperty.set(zoomProperty.get() * 2);
        generate();
    }

    public void zoomOutFixed() {
        if (rendering) return;

        if (isJulia) {
            juliaHistory.push(new FractalState(this));
        } else {
            mandelbrotHistory.push(new FractalState(this));
        }

        zoom /= 2;
        zoomProperty.set(zoomProperty.get() / 2);
        generate();
    }

    public void backToLastState() {
        if (rendering) return;

        if (isJulia) {
            if (!juliaHistory.empty()) {
                if (!goToState(juliaHistory.pop())) {
                    generate();
                }
            }

        } else {
            if (!mandelbrotHistory.empty()) {
                if (!goToState(mandelbrotHistory.pop())) {
                    generate();
                }
            }
        }

    }

    /**
     * Zoom in based on the position of the zoom rectangle
     * @param xPixel top left x coordinate on the image
     * @param yPixel top left y coordinate on the image
     * @param pixelWidth width of the zoom rectangle
     * @param pixelHeight height of the zoom rectangle
     */
    public void zoomIn(double xPixel, double yPixel, double pixelWidth, double pixelHeight) {
        if (rendering) return;

        if (isJulia) {
            juliaHistory.push(new FractalState(this));
        } else {
            mandelbrotHistory.push(new FractalState(this));
        }

        // Find the new center and zoom level
        reCenter = getRealComponent(xPixel + pixelWidth / 2);
        imCenter = - getImaginaryComponent(yPixel + pixelHeight / 2);
        zoom *= width / pixelWidth;
        zoomProperty.set(zoomProperty.get() * width / pixelWidth);

        generate();
    }


    /**
     * Method to generate the fractal based on current state.
     * Uses a thread so it runs in the background and sends progress to the indicator.
     */
    private void generate() {
        rendering = true;
        renderingProperty.setValue("Rendering...");
        BufferedImage newImage = new BufferedImage((int)width, (int)height, BufferedImage.TYPE_INT_RGB);

        new Thread(() -> {
            try {
                combineSlices(newImage);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Creates an appropriate number of slice generator tasks based on the number of available cores.
     * @param newImage
     */
    private void combineSlices(BufferedImage newImage) throws InterruptedException, ExecutionException {

        long startTime = System.currentTimeMillis();

        Collection<Callable<BufferedImage>> tasks = new ArrayList<>();
        int processors = Runtime.getRuntime().availableProcessors();

        int sliceWidth = (int)(width / processors);
        for (int i = 0; i < processors; i++) {
            GenerateFractalSliceTask task = new GenerateFractalSliceTask(sliceWidth * i , sliceWidth * (i + 1), 0, (int) height);
            tasks.add(task);
        }

        ExecutorService executor = Executors.newFixedThreadPool(processors);
        List<Future<BufferedImage>> slices = executor.invokeAll(tasks);


        int i = 0;
        for (Future<BufferedImage> futureSlice : slices) {
            BufferedImage slice = futureSlice.get();
            newImage.createGraphics().drawImage(slice, sliceWidth * i, 0, null);
            i++;
        }

        executor.shutdown();

        Platform.runLater(() -> {
            rendering = false;
            renderingProperty.setValue("");
            image = SwingFXUtils.toFXImage(newImage, null);
            imageProperty.setValue(image);
        });

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime);
        System.out.println("Rendered in: " + duration + "ms");
    }

    /**
     * This class is a worker that generates a slice of the Mandelbrot/Julia set.
     */
    private final class GenerateFractalSliceTask implements Callable<BufferedImage> {
        int xPixelStart;
        int xPixelEnd;
        int yPixelStart;
        int yPixelEnd;

        GenerateFractalSliceTask(int xPixelStart, int xPixelEnd, int yPixelStart, int yPixelEnd) {
            this.xPixelStart = xPixelStart;
            this.xPixelEnd = xPixelEnd;
            this.yPixelStart = yPixelStart;
            this.yPixelEnd = yPixelEnd;
        }

        @Override
        public BufferedImage call() throws Exception {
            BufferedImage slice = new BufferedImage(xPixelEnd - xPixelStart, yPixelEnd - yPixelStart, BufferedImage.TYPE_INT_RGB);

            // Iterate over every pixel on the screen, figure out if it's in the set, and color it
            for (int xPixel = xPixelStart; xPixel < xPixelEnd; xPixel++) {

                for (int yPixel = yPixelStart; yPixel < yPixelEnd; yPixel++) {

                    double re0, im0, re, im;

                    if (isJulia) {
                        re0 = juliaReSeed;
                        im0 = juliaImSeed;
                        re = getRealComponent(xPixel);
                        im = getImaginaryComponent(yPixel);
                    } else {
                        re0 = getRealComponent(xPixel);
                        im0 = getImaginaryComponent(yPixel);
                        re = 0;
                        im = 0;
                    }

                    double reSqr = re * re;
                    double imSqr = im * im;

                    int iteration = 0;

                    while (reSqr + imSqr < 4 && iteration < maxIterations) {
                        im = 2 * (re * im) + im0;
                        re = reSqr - imSqr + re0;
                        reSqr = re * re;
                        imSqr = im * im;

                        iteration++;
                    }

                    double escapeMagnitude = Math.sqrt(reSqr + imSqr);

                    // Use the brush to pick a color
                    slice.setRGB(xPixel - xPixelStart, yPixel, brush.getColor(iteration, escapeMagnitude, colorOffset));
                }
            }

            return slice;
        }
    }

    /**
     * Converts an x coordinate on the image into the real component of that point
     *
     * @param xPixel
     * @return
     */
    public double getRealComponent(double xPixel) {
        return (reCenter - width / zoom / 2.0) + (xPixel / zoom);
    }

    /**
     * Converts a y coordinate on the image into the imaginary component of that point.
     * Note that the signs are swapped from above because y values get bigger lower on the screen.
     * This is not important for the set (it is symmetrical about the real axis), but it makes more sense for displaying coordinates
     *
     * @param yPixel
     * @return
     */
    public double getImaginaryComponent(double yPixel) {
        return (height / zoom / 2.0 - imCenter) - (yPixel / zoom);
    }

    /**
     * Set this fractal's values to those stored in a FractalState
     * @param state
     */
    private boolean goToState(FractalState state) {
        zoom = state.zoom;
        reCenter = state.reCenter;
        imCenter = state.imCenter;
        zoomProperty.setValue(state.readableZoom);

        if (state.isCompatible(this)) {
            image = state.image;
            imageProperty.setValue(image);
            return true;
        }

        return false;
    }
}
