import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder; 

public class Main extends JFrame {
    final private Font mainFont = new Font("Segoe Ui", Font.BOLD, 18);
    final private Font placeholderFont = new Font("Segoe Ui", Font.PLAIN, 16); // Smaller, italic font for placeholders
    JTextField tfEmail;
    JPasswordField tfPassword;

    public void initialize() {
         /********** Logo Panel **********/
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Remove extra vertical padding

        JLabel logoLabel = new JLabel();
        java.net.URL imgURL = getClass().getResource("/images/smalllogonotext.png");
        System.out.println("Image URL: " + imgURL); // Debug print
        if (imgURL != null) {
            ImageIcon logoIcon = new ImageIcon(imgURL);
            Image image = logoIcon.getImage();
            Image newimg = image.getScaledInstance(250, 75, java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(newimg);
            logoLabel.setIcon(logoIcon);
        } else {
            logoLabel.setText("Logo Missing!");
            logoLabel.setFont(mainFont);
            logoLabel.setForeground(Color.RED);
        }
        logoPanel.add(logoLabel);
        // Add bottom margin to logoPanel
        logoPanel.setBorder(new EmptyBorder(40, 0, 5, 0));


        /********** Form Panel **********/
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
        formPanel.setLayout(null); // Use absolute positioning
        formPanel.setPreferredSize(new Dimension(450, 160)); 
        formPanel.setBackground(Color.WHITE);

        formPanel.add(lbEmail);
        formPanel.add(tfEmail);
        formPanel.add(lbPassword);
        formPanel.add(tfPassword);

        /********** Button Panel **********/
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(mainFont);
        btnLogin.setBackground(new Color(26, 40, 126));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setBounds(80, 0, 120, 35); 
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = new String(tfPassword.getPassword()); // Use getPassword() for JPasswordField
                JOptionPane.showMessageDialog(Main.this, "Email: " + email + "\nPassword: " + password, "Login Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton btnClear = new JButton("Clear");
        btnClear.setFont(mainFont);
        btnClear.setBackground(new Color(26, 40, 126));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        btnClear.setBorderPainted(false);
        
        btnClear.setBounds(250, 0, 120, 35); 
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfEmail.setText("");
                tfPassword.setText("");
                // Reset placeholders after clearing
                setPlaceholder(tfEmail, "Enter your email");
                setPlaceholder(tfPassword, "Enter your password");
            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(null); // No layout manager
        buttonsPanel.setPreferredSize(new Dimension(450, 55)); 
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.add(btnLogin);
        buttonsPanel.add(btnClear);
        // Move the buttonsPanel upwards by adjusting its location
        buttonsPanel.setLocation(0, 50); 

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE); 

        mainPanel.add(logoPanel, BorderLayout.NORTH); // <-- Add this line to show the logo panel
        // Create a panel to center the form and buttons vertically and horizontally
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // Add vertical glue before and after to center content vertically
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(formPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space between form and buttons
        centerPanel.add(buttonsPanel);
        centerPanel.add(Box.createVerticalGlue());

        // Wrap centerPanel in another panel to center horizontally
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(Color.WHITE);
        centerWrapper.add(centerPanel);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        add(mainPanel);

        setTitle("Admin Login");
        setSize(500, 550); 
        setMinimumSize(new Dimension(500, 550));
        setResizable(false); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setVisible(true);
    }

    // Helper method to set placeholder text for JTextField and JPasswordField
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
                        ((JPasswordField) field).setEchoChar('*'); // Set back to password char
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
                        ((JPasswordField) field).setEchoChar((char) 0); // Hide password char when placeholder
                    }
                }
            }
        });
    }
    public static void main(String[] args) {
         SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main myFrame = new Main();
                myFrame.initialize();
            }
        });
    }
}