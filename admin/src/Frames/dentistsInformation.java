package Frames;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class dentistsInformation extends JFrame {
    Font btnFont = new Font("Segoe Ui", Font.BOLD, 14);
    Font mainFont = new Font("Segoe Ui", Font.BOLD, 20);

    private java.util.Map<String, JComponent> fieldInputs = new java.util.HashMap<>();
    private String selectedMenu = "Dentists Information"; // Track selected menu

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
    // Helper class to limit JTextArea input length
    static class JTextAreaLimit extends PlainDocument {
        private final int limit;
        JTextAreaLimit(int limit) {
            super();
            this.limit = limit;
        }
        @Override
        public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }
    public void initialize() {
        getContentPane().setBackground(Color.WHITE);

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
                accountLabel.setText(makeMenuLabel("Account Information", selectedMenu.equals("Account Information")).getText());
                clinicLabel.setText(makeMenuLabel("Clinic Information", selectedMenu.equals("Clinic Information")).getText());

            // Navigation logic
            switch (menuText) {
                case "Home":
                    homepage homepageInfo = new homepage();
                    homepageInfo.home();
                    dispose();
                    break;
                case "Services Display":
                    servicesDisplay servicesInfo = new servicesDisplay();
                    servicesInfo.services();
                    dispose();
                    break;
                case "Dentists Information":
                    dentistsInformation dentistInfo = new dentistsInformation();
                    dentistInfo.initialize();
                    dispose();
                    break;
                case "Account Information":
                    // Account Management
                    break;
                case "Clinic Information":
                    clinicInformationFrame clinicInfo = new clinicInformationFrame();
                    clinicInfo.initialize();
                    dispose();
                    break;
                }
            }
        };
        homeLabel.addMouseListener(menuClickHandler);
        servicesLabel.addMouseListener(menuClickHandler);
        dentistLabel.addMouseListener(menuClickHandler);
        accountLabel.addMouseListener(menuClickHandler);
        clinicLabel.addMouseListener(menuClickHandler);

        // Sidebar Panel
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.decode("#E1E3E5"));
        sidebarPanel.setPreferredSize(new Dimension(250, 750));
        sidebarPanel.add(homeLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        sidebarPanel.add(servicesLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        sidebarPanel.add(dentistLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        sidebarPanel.add(accountLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        sidebarPanel.add(clinicLabel);
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 0, 20));
        sidebarPanel.setVisible(false);

        // Menu Button
        JButton menuBtn = makeButton("Menu");
        menuBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sidebarPanel.setVisible(!sidebarPanel.isVisible());
                sidebarPanel.revalidate();
                sidebarPanel.repaint();
            }
        });

        // Blue Banner
        JPanel blueHeaderPanel = new JPanel(new BorderLayout());
        blueHeaderPanel.setBackground(Color.decode("#2A9DF4"));
        blueHeaderPanel.setPreferredSize(new Dimension(1200, 50));
        blueHeaderPanel.add(menuBtn, BorderLayout.WEST);
        blueHeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Logo and Admin label row ---
        JLabel logoImage = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/images/smalllogonotext.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            icon = new ImageIcon(icon.getImage().getScaledInstance(250, 90, Image.SCALE_SMOOTH));
            logoImage.setIcon(icon);
        } else {
            logoImage.setText("Logo");
        }
        
        JLabel logoAdmin = new JLabel("Admin");
        logoAdmin.setFont(new Font("Segoe Ui", Font.BOLD, 28));
        logoAdmin.setForeground(Color.decode("#192F8F"));
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.add(logoImage, BorderLayout.WEST);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 10, 10));

        // Content for Field Container Pane
        String[] fieldLabels = {"Title", "First Name", "Middle Name", "Last Name", "Age", "Bio"};
        JPanel fieldsContainer = new JPanel();
        fieldsContainer.setLayout(new BoxLayout(fieldsContainer, BoxLayout.Y_AXIS));
        fieldsContainer.setOpaque(false);
        fieldsContainer.setPreferredSize(new Dimension(500, 600));
        fieldsContainer.setMaximumSize(new Dimension(500, 600));

        for (String label : fieldLabels) {
            JPanel fieldPanel = new JPanel();
            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
            fieldPanel.setOpaque(false);

            JLabel fieldLabel = new JLabel(label);
            fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            fieldLabel.setForeground(Color.decode("#192F8F"));

            if (label.equals("Bio")) {
                JTextArea bioArea = new JTextArea(3, 20);
                bioArea.setFont(new Font("Segoe UI", Font.BOLD, 20));
                bioArea.setLineWrap(true);
                bioArea.setWrapStyleWord(true);
                bioArea.setBackground(Color.WHITE);
                bioArea.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));
                bioArea.setEditable(false); // or true if you want user input
                bioArea.setAlignmentX(Component.LEFT_ALIGNMENT);
                bioArea.setPreferredSize(new Dimension(400, 80));
                bioArea.setMaximumSize(new Dimension(1000, 120));
                // Limit to 500 characters
                bioArea.setDocument(new JTextAreaLimit(500));

                fieldInputs.put(label, bioArea);
                fieldPanel.add(fieldLabel);
                fieldPanel.add(bioArea);
            } else {
                JTextField textField = new JTextField();
                textField.setFont(new Font("Segoe UI", Font.BOLD, 20));
                textField.setBackground(Color.WHITE);
                textField.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));
                textField.setEditable(true);
                textField.setAlignmentX(Component.LEFT_ALIGNMENT);
                textField.setMaximumSize(new Dimension(1000, 35));
                textField.setPreferredSize(new Dimension(400, 35));

                fieldInputs.put(label, textField);
                fieldPanel.add(fieldLabel);
                fieldPanel.add(textField);
            }
            fieldsContainer.add(fieldPanel);
            fieldsContainer.add(Box.createVerticalStrut(8));
        }

        // Record Panel
        JPanel recordPanel = new JPanel(new BorderLayout());
        recordPanel.setBackground(Color.decode("#D0EFFF"));
        recordPanel.setPreferredSize(new Dimension(260, 0));
        recordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, Color.decode("#FFFFFF")),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Top label
        JLabel dentistsInfoLabel = new JLabel("Dentists Information");
        dentistsInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dentistsInfoLabel.setForeground(Color.decode("#192F8F"));
        dentistsInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fields container (vertical box)
        Box fieldsBox = Box.createVerticalBox();
        fieldsBox.add(dentistsInfoLabel);
        fieldsBox.add(Box.createVerticalStrut(15));
        fieldsBox.add(fieldsContainer);

        // Add fields to CENTER
        recordPanel.add(fieldsBox, BorderLayout.CENTER);

        // Bottom right Save button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        buttonPanel.setOpaque(false);
        JButton actionBtn = new JButton("Apply Changes");
        actionBtn.setFont(btnFont);
        actionBtn.setBackground(Color.decode("#1167B1"));
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setOpaque(true);
        actionBtn.setBorderPainted(false);
        actionBtn.setFocusPainted(false);
        actionBtn.setPreferredSize(new Dimension(140, 36));
        buttonPanel.add(actionBtn);

        // Add button panel to SOUTH
        recordPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(500, 600));
        rightPanel.setMaximumSize(new Dimension(500, 600));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.add(recordPanel);
        rightPanel.setOpaque(false);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(rightPanel, BorderLayout.CENTER);
        contentPanel.setPreferredSize(new Dimension(500, 600));
        contentPanel.setMaximumSize(new Dimension(500, 600));

        // Main Content Panel
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);
        mainContentPanel.add(logoImage);
        mainContentPanel.add(Box.createRigidArea(new Dimension(1, 20)));
        mainContentPanel.add(contentPanel);
        mainContentPanel.add(Box.createVerticalGlue());
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        logoImage.setAlignmentX(CENTER_ALIGNMENT);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.add(blueHeaderPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setTitle("Dentists Information");
        setSize(1200, 800);
        setResizable(true);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(btnFont);
        btn.setBackground(Color.decode("#1167B1"));
        btn.setForeground(Color.WHITE);
        btn.setMaximumSize(new Dimension(110, 30));
        btn.setMargin(new Insets(2, 2, 2, 2));
        btn.setFocusPainted(false);
        return btn;
    }

    public static void main(String[] args) {
        dentistsInformation myFrame = new dentistsInformation();
        myFrame.initialize();
    }
}