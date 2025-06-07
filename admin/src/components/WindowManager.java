package components;

import java.awt.*;
import javax.swing.*;

public class WindowManager {
    
    public static void setupLoginWindow(JFrame frame) {
        frame.setTitle("Admin Login");
        frame.setSize(500, 550);
        frame.setMinimumSize(new Dimension(500, 550));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}