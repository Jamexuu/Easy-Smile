package Frames;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

import DAO.DentistDAO;
import DAO.DentistDAO.Dentist;

public class DentistsInformation extends JFrame {
    // Constants
    private static final Font BTN_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Color BLUE_COLOR = Color.decode("#1167B1");
    private static final Color LIGHT_BLUE_COLOR = Color.decode("#2A9DF4");
    private static final Color SIDEBAR_COLOR = Color.decode("#D0EFFF");
    private static final Color BACKGROUND_COLOR = Color.decode("#E1E3E5");

    // DAO and Data
    private DentistDAO dentistDAO;
    private List<Dentist> dentistsList;
    private Dentist currentDentist;
    private int currentDentistIndex = -1;
    private String selectedImagePath = null;
    private boolean isEditing = false;

    // UI Components
    private java.util.Map<String, JComponent> fieldInputs = new java.util.HashMap<>();
    private String selectedMenu = "Dentists Information";
    private JButton actionBtn;
    private JButton prevBtn;
    private JButton nextBtn;
    private JButton selectImageBtn;
    private JLabel recordCounter;
    private JPanel sidebarPanel;

    private boolean isInitialized = false;

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

    public DentistsInformation() {
        dentistDAO = new DentistDAO();
        dentistsList = new ArrayList<>();
        initialize();
        loadDentistData();
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
        
        setTitle("Dentists Information");
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

        sidebarPanel = new JPanel();
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

        mainContentPanel.add(Box.createVerticalStrut(10)); // Small top margin
        mainContentPanel.add(logoImage);
        mainContentPanel.add(Box.createVerticalStrut(15)); // Small gap between logo and form (adjust as needed)
        mainContentPanel.add(createContentPanel());
        // Do NOT add Box.createVerticalGlue() or large struts after this

        logoImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        return mainContentPanel;
    }

    // 1. Add ScrollPane to ensure everything is visible
    private JPanel createContentPanel() {
        JPanel mainContentPanel = new JPanel(new GridBagLayout());
        mainContentPanel.setOpaque(false);

        JPanel coloredPanel = new JPanel();
        coloredPanel.setBackground(Color.decode("#D0EFFF"));
        coloredPanel.setLayout(new BoxLayout(coloredPanel, BoxLayout.Y_AXIS));
        coloredPanel.setOpaque(true);
        
        // Don't set preferred size - let it determine by content
        // coloredPanel.setPreferredSize(new Dimension(650, 750));
        
        coloredPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 80, 40)); // Much more bottom padding

        // Add form panel to a scroll pane to ensure everything is visible
        JScrollPane scrollPane = new JScrollPane(createFormPanel());
        scrollPane.setBorder(null); // Remove border
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        coloredPanel.add(scrollPane);

        // GridBagConstraints to ensure full panel shows
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Allow vertical expansion
        gbc.fill = GridBagConstraints.BOTH; // Let it grow in both directions
        gbc.anchor = GridBagConstraints.NORTH;

        mainContentPanel.add(coloredPanel, gbc);
        return mainContentPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title header
        JLabel dentistsInfoLabel = new JLabel("Dentists Information");
        dentistsInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        dentistsInfoLabel.setForeground(Color.decode("#192F8F"));
        dentistsInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        formPanel.add(dentistsInfoLabel);
        formPanel.add(Box.createVerticalStrut(16));
        
        // Record counter
        recordCounter = new JLabel("No records");
        recordCounter.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        recordCounter.setForeground(Color.decode("#192F8F"));
        recordCounter.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(recordCounter);
        formPanel.add(Box.createVerticalStrut(24));
        
        // Container for form fields (fixed width)
        JPanel fieldsContainer = new JPanel();
        fieldsContainer.setLayout(new BoxLayout(fieldsContainer, BoxLayout.Y_AXIS));
        fieldsContainer.setOpaque(false);
        fieldsContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldsContainer.setMaximumSize(new Dimension(520, Integer.MAX_VALUE));
        
        // Add fields
        String[] fieldLabels = {"Prefix", "First Name", "Middle Name", "Last Name", "Title", "Age", "Bio"};
        for (String label : fieldLabels) {
            JLabel fieldLabel = new JLabel(label);
            fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // LEFT ALIGN the labels
            
            if (label.equals("Bio")) {
                JTextArea bioArea = new JTextArea(6, 1);
                bioArea.setLineWrap(true);
                bioArea.setWrapStyleWord(true);
                bioArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                bioArea.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                // Set width to match other fields
                Dimension bioSize = new Dimension(520, 110);
                bioArea.setPreferredSize(bioSize);
                bioArea.setMaximumSize(bioSize);
                
                // Word limit to 500
                bioArea.setDocument(new PlainDocument() {
                    @Override
                    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                        if (str == null) return;
                        String currentText = getText(0, getLength());
                        String newText = currentText.substring(0, offs) + str + currentText.substring(offs);
                        int wordCount = newText.trim().isEmpty() ? 0 : newText.trim().split("\\s+").length;
                        if (wordCount <= 500) {
                            super.insertString(offs, str, a);
                        } else {
                            Toolkit.getDefaultToolkit().beep();
                        }
                    }
                });
                
                fieldInputs.put(label, bioArea);
                fieldsContainer.add(fieldLabel);
                fieldsContainer.add(Box.createVerticalStrut(6));
                fieldsContainer.add(bioArea);
            } else {
                JTextField textField = new JTextField();
                textField.setMaximumSize(new Dimension(520, 36));
                textField.setPreferredSize(new Dimension(520, 36));
                textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                textField.setAlignmentX(Component.LEFT_ALIGNMENT);
                
                fieldInputs.put(label, textField);
                fieldsContainer.add(fieldLabel);
                fieldsContainer.add(Box.createVerticalStrut(6));
                fieldsContainer.add(textField);
            }
            fieldsContainer.add(Box.createVerticalStrut(18));
        }
        
        // Add select image button
        JPanel imageBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        imageBtnPanel.setOpaque(false);
        imageBtnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imageBtnPanel.setMaximumSize(new Dimension(520, 38));
        
        selectImageBtn = new JButton("Select Image");
        selectImageBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        selectImageBtn.setPreferredSize(new Dimension(160, 38));
        selectImageBtn.addActionListener(this::selectImage);
        
        imageBtnPanel.add(selectImageBtn);
        fieldsContainer.add(imageBtnPanel);
        
        // Add fields container to form
        formPanel.add(fieldsContainer);
        
        // Add spacing before the Edit button
        formPanel.add(Box.createVerticalStrut(30));
        
        // Edit button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        actionBtn = new JButton("Edit");
        actionBtn.setFont(BTN_FONT);
        actionBtn.setBackground(BLUE_COLOR);
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setPreferredSize(new Dimension(150, 40));
        actionBtn.addActionListener(this::handleActionButton);
        
        buttonPanel.add(actionBtn);
        formPanel.add(buttonPanel);
        
        return formPanel;
    }

    // Helper method to add a form field with label on left
    private void addFormField(JPanel panel, String labelText, int x, int y, int fieldWidth) {
        JLabel label = new JLabel(labelText + ":");
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 5, 10);
        panel.add(label, gbc);
        
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textField.setPreferredSize(new Dimension(fieldWidth, 36));
        fieldInputs.put(labelText, textField);
        
        gbc = new GridBagConstraints();
        gbc.gridx = x + 1;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 5, 10);
        panel.add(textField, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Centered horizontally
        buttonPanel.setOpaque(false);

        actionBtn = new JButton("Edit");
        actionBtn.setFont(BTN_FONT);
        actionBtn.setBackground(BLUE_COLOR);
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setPreferredSize(new Dimension(120, 40));
        actionBtn.addActionListener(this::handleActionButton);

        buttonPanel.add(actionBtn);

        return buttonPanel;
    }

    // Helper methods
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
    private void loadDentistData() {
        SwingUtilities.invokeLater(() -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                dentistsList = dentistDAO.getAllDentists();
                currentDentistIndex = 0;
                displayCurrentDentist();
                updateRecordCounter();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading dentist data: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    private void displayCurrentDentist() {
        if (dentistsList.isEmpty() || currentDentistIndex < 0 || currentDentistIndex >= dentistsList.size()) {
            clearFields();
            currentDentist = null;
            return;
        }

        currentDentist = dentistsList.get(currentDentistIndex);
        populateFields(currentDentist);
    }

    private void populateFields(Dentist dentist) {
        if (dentist != null) {
            ((JTextField) fieldInputs.get("Title")).setText(dentist.getTitle() != null ? dentist.getTitle() : "");
            ((JTextField) fieldInputs.get("First Name")).setText(dentist.getFirstName() != null ? dentist.getFirstName() : "");
            ((JTextField) fieldInputs.get("Middle Name")).setText(dentist.getMiddleName() != null ? dentist.getMiddleName() : "");
            ((JTextField) fieldInputs.get("Last Name")).setText(dentist.getLastName() != null ? dentist.getLastName() : "");
            ((JTextField) fieldInputs.get("Prefix")).setText(dentist.getPrefix() != null ? dentist.getPrefix() : "");
            ((JTextField) fieldInputs.get("Age")).setText(dentist.getAge() > 0 ? String.valueOf(dentist.getAge()) : "");
            ((JTextArea) fieldInputs.get("Bio")).setText(dentist.getBio() != null ? dentist.getBio() : "");
            selectedImagePath = dentist.getDentistImgPath();
        }
    }

    private void clearFields() {
        for (JComponent field : fieldInputs.values()) {
            if (field instanceof JTextField) {
                ((JTextField) field).setText("");
            } else if (field instanceof JTextArea) {
                ((JTextArea) field).setText("");
            }
        }
        selectedImagePath = null;
    }

    private void saveDentist() {
        if (!validateFields()) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // Update current dentist with field values
            currentDentist.setPrefix(((JTextField) fieldInputs.get("Prefix")).getText().trim()); // Prefix
            currentDentist.setTitle(((JTextField) fieldInputs.get("Title")).getText().trim()); // Title
            currentDentist.setFirstName(((JTextField) fieldInputs.get("First Name")).getText().trim()); // First Name
            currentDentist.setMiddleName(((JTextField) fieldInputs.get("Middle Name")).getText().trim()); // Middle Name
            currentDentist.setLastName(((JTextField) fieldInputs.get("Last Name")).getText().trim()); // Last Name
            currentDentist.setAge(Integer.parseInt(((JTextField) fieldInputs.get("Age")).getText().trim())); // Age
            currentDentist.setBio(((JTextArea) fieldInputs.get("Bio")).getText().trim()); // Bio
            currentDentist.setDentistImgPath(selectedImagePath); // Image Path

            // Update existing dentist
            boolean success = dentistDAO.updateDentist(currentDentist);

            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Dentist information updated successfully!", 
                    "Update Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadDentistData(); // Reload to get updated list
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update dentist information.", 
                    "Update Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Age must be a valid number.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Age").requestFocus();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error updating dentist information: " + e.getMessage(), 
                "Update Error", 
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
            // Confirm before applying changes
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to apply the changes?",
                "Confirm Apply Changes",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                saveDentist();
                setFieldsEditable(false);
                actionBtn.setText("Edit");
                isEditing = false;
            }
            // If NO, stay in edit mode
        }
    }

    private void selectImage(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif", "bmp"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();
        }
    }

    private void navigateDentist(int direction) {
        if (isEditing) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "You have unsaved changes. Do you want to save before navigating?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                saveDentist();
                setFieldsEditable(false);
                actionBtn.setText("Edit");
                isEditing = false;
            } else if (confirm == JOptionPane.CANCEL_OPTION) {
                return;
            } else {
                setFieldsEditable(false);
                actionBtn.setText("Edit");
                isEditing = false;
            }
        }

        currentDentistIndex += direction;
        if (currentDentistIndex < 0) currentDentistIndex = 0;
        if (currentDentistIndex >= dentistsList.size()) currentDentistIndex = dentistsList.size() - 1;
        
        displayCurrentDentist();
        updateNavigationButtons();
        updateRecordCounter();
    }

    private boolean validateFields() {
        String prefix = ((JTextField) fieldInputs.get("Prefix")).getText().trim();
        String title = ((JTextField) fieldInputs.get("Title")).getText().trim();
        String firstName = ((JTextField) fieldInputs.get("First Name")).getText().trim();
        String lastName = ((JTextField) fieldInputs.get("Last Name")).getText().trim();
        String ageText = ((JTextField) fieldInputs.get("Age")).getText().trim();
        String bio = ((JTextArea) fieldInputs.get("Bio")).getText().trim();

        if (prefix.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Prefix is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Prefix").requestFocus();
            return false;
        }

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Title").requestFocus();
            return false;
        }

        if (firstName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("First Name").requestFocus();
            return false;
        }

        if (lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Last Name").requestFocus();
            return false;
        }

        if (ageText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Age is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Age").requestFocus();
            return false;
        }

        try {
            int age = Integer.parseInt(ageText);
            if (age < 18 || age > 100) {
                JOptionPane.showMessageDialog(this, "Age must be between 18 and 100.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                fieldInputs.get("Age").requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Age").requestFocus();
            return false;
        }

        if (bio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bio is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Bio").requestFocus();
            return false;
        }

        return true;
    }

    private void setFieldsEditable(boolean editable) {
        for (JComponent field : fieldInputs.values()) {
            if (field instanceof JTextField) {
                ((JTextField) field).setEditable(editable);
                field.setBackground(editable ? Color.WHITE : Color.decode("#F5F5F5"));
            } else if (field instanceof JTextArea) {
                ((JTextArea) field).setEditable(editable);
                field.setBackground(editable ? Color.WHITE : Color.decode("#F5F5F5"));
            }
        }
        selectImageBtn.setEnabled(editable);
    }

    private void updateNavigationButtons() {
        prevBtn.setEnabled(currentDentistIndex > 0);
        nextBtn.setEnabled(currentDentistIndex < dentistsList.size() - 1);
    }

    private void updateRecordCounter() {
        if (dentistsList.isEmpty()) {
            recordCounter.setText("No records");
        } else if (currentDentistIndex >= 0) {
            recordCounter.setText(String.format("Record %d of %d", currentDentistIndex + 1, dentistsList.size()));
        }
    }

    private void toggleSidebar() {
        sidebarPanel.setVisible(!sidebarPanel.isVisible());
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
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
                saveDentist();
            } else if (confirm == JOptionPane.CANCEL_OPTION) {
                return;
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
                // Already on this page
                break;
            case "Account Information":
                dispose();
                SwingUtilities.invokeLater(() -> {
                    AccountManagement accountInfo = new AccountManagement();
                    accountInfo.initialize();
                });
                break;
            case "Clinic Information":
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new ClinicInformationFrame();
                });
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DentistsInformation();
        });
    }
}