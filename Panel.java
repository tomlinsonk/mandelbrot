package mandelbrot;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Panel extends JPanel {

	JLabel imageLabel = new JLabel();
	private static final long serialVersionUID = 1L;
	
	Panel() {
		setSize(1440, 800);
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		gbc.fill = GridBagConstraints.BOTH;
		add(imageLabel, gbc);
	}
	
	void setImage(BufferedImage image) {
		imageLabel.setIcon(new ImageIcon(image));
	}
	

}
