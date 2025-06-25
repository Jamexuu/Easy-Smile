package Frames;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class homepage extends JFrame{
    Font btnFont = new Font("Segoe Ui", Font.BOLD, 24);
    Font mainFont = new Font("Segoe Ui", Font.BOLD, 20);

    private String selectedMenu = "Home"; // Track selected menu

    // Helper to create menu label with or without underline
    private JLabel makeMenuLabel(String text, boolean underline) {
        String html = underline
            ? String.format("<html><a style='color:#1167B1;text-decoration:underline;'>%s</a></html>", text)
            : String.format("<html><a style='color:#1167B1;text-decoration:none;'>%s</a></html>", text);
        JLabel lbl = new JLabel(html);
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl.setFont(mainFont);
        return lbl;
    }

    public void home() {
        getContentPane().setBackground(Color.WHITE);

        /* Blue Banner */
        JPanel blueHeaderPanel = new JPanel(new BorderLayout());
        blueHeaderPanel.setBackground(Color.decode("#2A9DF4"));
        blueHeaderPanel.setPreferredSize(new Dimension(1200, 50));
        
        // Sidebar Nav (labels as fields so we can update them)
        JLabel homeLabel = makeMenuLabel("Home", selectedMenu.equals("Home"));
        JLabel servicesLabel = makeMenuLabel("Services Display", selectedMenu.equals("Services Display"));
        JLabel dentistLabel = makeMenuLabel("Dentists Information", selectedMenu.equals("Dentists Information"));
        JLabel accountLabel = makeMenuLabel("Account Information", selectedMenu.equals("Account Information"));
        JLabel clinicLabel = makeMenuLabel("Clinic Information", selectedMenu.equals("Clinic Information"));

        // Mouse listener to update underline on click
        MouseAdapter menuClickHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel clicked = (JLabel) e.getSource();
                // Remove HTML tags to get plain text
                String menuText = clicked.getText().replaceAll("<[^>]*>", "").trim();
                selectedMenu = menuText;

                // Update all menu labels
                homeLabel.setText(makeMenuLabel("Home", selectedMenu.equals("Home")).getText());
                servicesLabel.setText(makeMenuLabel("Services Display", selectedMenu.equals("Services Display")).getText());
                dentistLabel.setText(makeMenuLabel("Dentists Information", selectedMenu.equals("Dentists Information")).getText());
                accountLabel.setText(makeMenuLabel("Accounts", selectedMenu.equals("Account Information")).getText());
                clinicLabel.setText(makeMenuLabel("Clinic Information", selectedMenu.equals("Clinic Information")).getText());

                // Navigation logic
                switch (menuText) {
                    case "Home":
                        // Already on homepage, just refresh
                        break;
                    case "Services Display":
                        dispose();
                        SwingUtilities.invokeLater(() -> {
                            new ServicesDisplay(); // Fixed: Changed to match actual class name
                        });
                        break;
                    case "Dentists Information":
                        dispose();
                        SwingUtilities.invokeLater(() -> {
                            new DentistsInformation().initialize(); // Make sure this class exists
                        });
                        break;
                    case "Account Information":
                        dispose();
                        SwingUtilities.invokeLater(() -> {
                            new AccountManagement().initialize(); // Make sure this class exists
                        });
                        break;
                    case "Clinic Information":
                        dispose();
                        SwingUtilities.invokeLater(() -> {
                            new ClinicInformationFrame(); // Make sure this class exists
                        });
                        break;
                }
            }
        };
        homeLabel.addMouseListener(menuClickHandler);
        servicesLabel.addMouseListener(menuClickHandler);
        dentistLabel.addMouseListener(menuClickHandler);
        accountLabel.addMouseListener(menuClickHandler);
        clinicLabel.addMouseListener(menuClickHandler);

        /* Sidebar Panel */
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.decode("#E1E3E5"));
        sidebarPanel.setPreferredSize(new Dimension(250, 750));
        sidebarPanel.add(homeLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 25))); 
        sidebarPanel.add(servicesLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        sidebarPanel.add(accountLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        sidebarPanel.add(dentistLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        sidebarPanel.add(clinicLabel);
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 0, 20));
        
        /* Logo */
        JLabel logoImage = new JLabel();
        ImageIcon icon = new ImageIcon("client/static/images/EasySmileLogo.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(460, 170, Image.SCALE_SMOOTH));
        logoImage.setIcon(icon);
        /* Buttons */
        JButton patientBtn = makeButton("PATIENT MANAGEMENT");
        patientBtn.addActionListener(e -> {
            dispose(); 
            SwingUtilities.invokeLater(() -> {
                new PatientManagementFrame();
            });
        });
        JButton appointmentBtn = makeButton("APPOINTMENT MANAGEMENT");
        appointmentBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                AppointmentManagementFrame appointmentFrame = new AppointmentManagementFrame();
                appointmentFrame.initialize();
            });
            
        });
        /* Main Content Panel (Logo + Main Buttons)*/
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);
        mainContentPanel.add(logoImage);
        mainContentPanel.add(Box.createRigidArea(new Dimension(1, 100)));
        mainContentPanel.add(patientBtn);
        mainContentPanel.add(Box.createRigidArea(new Dimension(1, 20)));
        mainContentPanel.add(appointmentBtn);
        mainContentPanel.add(Box.createVerticalGlue());
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(100, 0, 50, 0));
        logoImage.setAlignmentX(CENTER_ALIGNMENT);
        patientBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        appointmentBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        /* Main Panel */
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false); 
        mainPanel.add(blueHeaderPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setTitle("Homepage");
        setSize(1200, 800);
        setResizable(true);
        setLocationRelativeTo(null); // Center on screen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Add this line for full screen
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);     
    }
    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(btnFont);
        btn.setBackground(Color.decode("#1167B1"));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension (600, 55));
        btn.setMaximumSize(new Dimension (600, 55));
        btn.setFocusPainted(false);
        return btn;
    }
    public static void main(String[] args) {
        homepage myFrame = new homepage();
        myFrame.home();
    }
}


