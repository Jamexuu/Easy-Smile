package Frames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import DAO.ClinicDAO;
import DAO.ClinicDAO.ClinicInfo;

public class clinicInformationFrame extends JFrame {
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

    public clinicInformationFrame() {
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
        JLabel logoImage = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/images/smalllogonotext.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            icon = new ImageIcon(icon.getImage().getScaledInstance(250, 90, Image.SCALE_SMOOTH));
            logoImage.setIcon(icon);
        } else {
            logoImage.setText("Logo");
        }

        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);
        mainContentPanel.add(logoImage);
        mainContentPanel.add(Box.createRigidArea(new Dimension(1, 20)));
        mainContentPanel.add(createContentPanel());
        mainContentPanel.add(Box.createVerticalGlue());
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        logoImage.setAlignmentX(CENTER_ALIGNMENT);
        
        return mainContentPanel;
    }
    
    private JPanel createContentPanel() {
        JPanel recordPanel = new JPanel(new BorderLayout());
        recordPanel.setBackground(SIDEBAR_COLOR);
        recordPanel.setPreferredSize(new Dimension(500, 600));
        recordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, Color.WHITE),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel clinicInfoLabel = new JLabel("Clinic Information");
        clinicInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        clinicInfoLabel.setForeground(Color.decode("#192F8F"));

        Box fieldsBox = Box.createVerticalBox();
        fieldsBox.add(clinicInfoLabel);
        fieldsBox.add(Box.createVerticalStrut(15));
        
        createInputFields(fieldsBox);
        
        recordPanel.add(fieldsBox, BorderLayout.CENTER);
        recordPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(recordPanel, BorderLayout.CENTER);
        contentPanel.setPreferredSize(new Dimension(500, 600));
        
        return contentPanel;
    }
    
    private void createInputFields(Box container) {
        String[] fieldLabels = {"Phone Number", "Email Address", "Location", "Facebook Link", "Instagram Link"};
        
        for (String label : fieldLabels) {
            JPanel fieldPanel = new JPanel();
            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
            fieldPanel.setOpaque(false);

            JLabel fieldLabel = new JLabel(label);
            fieldLabel.setFont(LABEL_FONT);
            fieldLabel.setForeground(Color.decode("#192F8F"));

            JTextField textField = new JTextField();
            textField.setFont(MAIN_FONT);
            textField.setMaximumSize(new Dimension(1000, 35));
            textField.setBackground(Color.WHITE);
            textField.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));
            textField.setEditable(false);

            fieldInputs.put(label, textField);

            fieldPanel.add(fieldLabel);
            fieldPanel.add(textField);
            container.add(fieldPanel);
            container.add(Box.createVerticalStrut(8));
        }
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 50));
        buttonPanel.setOpaque(false);
        
        actionBtn = new JButton("Edit");
        actionBtn.setFont(BTN_FONT);
        actionBtn.setBackground(BLUE_COLOR);
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setOpaque(true);
        actionBtn.setBorderPainted(false);
        actionBtn.setFocusPainted(false);
        actionBtn.setPreferredSize(new Dimension(140, 36));
        actionBtn.addActionListener(this::handleActionButton);
        
        buttonPanel.add(actionBtn);
        return buttonPanel;
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
            new clinicInformationFrame();
        });
    }
}