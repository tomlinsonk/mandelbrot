//package mandelbrot;
//
//import java.awt.KeyEventDispatcher;
//import java.awt.KeyboardFocusManager;
//import java.awt.event.KeyEvent;
//
//import javax.swing.JFrame;
//
//public class Window extends JFrame {
//
//	Panel panel = new Panel();
//	private static final long serialVersionUID = 1L;
//
//	Window() {
//		setSize((int)Main.SCREEN_WIDTH, (int)Main.SCREEN_HEIGHT);
//		setResizable(false);
//		setDefaultCloseOperation(EXIT_ON_CLOSE);
//		setVisible(true);
//		setContentPane(panel);
//
//		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
//            @Override
//            public boolean dispatchKeyEvent(KeyEvent e) {
//            	 int keyEventType = e.getID();
//                 if (keyEventType == KeyEvent.KEY_TYPED) {
//                	 char key = e.getKeyChar();
//                	 if (key == '=') {
//                		 Main.zoomLevel *= 2;
//                		 Main.paintImage(Main.xCenter, Main.yCenter, Main.zoomLevel);
//                	 } else if (key == '-') {
//                		 Main.zoomLevel /= 2.0;
//                		 Main.paintImage(Main.xCenter, Main.yCenter, Main.zoomLevel);
//                	 } else if (key == 'd') {
//                		 Main.xCenter += Main.X_SIZE / Main.zoomLevel / 10.0;
//                		 Main.paintImage(Main.xCenter, Main.yCenter, Main.zoomLevel);
//                	 } else if (key == 'w') {
//                		 Main.yCenter -= Main.Y_SIZE / Main.zoomLevel / 10.0;
//                		 Main.paintImage(Main.xCenter, Main.yCenter, Main.zoomLevel);
//                	 } else if (key == 's') {
//                		 Main.yCenter += Main.Y_SIZE / Main.zoomLevel / 10.0;
//                		 Main.paintImage(Main.xCenter, Main.yCenter, Main.zoomLevel);
//                	 } else if (key == 'a') {
//                		 Main.xCenter -= Main.X_SIZE / Main.zoomLevel / 10.0;
//                		 Main.paintImage(Main.xCenter, Main.yCenter, Main.zoomLevel);
//                	 }
//                 }
//            	return false;
//            }
//        });
//	}
//}
