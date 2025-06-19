package Frames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

import DAO.ServicesDAO;
import DAO.ServicesDAO.Service;

public class ServicesDisplay extends JFrame {
    // Constants
    private static final Font BTN_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Color BLUE_COLOR = Color.decode("#1167B1");
    private static final Color LIGHT_BLUE_COLOR = Color.decode("#2A9DF4");
    private static final Color SIDEBAR_COLOR = Color.decode("#B8D4F0");
    private static final Color BACKGROUND_COLOR = Color.decode("#E1E3E5");

    // DAO and Data
    private ServicesDAO servicesDAO;
    private List<Service> servicesList;
    private Service currentService;
    private DefaultTableModel tableModel;
    private JTable servicesTable;
    private JComboBox<String> statusComboBox; // Fixed: Added missing field

    // UI Components
    private java.util.Map<String, JTextField> fieldInputs = new java.util.HashMap<>();
    private String selectedMenu = "Services Display";
    private JPanel sidebarPanel;
    private JLabel recordTitle;
    private boolean isEditing = false;
    private int selectedRow = -1;

    private boolean isInitialized = false;

    public ServicesDisplay() {
        servicesDAO = new ServicesDAO();
        servicesList = new ArrayList<>();
        initialize();
    }

    public void services() {
        initialize();
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
        loadServicesData();
        
        setTitle("Services Display");
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
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);
        contentPanel.add(createRightPanel(), BorderLayout.EAST);
        
        return contentPanel;
    }

    private JPanel createTablePanel() {
        String[] columnNames = {"Service ID", "Service Name", "Description", "Starting Price", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column is editable
            }
        };

        servicesTable = new JTable(tableModel);
        servicesTable.setRowHeight(36);
        servicesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        servicesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        servicesTable.getTableHeader().setBackground(BLUE_COLOR);
        servicesTable.getTableHeader().setForeground(Color.WHITE);

        // Hide Service ID column but keep the data
        servicesTable.getColumnModel().getColumn(0).setMinWidth(0);
        servicesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        servicesTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        // Set column widths
        servicesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        servicesTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        servicesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        servicesTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        servicesTable.getColumnModel().getColumn(5).setPreferredWidth(200);

        setupTableActions();

        JScrollPane tableScroll = new JScrollPane(servicesTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return tablePanel;
    }

    private void setupTableActions() {
        // Custom renderer for action buttons
        servicesTable.getColumn("Actions").setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return createActionPanel(row);
            }
        });
    }

    private JPanel createActionPanel(int row) {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        actionPanel.setOpaque(false);

        JButton viewBtn = makeSmallButton("View");
        JButton editBtn = makeSmallButton("Edit");
        JButton deleteBtn = makeSmallButton("Delete");

        viewBtn.addActionListener(e -> viewService(row));
        editBtn.addActionListener(e -> editService(row));
        deleteBtn.addActionListener(e -> deleteService(row));

        actionPanel.add(viewBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);

        return actionPanel;
    }

    private JPanel createRightPanel() {
        JPanel recordPanel = new JPanel();
        recordPanel.setLayout(new BoxLayout(recordPanel, BoxLayout.Y_AXIS));
        recordPanel.setBackground(SIDEBAR_COLOR);
        recordPanel.setPreferredSize(new Dimension(320, 0)); // Not too wide
        recordPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 2, 0, 0, Color.decode("#C0C0C0")),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        recordPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel recordTitle = new JLabel("View a Service");
        recordTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        recordTitle.setForeground(Color.decode("#192F8F"));
        recordTitle.setAlignmentX(Component.LEFT_ALIGNMENT); // Left align

        JPanel fieldsContainer = new JPanel();
        fieldsContainer.setLayout(new BoxLayout(fieldsContainer, BoxLayout.Y_AXIS));
        fieldsContainer.setOpaque(false);
        fieldsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        Dimension fieldSize = new Dimension(220, 28);

        String[] fieldLabels = {"Service Name:", "Description:", "Starting Price:", "Status:"};
        for (String label : fieldLabels) {
            JPanel fieldPanel = new JPanel();
            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
            fieldPanel.setOpaque(false);
            fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel fieldLabel = new JLabel(label);
            fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fieldLabel.setForeground(Color.BLACK);
            fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            fieldPanel.add(fieldLabel);
            fieldPanel.add(Box.createVerticalStrut(5));

            if (label.equals("Status:")) {
                statusComboBox = new JComboBox<>(new String[]{"Available", "Unavailable"});
                statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                statusComboBox.setMaximumSize(new Dimension(450, 30));
                statusComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
                statusComboBox.setBackground(Color.WHITE);
                statusComboBox.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));
                fieldPanel.add(statusComboBox);
            } else {
                JTextField textField = new JTextField();
                textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                textField.setMaximumSize(new Dimension(450, 30));
                textField.setPreferredSize(fieldSize);
                textField.setMinimumSize(fieldSize);
                textField.setAlignmentX(Component.LEFT_ALIGNMENT);
                textField.setBackground(Color.WHITE);
                textField.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));
                fieldInputs.put(label, textField);
                fieldPanel.add(textField);
            }
            fieldsContainer.add(fieldPanel);
            fieldsContainer.add(Box.createVerticalStrut(12));
        }

        recordPanel.add(recordTitle);
        recordPanel.add(Box.createVerticalStrut(15));
        recordPanel.add(fieldsContainer);
        recordPanel.add(Box.createVerticalGlue()); // Pushes everything up

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(300, 300));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.add(recordPanel);
        rightPanel.setOpaque(false);

        return rightPanel;
    }

    private JPanel createButtonContainer() {
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonContainer.setOpaque(false);

        JButton addBtn = makeButton("Add New");
        JButton saveBtn = makeButton("Save");
        JButton cancelBtn = makeButton("Cancel");

        addBtn.addActionListener(e -> addNewService());
        saveBtn.addActionListener(e -> saveService());
        cancelBtn.addActionListener(e -> cancelEdit());

        addBtn.setBackground(Color.decode("#28a745"));
        saveBtn.setBackground(Color.decode("#007bff"));
        cancelBtn.setBackground(Color.decode("#6c757d"));

        buttonContainer.add(addBtn);
        buttonContainer.add(saveBtn);
        buttonContainer.add(cancelBtn);

        return buttonContainer;
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

    private JButton makeSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(BLUE_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(60, 25));
        btn.setMargin(new Insets(1, 1, 1, 1));
        btn.setFocusPainted(false);
        return btn;
    }

    // DAO Methods
    private void loadServicesData() {
        SwingUtilities.invokeLater(() -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                servicesList = servicesDAO.getAllServices();
                updateTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading services data: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    private void updateTable() {
        tableModel.setRowCount(0); // Clear existing data
        
        for (Service service : servicesList) {
            Object[] rowData = {
                service.getServiceId(),
                service.getServiceName(),
                service.getServiceDesc(),
                String.format("₱%.2f", service.getStartingPrice()),
                service.getStatus(),
                createActionPanel(tableModel.getRowCount())
            };
            tableModel.addRow(rowData);
        }
    }

    private void viewService(int row) {
        if (row < 0 || row >= servicesList.size()) return;
        
        currentService = servicesList.get(row);
        selectedRow = row;
        populateFields(currentService);
        recordTitle.setText("View Service");
        recordTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        isEditing = false;
    }

    private void editService(int row) {
        if (row < 0 || row >= servicesList.size()) return;
        
        currentService = servicesList.get(row);
        selectedRow = row;
        populateFields(currentService);
        recordTitle.setText("Edit Service");
        setFieldsEditable(true);
        isEditing = true;
    }

    private void deleteService(int row) {
        if (row < 0 || row >= servicesList.size()) return;
        
        Service service = servicesList.get(row);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this service?\n" + service.getServiceName(),
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                boolean success = servicesDAO.deleteService(service.getInternalId());
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Service deleted successfully!", 
                        "Delete Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadServicesData(); // Reload data
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete service.", 
                        "Delete Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting service: " + e.getMessage(), 
                    "Delete Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    private void addNewService() {
        currentService = new Service();
        selectedRow = -1;
        clearFields();
        recordTitle.setText("Add New Service");
        setFieldsEditable(true);
        isEditing = true;
    }

    private void saveService() {
        if (!isEditing) return;
        
        if (!validateFields()) return;
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // Update current service with field values
            currentService.setServiceName(fieldInputs.get("Service Name:").getText().trim());
            currentService.setServiceDesc(fieldInputs.get("Description:").getText().trim());
            currentService.setStartingPrice(Double.parseDouble(fieldInputs.get("Starting Price:").getText().trim()));
            
            // Fixed: Use the statusComboBox field directly
            currentService.setStatus((String) statusComboBox.getSelectedItem());
            
            boolean success;
            if (currentService.getInternalId() == 0) {
                // Create new service
                success = servicesDAO.addService(currentService);
            } else {
                // Update existing service
                success = servicesDAO.updateService(currentService);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Service saved successfully!", 
                    "Save Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadServicesData(); // Reload data
                setFieldsEditable(false);
                recordTitle.setText("View Service");
                isEditing = false;
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save service.", 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving service: " + e.getMessage(), 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void cancelEdit() {
        if (isEditing) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel? Any unsaved changes will be lost.",
                "Confirm Cancel",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                setFieldsEditable(false);
                recordTitle.setText("View Service");
                isEditing = false;
                
                if (selectedRow >= 0) {
                    viewService(selectedRow);
                } else {
                    clearFields();
                }
            }
        }
    }

    private boolean validateFields() {
        String serviceName = fieldInputs.get("Service Name:").getText().trim();
        String description = fieldInputs.get("Description:").getText().trim();
        String priceText = fieldInputs.get("Starting Price:").getText().trim();
        
        if (serviceName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Service Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Service Name:").requestFocus();
            return false;
        }
        
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Description is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Description:").requestFocus();
            return false;
        }
        
        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Starting Price is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Starting Price:").requestFocus();
            return false;
        }
        
        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Starting Price must be a positive number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                fieldInputs.get("Starting Price:").requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Starting Price must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            fieldInputs.get("Starting Price:").requestFocus();
            return false;
        }
        
        return true;
    }

    private void populateFields(Service service) {
        if (service != null) {
            fieldInputs.get("Service Name:").setText(service.getServiceName() != null ? service.getServiceName() : "");
            fieldInputs.get("Description:").setText(service.getServiceDesc() != null ? service.getServiceDesc() : "");
            fieldInputs.get("Starting Price:").setText(service.getStartingPrice() > 0 ? String.valueOf(service.getStartingPrice()) : "");
            
            // Fixed: Use the statusComboBox field directly
            statusComboBox.setSelectedItem(service.getStatus() != null ? service.getStatus() : "Available");
        }
    }

    private void clearFields() {
        for (JTextField field : fieldInputs.values()) {
            field.setText("");
        }
        
        // Fixed: Use the statusComboBox field directly
        statusComboBox.setSelectedIndex(0);
    }

    private void setFieldsEditable(boolean editable) {
        for (JTextField field : fieldInputs.values()) {
            field.setEditable(editable);
            field.setBackground(editable ? Color.WHITE : Color.decode("#F5F5F5"));
        }
        
        // Fixed: Use the statusComboBox field directly
        statusComboBox.setEnabled(editable);
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
                saveService();
            } else if (confirm == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        switch (menuText) {
            case "Home":
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new homepage().home();
                });
                break;
            case "Services Display":
                // Already on this page
                break;
            case "Dentists Information":
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new DentistsInformation().initialize();
                });
                break;
            case "Account Information":
                dispose();
                SwingUtilities.invokeLater(() -> {
                    new AccountManagement().initialize();
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
            new ServicesDisplay();
        });
    }
}