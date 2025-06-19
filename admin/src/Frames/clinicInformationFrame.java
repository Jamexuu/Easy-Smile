package Frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import DAO.ClinicDAO;
import DAO.ClinicDAO.ClinicInfo;

public class ClinicInformationFrame extends JFrame {
    // Constants
    private static final Font BTN_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Color BLUE_COLOR = Color.decode("#1167B1");
    private static final Color LIGHT_BLUE_COLOR = Color.decode("#2A9DF4");
    private static final Color SIDEBAR_COLOR = Color.decode("#D0EFFF");
    private static final Color BACKGROUND_COLOR = Color.decode("#E1E3E5");
    
    // DAO and Data
    private ClinicDAO clinicDAO;
    private ClinicInfo currentClinicInfo;
    private java.util.Map<String, JTextField> fieldInputs = new java.util.HashMap<>();
    private String selectedMenu = "Clinic Information";
    
    // UI Components
    private JButton actionBtn;
    private boolean isEditing = false;

    private boolean isInitialized = false;

    public ClinicInformationFrame() {
        clinicDAO = new ClinicDAO();
        initialize();
        loadClinicData();
    }

    public void initialize() {
        getContentPane().setBackground(Color.WHITE);

        if (isInitialized) {
            return;
        }

        isInitialized = true;

        getContentPane().removeAll();

        getContentPane().setBackground(Color.decode("#E1E3E5"));

        createComponents();
        
        setTitle("Clinic Information");
        setSize(1200, 800);
        setResizable(true);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createSidebarPanel(), BorderLayout.WEST);
        mainPanel.add(createMainContentPanel(), BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JButton menuBtn = makeButton("Menu");
        menuBtn.addActionListener(e -> toggleSidebar());

        JPanel blueHeaderPanel = new JPanel(new BorderLayout());
        blueHeaderPanel.setBackground(LIGHT_BLUE_COLOR);
        blueHeaderPanel.setPreferredSize(new Dimension(1200, 50));
        blueHeaderPanel.add(menuBtn, BorderLayout.WEST);
        blueHeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        return blueHeaderPanel;
    }
    
    private JPanel createSidebarPanel() {
        JLabel homeLabel = makeMenuLabel("Home", selectedMenu.equals("Home"));
        JLabel servicesLabel = makeMenuLabel("Services Display", selectedMenu.equals("Services Display"));
        JLabel dentistLabel = makeMenuLabel("Dentists Information", selectedMenu.equals("Dentists Information"));
        JLabel accountLabel = makeMenuLabel("Account Information", selectedMenu.equals("Account Information"));
        JLabel clinicLabel = makeMenuLabel("Clinic Information", selectedMenu.equals("Clinic Information"));

        MouseAdapter menuClickHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel clicked = (JLabel) e.getSource();
                String menuText = clicked.getText().replaceAll("<[^>]*>", "").trim();
                selectedMenu = menuText;

                updateMenuLabels(homeLabel, servicesLabel, dentistLabel, accountLabel, clinicLabel);
                handleNavigation(menuText);
            }
        };
        
        homeLabel.addMouseListener(menuClickHandler);
        servicesLabel.addMouseListener(menuClickHandler);
        dentistLabel.addMouseListener(menuClickHandler);
        accountLabel.addMouseListener(menuClickHandler);
        clinicLabel.addMouseListener(menuClickHandler);

        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(BACKGROUND_COLOR);
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
        
        return sidebarPanel;
    }
    
    private JPanel createMainContentPanel() {
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        
        // Logo at the top - REDUCE HEIGHT to move form up
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        
        JLabel logoImage = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/images/smalllogonotext.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            icon = new ImageIcon(icon.getImage().getScaledInstance(250, 70, Image.SCALE_SMOOTH)); // Smaller height
            logoImage.setIcon(icon);
        } else {
            logoImage.setText("Logo");
        }
        
        logoPanel.add(logoImage);
        // Add less padding to logo panel
        logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // Reduced top padding
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        
        // Center panel with the form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        
        // Create the form
        JPanel formPanel = createFormPanel();
        
        // Add the form to the center panel using GridBagLayout for centering
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH; // CHANGED: Anchor to NORTH instead of CENTER
        gbc.insets = new Insets(30, 0, 0, 0); // ADDED: Negative top inset to push upwards
        centerPanel.add(formPanel, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add bottom panel with empty space to push content up
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(1, 100)); // Add space at bottom
        bottomPanel.setOpaque(false);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }

    private JPanel createFormPanel() {
        // Form panel with light blue background
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.decode("#D0EFFF")); // Light blue color from ServicesDisplay
        formPanel.setOpaque(true);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Title
        JLabel titleLabel = new JLabel("Clinic Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.decode("#192F8F"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Form fields
        String[] fieldLabels = {"Phone Number", "Email Address", "Location", "Facebook Link", "Instagram Link"};
        for (String label : fieldLabels) {
            JLabel fieldLabel = new JLabel(label);
            fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            JTextField textField = new JTextField();
            textField.setMaximumSize(new Dimension(380, 36));
            textField.setPreferredSize(new Dimension(380, 36));
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            textField.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            fieldInputs.put(label, textField);
            formPanel.add(fieldLabel);
            formPanel.add(Box.createVerticalStrut(6));
            formPanel.add(textField);
            formPanel.add(Box.createVerticalStrut(18));
        }
        
        // Edit button positioned right after the last field (moved up)
        formPanel.add(Box.createVerticalStrut(10)); // Just a bit of spacing
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        
        actionBtn = new JButton("Edit");
        actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        actionBtn.setBackground(Color.decode("#1167B1"));
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setPreferredSize(new Dimension(120, 40));
        actionBtn.addActionListener(this::handleActionButton);
        
        buttonPanel.add(actionBtn);
        formPanel.add(buttonPanel);
        
        return formPanel;
    }
    
    // Helper to create menu label with or without underline
    private JLabel makeMenuLabel(String text, boolean underline) {
        String html = underline
            ? String.format("<html><a style='color:#1167B1;text-decoration:underline;'>%s</a></html>", text)
            : String.format("<html><a style='color:#1167B1;text-decoration:none;'>%s</a></html>", text);
        JLabel lbl = new JLabel(html);
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl.setFont(MAIN_FONT);
        return lbl;
    }

    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BTN_FONT);
        btn.setBackground(BLUE_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setMaximumSize(new Dimension(110, 30));
        btn.setMargin(new Insets(2, 2, 2, 2));
        btn.setFocusPainted(false);
        return btn;
    }
    
    // DAO Methods
    private void loadClinicData() {
        SwingUtilities.invokeLater(() -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                currentClinicInfo = clinicDAO.getClinicInfo();
                if (currentClinicInfo != null) {
                    populateFields();
                } else {
                    // Create default clinic info if none exists
                    currentClinicInfo = new ClinicInfo();
                    clearFields();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading clinic information: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                currentClinicInfo = new ClinicInfo();
                clearFields();
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }
    
    private void populateFields() {
        if (currentClinicInfo != null) {
            fieldInputs.get("Phone Number").setText(currentClinicInfo.getPhoneNumber() != null ? currentClinicInfo.getPhoneNumber() : "");
            fieldInputs.get("Email Address").setText(currentClinicInfo.getEmailAddress() != null ? currentClinicInfo.getEmailAddress() : "");
            fieldInputs.get("Location").setText(currentClinicInfo.getLocation() != null ? currentClinicInfo.getLocation() : "");
            fieldInputs.get("Facebook Link").setText(currentClinicInfo.getFacebookLink() != null ? currentClinicInfo.getFacebookLink() : "");
            fieldInputs.get("Instagram Link").setText(currentClinicInfo.getInstagramLink() != null ? currentClinicInfo.getInstagramLink() : "");
        }
    }
    
    private void clearFields() {
        for (JTextField field : fieldInputs.values()) {
            field.setText("");
        }
    }
    
    private void saveClinicInfo() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // Update current clinic info with field values
            currentClinicInfo.setPhoneNumber(fieldInputs.get("Phone Number").getText().trim());
            currentClinicInfo.setEmailAddress(fieldInputs.get("Email Address").getText().trim());
            currentClinicInfo.setLocation(fieldInputs.get("Location").getText().trim());
            currentClinicInfo.setFacebookLink(fieldInputs.get("Facebook Link").getText().trim());
            currentClinicInfo.setInstagramLink(fieldInputs.get("Instagram Link").getText().trim());
            
            boolean success;
            if (currentClinicInfo.getClinicId() == null || currentClinicInfo.getClinicId().isEmpty()) {
                // Create new clinic info
                success = clinicDAO.addClinicInfo(currentClinicInfo);
            } else {
                // Update existing clinic info
                success = clinicDAO.updateClinicInfo(currentClinicInfo);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Clinic information saved successfully!", 
                    "Save Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadClinicData(); // Reload to get updated data
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save clinic information.", 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving clinic information: " + e.getMessage(), 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    // Event Handlers
    private void handleActionButton(ActionEvent e) {
        if (!isEditing) {
            // Switch to edit mode
            setFieldsEditable(true);
            actionBtn.setText("Apply Changes");
            isEditing = true;
        } else {
            // Validate and save
            if (validateFields()) {
                saveClinicInfo();
                setFieldsEditable(false);
                actionBtn.setText("Edit");
                isEditing = false;
            }
        }
    }
    
    private boolean validateFields() {
        String phoneNumber = fieldInputs.get("Phone Number").getText().trim();
        String emailAddress = fieldInputs.get("Email Address").getText().trim();
        String location = fieldInputs.get("Location").getText().trim();
        
        if (phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Phone Number is required.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Phone Number").requestFocus();
            return false;
        }
        
        if (emailAddress.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Email Address is required.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Email Address").requestFocus();
            return false;
        }
        
        if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Location is required.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Location").requestFocus();
            return false;
        }
        
        // Validate email format
        if (!emailAddress.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid email address.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Email Address").requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void setFieldsEditable(boolean editable) {
        for (JTextField field : fieldInputs.values()) {
            field.setEditable(editable);
            field.setBackground(editable ? Color.WHITE : Color.decode("#F5F5F5"));
        }
    }
    
    private void toggleSidebar() {
        Component sidebar = null;
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component subComp : panel.getComponents()) {
                    if (subComp instanceof JPanel && ((JPanel) subComp).getPreferredSize().width == 250) {
                        sidebar = subComp;
                        break;
                    }
                }
            }
        }
        
        if (sidebar != null) {
            sidebar.setVisible(!sidebar.isVisible());
            sidebar.revalidate();
            sidebar.repaint();
        }
    }
    
    private void updateMenuLabels(JLabel homeLabel, JLabel servicesLabel, JLabel dentistLabel, 
                                 JLabel accountLabel, JLabel clinicLabel) {
        homeLabel.setText(makeMenuLabel("Home", selectedMenu.equals("Home")).getText());
        servicesLabel.setText(makeMenuLabel("Services Display", selectedMenu.equals("Services Display")).getText());
        dentistLabel.setText(makeMenuLabel("Dentists Information", selectedMenu.equals("Dentists Information")).getText());
        accountLabel.setText(makeMenuLabel("Account Information", selectedMenu.equals("Account Information")).getText());
        clinicLabel.setText(makeMenuLabel("Clinic Information", selectedMenu.equals("Clinic Information")).getText());
    }
    
    private void handleNavigation(String menuText) {
        if (isEditing) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "You have unsaved changes. Do you want to save before leaving?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (validateFields()) {
                    saveClinicInfo();
                } else {
                    return; // Don't navigate if validation fails
                }
            } else if (confirm == JOptionPane.CANCEL_OPTION) {
                return; // Don't navigate
            }
        }
        
        switch (menuText) {
            case "Home":
                dispose();
                SwingUtilities.invokeLater(() -> {
                    homepage homepageInfo = new homepage();
                    homepageInfo.home();
                });
                break;
            case "Services Display":
                dispose();
                SwingUtilities.invokeLater(() -> {
                    ServicesDisplay servicesInfo = new ServicesDisplay();
                    servicesInfo.services();
                });
                break;
            case "Dentists Information":
                dispose();
                SwingUtilities.invokeLater(() -> {
                    DentistsInformation dentistInfo = new DentistsInformation();
                    dentistInfo.initialize();
                });
                break;
            case "Account Information":
                dispose();
                SwingUtilities.invokeLater(() -> {
                    AccountManagement accountInfo = new AccountManagement();
                    accountInfo.initialize();
                });
                break;
            case "Clinic Information":
                // Already on this page, do nothing
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClinicInformationFrame();
        });
    }
}