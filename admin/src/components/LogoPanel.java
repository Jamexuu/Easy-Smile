package components;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LogoPanel {
    
    public static JPanel create() {
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JLabel logoLabel = new JLabel();
        java.net.URL imgURL = LogoPanel.class.getResource("/images/smalllogonotext.png");
        
        if (imgURL != null) {
            ImageIcon logoIcon = new ImageIcon(imgURL);
            Image image = logoIcon.getImage();
            Image newimg = image.getScaledInstance(250, 75, java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(newimg);
            logoLabel.setIcon(logoIcon);
        } else {
            logoLabel.setText("Logo Missing!");
            logoLabel.setFont(new Font("Segoe Ui", Font.BOLD, 18));
            logoLabel.setForeground(Color.RED);
        }
        
        logoPanel.add(logoLabel);
        logoPanel.setBorder(new EmptyBorder(40, 0, 5, 0));
        return logoPanel;
    }
}
