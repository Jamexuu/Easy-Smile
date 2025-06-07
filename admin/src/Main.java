import java.awt.*;
import javax.swing.*;

import DataBase.DBConnector;
import Frames.LoginFrame;
import components.LogoPanel;
import components.WindowManager;

public class Main extends JFrame {
    
    public void initialize() {
        LoginFrame loginHandler = new LoginFrame(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.add(LogoPanel.create(), BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(loginHandler.createLoginForm());
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(loginHandler.createButtonsPanel());
        centerPanel.add(Box.createVerticalGlue());

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(centerPanel);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        add(mainPanel);
        
        WindowManager.setupLoginWindow(this);
    }

    public static void main(String[] args) {
    // Test database connection first
    if (DBConnector.testConnection()) {
        System.out.println("The database connection is successful!");
        SwingUtilities.invokeLater(() -> new Main().initialize());
    } else {
        System.err.println("Cannot start application - Database connection failed!");
    }
}
}