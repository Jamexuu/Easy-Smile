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
    private JButton addBtn;
    private JButton deleteBtn;
    private JButton selectImageBtn;
    private JLabel imagePreview;
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
        mainContentPanel.add(logoImage);
        mainContentPanel.add(Box.createRigidArea(new Dimension(1, 20)));
        mainContentPanel.add(createContentPanel());
        mainContentPanel.add(Box.createVerticalGlue());
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        logoImage.setAlignmentX(CENTER_ALIGNMENT);
        
        return mainContentPanel;
    }

    private JPanel createContentPanel() {
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setOpaque(false);
        
        // Left side - Image preview
        JPanel imagePanel = createImagePanel();
        
        // Right side - Form
        JPanel formPanel = createFormPanel();
        
        mainContentPanel.add(imagePanel, BorderLayout.WEST);
        mainContentPanel.add(formPanel, BorderLayout.CENTER);
        
        return mainContentPanel;
    }

    private JPanel createImagePanel() {
        JPanel imageContainer = new JPanel(new BorderLayout());
        imageContainer.setOpaque(false);
        imageContainer.setPreferredSize(new Dimension(300, 600));
        
        // Image preview
        imagePreview = new JLabel("No Image Selected");
        imagePreview.setPreferredSize(new Dimension(250, 300));
        imagePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        imagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreview.setVerticalAlignment(SwingConstants.CENTER);
        imagePreview.setBackground(Color.WHITE);
        imagePreview.setOpaque(true);
        
        // Select image button
        selectImageBtn = new JButton("Select Image");
        selectImageBtn.setFont(BTN_FONT);
        selectImageBtn.addActionListener(this::selectImage);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(selectImageBtn);
        
        imageContainer.add(imagePreview, BorderLayout.CENTER);
        imageContainer.add(buttonPanel, BorderLayout.SOUTH);
        imageContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        return imageContainer;
    }

    private JPanel createFormPanel() {
        JPanel recordPanel = new JPanel(new BorderLayout());
        recordPanel.setBackground(SIDEBAR_COLOR);
        recordPanel.setPreferredSize(new Dimension(500, 600));
        recordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, Color.WHITE),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel dentistsInfoLabel = new JLabel("Dentists Information");
        dentistsInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        dentistsInfoLabel.setForeground(Color.decode("#192F8F"));

        Box fieldsBox = Box.createVerticalBox();
        fieldsBox.add(dentistsInfoLabel);
        fieldsBox.add(Box.createVerticalStrut(10));
        
        // Record counter
        recordCounter = new JLabel("Record 0 of 0");
        recordCounter.setFont(LABEL_FONT);
        recordCounter.setForeground(Color.decode("#192F8F"));
        fieldsBox.add(recordCounter);
        fieldsBox.add(Box.createVerticalStrut(15));
        
        createInputFields(fieldsBox);
        
        recordPanel.add(fieldsBox, BorderLayout.CENTER);
        recordPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        return recordPanel;
    }

    private void createInputFields(Box container) {
        String[] fieldLabels = {"Title", "First Name", "Middle Name", "Last Name", "Age", "Bio"};
        
        for (String label : fieldLabels) {
            JPanel fieldPanel = new JPanel();
            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
            fieldPanel.setOpaque(false);

            JLabel fieldLabel = new JLabel(label);
            fieldLabel.setFont(LABEL_FONT);
            fieldLabel.setForeground(Color.decode("#192F8F"));

            if (label.equals("Bio")) {
                JTextArea bioArea = new JTextArea(3, 20);
                bioArea.setFont(MAIN_FONT);
                bioArea.setLineWrap(true);
                bioArea.setWrapStyleWord(true);
                bioArea.setBackground(Color.WHITE);
                bioArea.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));
                bioArea.setEditable(false);
                bioArea.setPreferredSize(new Dimension(400, 80));
                bioArea.setMaximumSize(new Dimension(1000, 120));
                bioArea.setDocument(new JTextAreaLimit(500));

                fieldInputs.put(label, bioArea);
                fieldPanel.add(fieldLabel);
                fieldPanel.add(new JScrollPane(bioArea));
            } else {
                JTextField textField = new JTextField();
                textField.setFont(MAIN_FONT);
                textField.setBackground(Color.WHITE);
                textField.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));
                textField.setEditable(false);
                textField.setMaximumSize(new Dimension(1000, 35));
                textField.setPreferredSize(new Dimension(400, 35));

                fieldInputs.put(label, textField);
                fieldPanel.add(fieldLabel);
                fieldPanel.add(textField);
            }
            container.add(fieldPanel);
            container.add(Box.createVerticalStrut(8));
        }
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        
        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.setOpaque(false);
        
        prevBtn = new JButton("◀ Previous");
        nextBtn = new JButton("Next ▶");
        prevBtn.setFont(BTN_FONT);
        nextBtn.setFont(BTN_FONT);
        prevBtn.addActionListener(e -> navigateDentist(-1));
        nextBtn.addActionListener(e -> navigateDentist(1));
        
        navPanel.add(prevBtn);
        navPanel.add(nextBtn);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 20));
        actionPanel.setOpaque(false);
        
        addBtn = new JButton("Add New");
        deleteBtn = new JButton("Delete");
        actionBtn = new JButton("Edit");
        
        addBtn.setFont(BTN_FONT);
        deleteBtn.setFont(BTN_FONT);
        actionBtn.setFont(BTN_FONT);
        
        addBtn.setBackground(Color.decode("#28a745"));
        deleteBtn.setBackground(Color.decode("#dc3545"));
        actionBtn.setBackground(BLUE_COLOR);
        
        addBtn.setForeground(Color.WHITE);
        deleteBtn.setForeground(Color.WHITE);
        actionBtn.setForeground(Color.WHITE);
        
        addBtn.setPreferredSize(new Dimension(100, 36));
        deleteBtn.setPreferredSize(new Dimension(100, 36));
        actionBtn.setPreferredSize(new Dimension(140, 36));
        
        addBtn.addActionListener(this::addNewDentist);
        deleteBtn.addActionListener(this::deleteDentist);
        actionBtn.addActionListener(this::handleActionButton);
        
        actionPanel.add(addBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(actionBtn);
        
        buttonPanel.add(navPanel, BorderLayout.WEST);
        buttonPanel.add(actionPanel, BorderLayout.EAST);
        
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
                updateNavigationButtons();
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
            ((JTextField) fieldInputs.get("Age")).setText(dentist.getAge() > 0 ? String.valueOf(dentist.getAge()) : "");
            ((JTextArea) fieldInputs.get("Bio")).setText(dentist.getBio() != null ? dentist.getBio() : "");
            
            // Load image
            loadDentistImage(dentist.getDentistImgPath());
            selectedImagePath = dentist.getDentistImgPath();
        }
    }

    private void loadDentistImage(String imagePath) {
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(imagePath);
                    Image img = icon.getImage().getScaledInstance(250, 300, Image.SCALE_SMOOTH);
                    imagePreview.setIcon(new ImageIcon(img));
                    imagePreview.setText("");
                } else {
                    imagePreview.setIcon(null);
                    imagePreview.setText("Image not found");
                }
            } catch (Exception e) {
                imagePreview.setIcon(null);
                imagePreview.setText("Error loading image");
            }
        } else {
            imagePreview.setIcon(null);
            imagePreview.setText("No Image Selected");
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
        imagePreview.setIcon(null);
        imagePreview.setText("No Image Selected");
        selectedImagePath = null;
    }

    private void saveDentist() {
        if (!validateFields()) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // Update current dentist with field values
            currentDentist.setTitle(((JTextField) fieldInputs.get("Title")).getText().trim());
            currentDentist.setFirstName(((JTextField) fieldInputs.get("First Name")).getText().trim());
            currentDentist.setMiddleName(((JTextField) fieldInputs.get("Middle Name")).getText().trim());
            currentDentist.setLastName(((JTextField) fieldInputs.get("Last Name")).getText().trim());
            currentDentist.setAge(Integer.parseInt(((JTextField) fieldInputs.get("Age")).getText().trim()));
            currentDentist.setBio(((JTextArea) fieldInputs.get("Bio")).getText().trim());
            currentDentist.setDentistImgPath(selectedImagePath);
            
            boolean success;
            if (currentDentist.getInternalId() == 0) {
                // Create new dentist
                success = dentistDAO.addDentist(currentDentist);
                if (success) {
                    loadDentistData(); // Reload to get updated list with new dentist
                }
            } else {
                // Update existing dentist
                success = dentistDAO.updateDentist(currentDentist);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Dentist information saved successfully!", 
                    "Save Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save dentist information.", 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving dentist information: " + e.getMessage(), 
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
            saveDentist();
            setFieldsEditable(false);
            actionBtn.setText("Edit");
            isEditing = false;
        }
    }

    private void addNewDentist(ActionEvent e) {
        if (isEditing) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "You have unsaved changes. Do you want to save before creating a new dentist?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                saveDentist();
            } else if (confirm == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        // Create new dentist
        currentDentist = new Dentist();
        clearFields();
        setFieldsEditable(true);
        actionBtn.setText("Apply Changes");
        isEditing = true;
        
        // Update record counter
        recordCounter.setText("New Record");
        currentDentistIndex = -1; // Indicate new record
    }

    private void deleteDentist(ActionEvent e) {
        if (currentDentist == null || currentDentist.getInternalId() == 0) {
            JOptionPane.showMessageDialog(this, 
                "No dentist selected for deletion.", 
                "Delete Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this dentist record?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                boolean success = dentistDAO.deleteDentist(currentDentist.getInternalId());
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Dentist deleted successfully!", 
                        "Delete Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadDentistData(); // Reload data
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete dentist.", 
                        "Delete Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting dentist: " + ex.getMessage(), 
                    "Delete Error", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
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
            loadDentistImage(selectedImagePath);
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
        String title = ((JTextField) fieldInputs.get("Title")).getText().trim();
        String firstName = ((JTextField) fieldInputs.get("First Name")).getText().trim();
        String lastName = ((JTextField) fieldInputs.get("Last Name")).getText().trim();
        String ageText = ((JTextField) fieldInputs.get("Age")).getText().trim();
        String bio = ((JTextArea) fieldInputs.get("Bio")).getText().trim();
        
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