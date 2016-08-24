package mandelbrot;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Main {
	static Window frame = new Window();
	
	
	static final double X_SIZE = 3.5;
	static final double Y_SIZE = 2;
	static final double SCREEN_WIDTH = 1920;
	static final double SCREEN_HEIGHT = 1105;
	
	static BufferedImage image = new BufferedImage((int)SCREEN_WIDTH, (int)SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
	
	static int[] pallete = generateColors(1000);


	
	static double zoomLevel = 1;
	static double xCenter = -0.75;
	static double yCenter = 0;
	
	public static void main(String[] args) {
		
		Color myWhite = new Color(255, 255, 255); 
		int rgb = myWhite.getRGB();
		
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				image.setRGB(i, j, rgb);
			}
		}
		
		paintImage(xCenter, yCenter, zoomLevel);
	}
	
	static void paintImage(double xCenter, double yCenter, double zoom) {
		
		double xZoom = X_SIZE / zoom;
		double yZoom = Y_SIZE / zoom;
		
		for (int Px = 0; Px < image.getWidth(); Px++) {
			for (int Py = 0; Py < image.getHeight(); Py++) {
				
				double x0 = xScale(Px, xCenter, xZoom);
				double y0 = yScale(Py, yCenter, yZoom);
				
				double x = 0;
				double y = 0;
				
				// Julia Sets
//				double x0 = 0.0315;
//				double y0 = -0.121;
//				
//				double x = xScale(Px, xCenter, xZoom);
//				double y = yScale(Py, yCenter, yZoom);
//				
				double xSqr = x * x;
				double ySqr = y * y;
				
				double xLast = 0;
				double yLast = 0;
				
				int iteration = 0;
				int maxIteration = 1000;
			
				while (xSqr + ySqr < 4 && iteration < maxIteration) {
					y *= x;
					y += y;
					y += y0;
					x = xSqr - ySqr + x0;
					xSqr = x * x;
					ySqr = y * y;
					
					if (x == xLast && y == yLast) {
						iteration = maxIteration;
//							System.out.println("Saved");
						break;
					}
					
					xLast = x;
					yLast = y;
					iteration++;
				}
				
				
				// Simple
//				image.setRGB(Px, Py, new Color(iteration == maxIteration ? 255 : 0, 
//											   iteration == maxIteration ? 255 : 0, 
//			       							   iteration == maxIteration ? 255 : 0).getRGB());
				
				// Interesting
//				image.setRGB(Px, Py, new Color(iteration == maxIteration ? 255 : (iteration + 70) % 255, 
//											   iteration == maxIteration ? 255 : (iteration + 140) % 255, 
//											   iteration == maxIteration ? 255 : (iteration + 210) % 255).getRGB());
				
				// Proper Rainbow
//				image.setRGB(Px, Py, iteration == maxIteration ? new Color(255, 255, 255).getRGB() : pallete[iteration]);
				
				// Black and white gradients
				image.setRGB(Px, Py, new Color(iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration + 0) % 225)), 
						   					   iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration + 0) % 225)), 
						   					   iteration == maxIteration ? 255 : ((iteration / 255) % 2 == 0 ? (iteration + 0) % 255 : 255 - ((iteration +  0) % 225))).getRGB());
//				
				// Stripes
//				int[] colors = {Color.white.getRGB(), Color.black.getRGB(), Color.red.getRGB()};
//				image.setRGB(Px, Py, colors[iteration % 3]);				
				
				// Black, Red, White
//				int outer = iteration % 255;
//				outer = outer < 50 ? 0 : 255;					
//				image.setRGB(Px, Py, new Color(iteration == maxIteration ? 255 : outer, 
//											   iteration == maxIteration ? 255 : 0, 
//										       iteration == maxIteration ? 255 : 0).getRGB());


			}
		}
		frame.setTitle("Zoom: " + zoomLevel);
		frame.panel.setImage(image);
	}
	
	static double xScale(int x, double xCenter, double zoom) {
		double fraction = x / SCREEN_WIDTH;
		return (xCenter - zoom / 2.0) + (fraction * zoom);
	}
	
	static double yScale(int y, double yCenter, double zoom) {
		double fraction = y / SCREEN_HEIGHT;
		return (yCenter - zoom / 2.0) + (fraction * zoom);
	}
	
	static int[] generateColors(int n) {
		int[] cols = new int[n];
		for(int i = 0; i < n; i++) {
			cols[i] = Color.getHSBColor((float) i / (float) n, 0.85f, (float) i*30 / (float) n).getRGB();
		}
		return cols;
	}
}
