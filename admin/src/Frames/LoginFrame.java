package Frames;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;
import DAO.adminDAO; // Import the DAO
import main.Main;

public class LoginFrame {
    private final Font mainFont = new Font("Segoe Ui", Font.BOLD, 18);
    private final Font placeholderFont = new Font("Segoe Ui", Font.PLAIN, 16);
    
    private JTextField tfEmail;
    private JPasswordField tfPassword;
    private JFrame parentFrame;
    
    public LoginFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
    
    /**
     * Creates and returns the login form panel
     */
    public JPanel createLoginForm() {
        JLabel lbEmail = new JLabel("Email");
        lbEmail.setFont(mainFont);
        lbEmail.setBounds(100, -10, 100, 30);

        tfEmail = new JTextField();
        tfEmail.setFont(mainFont);
        tfEmail.setBounds(100, 20, 250, 30);
        setPlaceholder(tfEmail, "Enter your email");

        JLabel lbPassword = new JLabel("Password");
        lbPassword.setFont(mainFont);
        lbPassword.setBounds(100, 70, 100, 30);

        tfPassword = new JPasswordField();
        tfPassword.setFont(mainFont);
        tfPassword.setBounds(100, 100, 250, 30);
        setPlaceholder(tfPassword, "Enter your password");

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setPreferredSize(new Dimension(450, 160));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(lbEmail);
        formPanel.add(tfEmail);
        formPanel.add(lbPassword);
        formPanel.add(tfPassword);

        return formPanel;
    }
    
    /**
     * Creates and returns the buttons panel
     */
    public JPanel createButtonsPanel() {
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(mainFont);
        btnLogin.setBackground(new Color(26, 40, 126));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setBounds(80, 0, 120, 35);
        btnLogin.addActionListener(new LoginActionListener());

        JButton btnClear = new JButton("Clear");
        btnClear.setFont(mainFont);
        btnClear.setBackground(new Color(26, 40, 126));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        btnClear.setBounds(250, 0, 120, 35);
        btnClear.addActionListener(new ClearActionListener());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(null);
        buttonsPanel.setPreferredSize(new Dimension(450, 55));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.add(btnLogin);
        buttonsPanel.add(btnClear);
        buttonsPanel.setLocation(0, 50);

        return buttonsPanel;
    }
    
    /**
     * Helper method to set placeholder text
     */
    private void setPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.LIGHT_GRAY);
        field.setFont(placeholderFont);
        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setFont(mainFont);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('*');
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.LIGHT_GRAY);
                    field.setFont(placeholderFont);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
    }
    
    /**
     * Inner class for login button action
     */
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = tfEmail.getText();
            String password = new String(tfPassword.getPassword());
            
            // Skip validation if placeholder text is still showing
            if (email.equals("Enter your email") || password.equals("Enter your password")) {
                JOptionPane.showMessageDialog(parentFrame, "Please enter your credentials", "Login Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                // Use DAO for validation
                if (adminDAO.validateCredentials(email, password)) {
                    JOptionPane.showMessageDialog(parentFrame, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Navigate to homepage - Fixed: Check if parent is Main class
                    if (parentFrame instanceof Main) {
                        ((Main) parentFrame).navigateToHomepage();
                    } else {
                        // Fallback: Direct navigation
                        parentFrame.dispose();
                        SwingUtilities.invokeLater(() -> {
                            new homepage().home();
                        });
                    }
                } else {
                    // Use DAO for specific error message
                    String errorMessage = adminDAO.getLoginError(email, password);
                    JOptionPane.showMessageDialog(parentFrame, errorMessage, "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Login error: " + ex.getMessage(), 
                    "System Error", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Inner class for clear button action
     */
    private class ClearActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            tfEmail.setText("");
            tfPassword.setText("");
            setPlaceholder(tfEmail, "Enter your email");
            setPlaceholder(tfPassword, "Enter your password");
        }
    }
}