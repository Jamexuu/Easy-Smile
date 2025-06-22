package Frames;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.EventObject;
import Frames.CalendarPanel;

import DAO.AppointmentDAO;
import DAO.AppointmentDAO.Appointment;
import DAO.ServicesDAO;
import DAO.AccountManagementDAO;
import DAO.PatientDAO;

public class AppointmentManagementFrame extends JFrame {
    // Constants
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font BTN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color BACKGROUND_COLOR = Color.decode("#E1E3E5");
    private static final Color BLUE_COLOR = Color.decode("#1167B1");
    private static final Color LIGHT_BLUE_COLOR = Color.decode("#2A9DF4");
    private static final Color SIDEBAR_COLOR = Color.decode("#D0EFFF");
    
    // DAO and Data
    private AppointmentDAO appointmentDAO; // Fixed: Changed variable name to lowercase
    private PatientDAO patientDAO; // Added for patient data lookup
    private ServicesDAO servicesDAO = new ServicesDAO();
    private AccountManagementDAO accountDAO = new AccountManagementDAO();
    private List<Integer> appointmentIds;
    private java.util.Map<String, JTextField> fieldInputs;
    
    // UI Components
    private JTable upcomingTable;
    private JTable completedTable;
    private JTable canceledTable;
    private DefaultTableModel upcomingModel;
    private DefaultTableModel completedModel;
    private DefaultTableModel canceledModel;
    private JTextField searchBar;
    private List<JTextField> sidebarFields;

    private JButton saveButton;
    private JButton cancelButton;
    private boolean editMode = false;
    private int currentEditingRow = -1;

    //appointment status
    private JComboBox<String> statusComboBox;
    private JLabel createdAtLabel;
    private JLabel updatedAtLabel;

    private JLabel statusValueLabel;

    private boolean isInitialized = false; 

    private JPanel sidebarPanel;
        
    public AppointmentManagementFrame() {
        appointmentDAO = new AppointmentDAO(); // Fixed: Changed to lowercase
        patientDAO = new PatientDAO(); // Added for patient lookup
        servicesDAO = new ServicesDAO();
        accountDAO = new AccountManagementDAO();
        patientDAO = new PatientDAO();
        appointmentIds = new ArrayList<>();
        fieldInputs = new java.util.HashMap<>();
        sidebarFields = new ArrayList<>();
        
        initialize();
        loadData();
    }
    
    public void initialize() {
        getContentPane().setBackground(BACKGROUND_COLOR);

        if (isInitialized) {
            return;
        }
        
        isInitialized = true;
        
        // Clear any existing content
        getContentPane().removeAll();
        
        // Set background color
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        createComponents();
        
        setTitle("Appointment Management");
        setSize(1200, 700);
        setResizable(true);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private void createComponents() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.add(createLogoPanel());
        headerPanel.add(createBluePanel());
        return headerPanel;
    }
    
    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 10, 10));
        
        JLabel logoImage = new JLabel();
        try {
            java.net.URL logoUrl = getClass().getResource("/images/smalllogonotext.png");
            if (logoUrl != null) {
                ImageIcon icon = new ImageIcon(logoUrl);
                icon = new ImageIcon(icon.getImage().getScaledInstance(170, 50, Image.SCALE_SMOOTH));
                logoImage.setIcon(icon);
            } else {
                logoImage.setText("Logo");
            }
        } catch (Exception e) {
            logoImage.setText("Logo");
        }
        
        JLabel logoAdmin = new JLabel("Admin");
        logoAdmin.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoAdmin.setForeground(Color.decode("#192F8F"));
        
        logoPanel.add(logoImage, BorderLayout.WEST);
        logoPanel.add(logoAdmin, BorderLayout.EAST);
        
        return logoPanel;
    }
    
    private JPanel createBluePanel() {
        JPanel bluePanel = new JPanel(new BorderLayout());
        bluePanel.setBackground(LIGHT_BLUE_COLOR);
        bluePanel.setMaximumSize(new Dimension(6000, 150));
        bluePanel.setPreferredSize(new Dimension(6000, 150));
        bluePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        bluePanel.add(createHeadersPanel(), BorderLayout.NORTH);
        bluePanel.add(createSearchPanel(), BorderLayout.CENTER);
        
        return bluePanel;
    }
    
    private JPanel createHeadersPanel() {
        JPanel headersPanel = new JPanel(new BorderLayout());
        headersPanel.setOpaque(false);
        headersPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        JLabel mainHeader = new JLabel("Appointment Management");
        mainHeader.setFont(TITLE_FONT);
        mainHeader.setForeground(Color.WHITE);
        
        JLabel subHeader = new JLabel("Schedule, View, Edit, and Cancel Appointments");
        subHeader.setFont(MAIN_FONT);
        subHeader.setForeground(Color.WHITE);
        
        JPanel buttonPanel = createHeaderButtonPanel();
        
        headersPanel.add(mainHeader, BorderLayout.WEST);
        headersPanel.add(buttonPanel, BorderLayout.EAST);
        headersPanel.add(subHeader, BorderLayout.SOUTH);
        
        return headersPanel;
    }
    
    private JPanel createHeaderButtonPanel() {
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
    
        JButton homeBtn = createStyledButton("Back to Home");
        
        homeBtn.addActionListener(this::goBackToHome);
        
        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnPanel.add(homeBtn);
        
        return btnPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setOpaque(false);
        
        searchBar = new JTextField();
        searchBar.setFont(MAIN_FONT);
        searchBar.setBackground(Color.WHITE);
        searchBar.setForeground(BLUE_COLOR);
        searchBar.setMaximumSize(new Dimension(250, 30));
        
        JButton searchBtn = createStyledButton("Search");
        JButton refreshBtn = createStyledButton("Refresh");
        
        searchBtn.addActionListener(this::performSearch);
        refreshBtn.addActionListener(this::refreshData);
        
        searchPanel.add(searchBar);
        searchPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        searchPanel.add(searchBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(refreshBtn);
        
        return searchPanel;
    }
    
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setOpaque(true);
        
        JTabbedPane appointmentTabs = createTabsPanel();
        contentPanel.add(appointmentTabs, BorderLayout.CENTER);

        sidebarPanel = createSidebarPanel();
        contentPanel.add(sidebarPanel, BorderLayout.EAST);

        appointmentTabs.addChangeListener(e -> {
            int selectedIndex = appointmentTabs.getSelectedIndex();
            String selectedTitle = appointmentTabs.getTitleAt(selectedIndex);
            if ("CALENDAR".equalsIgnoreCase(selectedTitle)) {
                if (sidebarPanel != null) {
                    sidebarPanel.removeAll();
                    sidebarPanel.revalidate();
                    sidebarPanel.repaint();
                }
            } else {
                sidebarPanel.removeAll();
                createSidebarFields(sidebarPanel);
                sidebarPanel.revalidate();
                sidebarPanel.repaint();
            }
        });
        
        return contentPanel;
    }
    
    private JTabbedPane createTabsPanel() {
        JTabbedPane appointmentTabs = new JTabbedPane();

        // Create table models - Updated column names to match actual data
        String[] appointmentColumns = {"Appointment ID", "Patient Full Name", "Service Desc.", "Date", "Time", "Email", "Status", "Actions"};
        String[] statusOptions = {"Upcoming", "Completed", "Canceled"};

        upcomingModel = new DefaultTableModel(appointmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Allow editing for Status (6) and Actions (7) columns
                return column == 6 || column == 7;
            }
        };
        completedModel = new DefaultTableModel(appointmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };
        canceledModel = new DefaultTableModel(appointmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        upcomingModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 6) {
                int row = e.getFirstRow();
                String appointmentId = (String) upcomingModel.getValueAt(row, 0);
                String newStatus = (String) upcomingModel.getValueAt(row, 6);
                // Update the status in the database
                try {
                    int internalId = appointmentIds.get(row);
                    Appointment appointment = appointmentDAO.getAppointmentById(internalId);
                    if (appointment != null) {
                        appointment.setStatus(newStatus);
                        appointmentDAO.updateAppointment(appointment);
                        loadData(); // Refresh tables to move row if needed
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to update status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create tables
        upcomingTable = createTable(upcomingModel);
        completedTable = createTable(completedModel);
        canceledTable = createTable(canceledModel);

        upcomingTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JComboBox<>(statusOptions)));

        // Set up renderers and editors
        setupTableActionsColumn(upcomingTable, 7, "edit");
        setupTableActionsColumn(completedTable, 7, "delete");
        setupTableActionsColumn(canceledTable, 7, "delete");

        // Add tables to tabs
        appointmentTabs.addTab("UPCOMING", new JScrollPane(upcomingTable));
        appointmentTabs.addTab("COMPLETED", new JScrollPane(completedTable));
        appointmentTabs.addTab("CANCELED", new JScrollPane(canceledTable));
        appointmentTabs.addTab("CALENDAR", new CalendarPanel());

        // JPanel tabsPanel = new JPanel(new BorderLayout());
        // tabsPanel.setOpaque(false);
        // tabsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        // tabsPanel.add(appointmentTabs, BorderLayout.CENTER);

        return appointmentTabs;
    }
    
    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(BLUE_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        
        // Hide Appointment ID column but keep the data
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component comp = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                if (status.equalsIgnoreCase("Completed")) {
                    setBackground(new Color(220, 255, 220)); // Light green
                    setForeground(new Color(0, 100, 0));     // Dark green
                } else if (status.equalsIgnoreCase("Canceled")) {
                    setBackground(new Color(255, 220, 220)); // Light red
                    setForeground(new Color(150, 0, 0));     // Dark red
                } else if (status.equalsIgnoreCase("Upcoming")) {
                    setBackground(new Color(220, 220, 255)); // Light blue
                    setForeground(new Color(0, 0, 150));     // Dark blue
                } else {
                    setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                }
            }
            return comp;
        }
    });
        return table;
    }
    
    private void setupTableActionsColumn(JTable table, int actionsColumn, String type) {
        if ("delete".equals(type)) {
            table.getColumn("Actions").setCellRenderer(new DeleteActionsRenderer());
            table.getColumn("Actions").setCellEditor(new DeleteActionsEditor(table, sidebarFields));
        } else {
            table.getColumn("Actions").setCellRenderer(new ActionsRenderer());
            table.getColumn("Actions").setCellEditor(new ActionsEditor(table, sidebarFields));
        }
    }
    
    private JPanel createSidebarPanel() {
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(260, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, Color.decode("#C0C0C0")),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel sidebarTitle = new JLabel("View Appointment Details");
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sidebarTitle.setForeground(Color.decode("#192F8F"));
        sidebarTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidebarPanel.add(sidebarTitle);
        sidebarPanel.add(Box.createVerticalStrut(15));
        
        createSidebarFields(sidebarPanel);

        // Add save/cancel buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(400, 40));
        
        saveButton = new JButton("Save Changes");
        saveButton.setFont(BTN_FONT);
        saveButton.setBackground(BLUE_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(this::saveAppointmentChanges);
        saveButton.setVisible(false); // Initially hidden
        
        cancelButton = new JButton("Cancel");
        cancelButton.setFont(BTN_FONT);
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(this::cancelEdit);
        cancelButton.setVisible(false); // Initially hidden
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(buttonPanel);
        
        return sidebarPanel;
    }
    
    private void createSidebarFields(JPanel parent) {
        // Updated to match appointment data structure
        String[] fieldLabels = {
            "Appointment ID:", 
            "Patient Full Name:", 
            "Service Description:", 
            "Email:", 
            "Appointment Date:", 
            "Appointment Time:", 
        };

        for (String label : fieldLabels) {
            JPanel fieldPanel = new JPanel();
            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
            fieldPanel.setOpaque(false);
            
            JLabel fieldLabel = new JLabel(label);
            fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fieldLabel.setForeground(Color.decode("#192F8F"));
            
            JTextField textField = new JTextField();
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            textField.setPreferredSize(new Dimension(400, 26));
            textField.setMaximumSize(new Dimension(400, 26));
            textField.setBackground(Color.WHITE);
            textField.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));
            textField.setEditable(false);
            
            fieldInputs.put(label, textField);
            sidebarFields.add(textField);
            
            fieldPanel.add(fieldLabel);
            fieldPanel.add(textField);
            parent.add(fieldPanel);
            parent.add(Box.createVerticalStrut(8));
        }

         // Add status dropdown
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setOpaque(false);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(Color.decode("#192F8F"));

        JPanel comboWrapper = new JPanel();
        comboWrapper.setLayout(new BoxLayout(comboWrapper, BoxLayout.X_AXIS));
        comboWrapper.setMaximumSize(new Dimension(400, 26));
        comboWrapper.setPreferredSize(new Dimension(400, 26));
        comboWrapper.setOpaque(false);
        comboWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel statusValueLabel = new JLabel("N/A");
        statusValueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusValueLabel.setForeground(Color.BLACK);
        statusValueLabel.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));
        statusValueLabel.setOpaque(true);
        statusValueLabel.setBackground(Color.WHITE);

        statusPanel.add(statusLabel);
        statusPanel.add(Box.createVerticalStrut(3));
        statusPanel.add(statusValueLabel);

        parent.add(statusPanel);
        parent.add(Box.createVerticalStrut(8));

        // Store reference for updating later
        this.statusValueLabel = statusValueLabel;

        // Add Created At and Updated At as labels
        addReadonlyField(parent, "Created At:", "createdAt");
        addReadonlyField(parent, "Updated At:", "updatedAt");
    }

    // Helper method for read-only fields
    private void addReadonlyField(JPanel parent, String labelText, String fieldName) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        JLabel textLabel = new JLabel(labelText);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(Color.decode("#192F8F"));
        
        JLabel valueLabel = new JLabel("N/A");
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueLabel.setForeground(Color.BLACK);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        if (fieldName.equals("createdAt")) {
            createdAtLabel = valueLabel;
        } else {
            updatedAtLabel = valueLabel;
        }
        
        panel.add(textLabel);
        panel.add(valueLabel);
        parent.add(panel);
        parent.add(Box.createVerticalStrut(8));
    }

    private JButton createStyledButton(String text) {
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
    private void loadData() {
        SwingUtilities.invokeLater(() -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                List<Appointment> appointments = appointmentDAO.getAllAppointments();
                populateTable(appointments);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading appointments: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }
    
    private void populateTable(List<Appointment> appointments) {
        upcomingModel.setRowCount(0);
        completedModel.setRowCount(0);
        canceledModel.setRowCount(0);
        appointmentIds.clear();
        
        for (Appointment appointment : appointments) {
            // Fetch patient full name
            String patientFullName = "";
            try {
                var patient = patientDAO.getPatientByFormattedId(appointment.getPatientId());
                if (patient != null) {
                    patientFullName = patient.getFullName();
                }
            } catch (Exception ex) {
                patientFullName = appointment.getPatientId();
            }

            String serviceDesc = appointment.getServiceId();
            try {
                var service = servicesDAO.getServiceByFormattedId(appointment.getServiceId());
                if (service != null && service.getServiceDesc() != null) {
                    serviceDesc = service.getServiceDesc();
                }
            } catch (Exception ex) {
                serviceDesc = appointment.getServiceId();
            }

            // Fetch scheduled by email
            String email = "";
            try {
                var account = accountDAO.getAccountByFormattedId(appointment.getScheduledBy());
                if (account != null && account.getEmail() != null) {
                    email = account.getEmail();
                }
            } catch (Exception ex) {
                email = appointment.getScheduledBy();
            }

            Object[] rowData = {
                appointment.getAppointmentId(),
                patientFullName,
                serviceDesc,
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                email,
                appointment.getStatus(),
                "Actions"
            };
            appointmentIds.add(appointment.getInternalId());

            String status = appointment.getStatus();
            if ("Upcoming".equalsIgnoreCase(status)) {
                upcomingModel.addRow(rowData);
            } else if ("Completed".equalsIgnoreCase(status)) {
                completedModel.addRow(rowData);
            } else if ("Canceled".equalsIgnoreCase(status)) {
                canceledModel.addRow(rowData);
            }
        }
    }
    
    // Event Handlers
    private void performSearch(ActionEvent e) {
        String searchText = searchBar.getText().trim();
        if (searchText.isEmpty()) {
            loadData();
            return;
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            List<Appointment> results = appointmentDAO.searchAppointments(searchText);
            populateTable(results);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error searching appointments: " + ex.getMessage(), 
                "Search Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private void refreshData(ActionEvent e) {
        searchBar.setText("");
        loadData();
        clearSidebarFields();
        JOptionPane.showMessageDialog(this, "Data refreshed successfully!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void goBackToHome(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to go back to home?",
            "Confirm",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new homepage().home();
            });
        }
    }
    
    private void viewAppointment(int row) {
        try {
            if (editMode) setEditMode(false);

            if (row >= 0 && row < appointmentIds.size()) {
                int appointmentInternalId = appointmentIds.get(row);
                Appointment appointment = appointmentDAO.getAppointmentById(appointmentInternalId);

                // Fetch patient full name
                String patientFullName = "";
                try {
                    var patient = patientDAO.getPatientByFormattedId(appointment.getPatientId());
                    if (patient != null) {
                        patientFullName = patient.getFullName();
                    }
                } catch (Exception ex) {
                    patientFullName = appointment.getPatientId();
                }

                // Fetch service description
                String serviceDesc = appointment.getServiceId();
                try {
                    var service = servicesDAO.getServiceByFormattedId(appointment.getServiceId());
                    if (service != null && service.getServiceDesc() != null) {
                        serviceDesc = service.getServiceDesc();
                    }
                } catch (Exception ex) {
                    serviceDesc = appointment.getServiceId();
                }

                String email = appointment.getScheduledBy();
                try {
                    var account = accountDAO.getAccountByFormattedId(appointment.getScheduledBy());
                    if (account != null && account.getEmail() != null) {
                        email = account.getEmail();
                    }
                } catch (Exception ex) {
                    email = appointment.getScheduledBy();
                }

                if (appointment != null) {
                    fieldInputs.get("Appointment ID:").setText(appointment.getAppointmentId() != null ? appointment.getAppointmentId() : "");
                    fieldInputs.get("Patient Full Name:").setText(patientFullName);
                    fieldInputs.get("Service Description:").setText(serviceDesc);
                    fieldInputs.get("Email:").setText(email);
                    fieldInputs.get("Appointment Date:").setText(appointment.getAppointmentDate() != null ? appointment.getAppointmentDate() : "");
                    fieldInputs.get("Appointment Time:").setText(appointment.getAppointmentTime() != null ? appointment.getAppointmentTime() : "");

                    String status = appointment.getStatus();
                    if (statusValueLabel != null) {
                        statusValueLabel.setText(status != null ? status : "N/A");
                    }
                    createdAtLabel.setText(appointment.getCreatedAt() != null ? appointment.getCreatedAt() : "N/A");
                    updatedAtLabel.setText(appointment.getUpdatedAt() != null ? appointment.getUpdatedAt() : "N/A");
                }
            }
        } catch (Exception e) {
            clearSidebarFields();
            JOptionPane.showMessageDialog(this,
                    "Error viewing appointment: " + e.getMessage(),
                    "View Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void deleteAppointment(int row) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this appointment?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                if (row >= 0 && row < appointmentIds.size()) {
                    int appointmentInternalId = appointmentIds.get(row);
                    boolean success = appointmentDAO.deleteAppointment(appointmentInternalId);
                    
                    if (success) {
                        loadData();
                        clearSidebarFields();
                        
                        JOptionPane.showMessageDialog(this, 
                            "Appointment deleted successfully!", 
                            "Delete Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to delete appointment.", 
                            "Delete Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error deleting appointment: " + ex.getMessage(), 
                    "Delete Error", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }
    
    private void clearSidebarFields() {
        for (JTextField field : sidebarFields) {
            field.setText("");
        }
        if (statusValueLabel != null) {
            statusValueLabel.setText("N/A");
        }
        createdAtLabel.setText("N/A");
        updatedAtLabel.setText("N/A");
    }
    
    // Inner Classes for Table Actions
    static class ActionsRenderer extends JPanel implements TableCellRenderer {
        private final JButton viewBtn;

        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 0));
            setOpaque(true);

            viewBtn = createStyledButton("View");

            add(viewBtn);
        }

        private static JButton createStyledButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setBackground(BLUE_COLOR);
            btn.setForeground(Color.WHITE);
            btn.setPreferredSize(new Dimension(60, 25));
            btn.setFocusPainted(false);
            return btn;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                viewBtn.setBackground(Color.decode("#0E4A7A"));
            } else {
                setBackground(table.getBackground());
                viewBtn.setBackground(BLUE_COLOR);
            }
            return this;
        }
    }

    // Renderer for Delete actions (View + Delete)
    static class DeleteActionsRenderer extends JPanel implements TableCellRenderer {
        private final JButton viewBtn;
        private final JButton deleteBtn;

        public DeleteActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 0));
            setOpaque(true);

            viewBtn = ActionsRenderer.createStyledButton("View");
            deleteBtn = ActionsRenderer.createStyledButton("Delete");
            deleteBtn.setBackground(Color.RED);

            add(viewBtn);
            add(deleteBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                viewBtn.setBackground(Color.decode("#0E4A7A"));
                deleteBtn.setBackground(Color.RED);
            } else {
                setBackground(table.getBackground());
                viewBtn.setBackground(BLUE_COLOR);
                deleteBtn.setBackground(Color.RED);
            }
            return this;
        }
    }

    // Editor for Delete actions (View + Delete)
    static class DeleteActionsEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private final JTable table;
        private int currentRow;

        public DeleteActionsEditor(JTable table, List<JTextField> sidebarFields) {
            this.table = table;
        }

        @Override
        public Component getTableCellEditorComponent(JTable tableParam, Object value,
                boolean isSelected, int row, int column) {
            this.currentRow = row;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
            panel.setOpaque(true);

            JButton viewBtn = ActionsRenderer.createStyledButton("View");
            JButton deleteBtn = ActionsRenderer.createStyledButton("Delete");
            deleteBtn.setBackground(Color.RED);

            viewBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    AppointmentManagementFrame parent = (AppointmentManagementFrame) SwingUtilities.getWindowAncestor(table);
                    if (parent != null) {
                        parent.viewAppointment(currentRow);
                    }
                    fireEditingStopped();
                });
            });

            deleteBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    AppointmentManagementFrame parent = (AppointmentManagementFrame) SwingUtilities.getWindowAncestor(table);
                    if (parent != null) {
                        parent.deleteAppointment(currentRow);
                        parent.loadData();
                    }
                    fireEditingStopped();
                });
            });

            panel.add(viewBtn);
            panel.add(deleteBtn);
            panel.setBackground(isSelected ? tableParam.getSelectionBackground() : tableParam.getBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject e) {
            return false;
        }
    }

    static class ActionsEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private final JTable table;
        private int currentRow;

        public ActionsEditor(JTable table, List<JTextField> sidebarFields) {
            this.table = table;
        }

        @Override
        public Component getTableCellEditorComponent(JTable tableParam, Object value, 
                boolean isSelected, int row, int column) {
            this.currentRow = row;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
            panel.setOpaque(true);

            JButton viewBtn = ActionsRenderer.createStyledButton("View");

            viewBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    AppointmentManagementFrame parent = (AppointmentManagementFrame) SwingUtilities.getWindowAncestor(table);
                    if (parent != null) {
                        parent.viewAppointment(currentRow);
                    }
                    fireEditingStopped();
                });
            });


            panel.add(viewBtn);
            panel.setBackground(isSelected ? tableParam.getSelectionBackground() : tableParam.getBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }
        
        @Override
        public boolean shouldSelectCell(EventObject e) {
            return false;
        }
    }

    public void showAppointmentsForDay(List<Object[]> appointments) {
        clearSidebarFields();
        if (appointments == null || appointments.isEmpty()) {
            return;
        }
        // Show all appointments in the sidebar as a multi-line label
        StringBuilder sb = new StringBuilder("<html>");
        for (Object[] appt : appointments) {
            // appt[0]=id, appt[1]=serviceDesc, appt[2]=time, appt[3]=display, appt[4]=status (add status if not present)
            String id = appt[0].toString();
            String serviceDesc = appt[1].toString();
            String time = appt[2].toString();
            String status = appt.length > 4 ? appt[4].toString() : "";
            sb.append("<b>").append(id).append("</b><br>")
            .append(time).append(" - ").append(serviceDesc)
            .append("<br>Status: ").append(status)
            .append("<br><br>");
        }
        sb.append("</html>");
        // You can use a dedicated JLabel in your sidebar for this, or show in a dialog:
        JOptionPane.showMessageDialog(this, sb.toString(), "Appointments for Selected Day", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setEditMode(boolean enabled) {
        editMode = enabled;
        
        // Make appointment ID field always read-only (should not be edited)
        fieldInputs.get("Appointment ID:").setEditable(false);
        
        // Make other fields editable or read-only based on edit mode
        fieldInputs.get("Patient Full Name:").setEditable(enabled);
        fieldInputs.get("Service Desc.:").setEditable(enabled);
        fieldInputs.get("Email:").setEditable(enabled);
        fieldInputs.get("Appointment Date:").setEditable(enabled);
        fieldInputs.get("Appointment Time:").setEditable(enabled);
        
        statusComboBox.setEnabled(enabled); // Enable status dropdown only in edit mode

        // Change background color to indicate editable state
        for (JTextField field : sidebarFields) {
            if (field.isEditable()) {
                field.setBackground(new Color(255, 255, 220)); // Light yellow for edit mode
            } else {
                field.setBackground(Color.WHITE);
            }
        }
        
        // Show or hide save/cancel buttons based on edit mode
        if (saveButton != null && cancelButton != null) {
            saveButton.setVisible(enabled);
            cancelButton.setVisible(enabled);
        }
    }

    private void saveAppointmentChanges(ActionEvent e) {
        try {
            // Get the appointment ID from the first field
            String appointmentId = fieldInputs.get("Appointment ID:").getText();
            
            if (appointmentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid appointment ID!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create appointment object from fields
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(appointmentId);
            appointment.setPatientId(fieldInputs.get("Patient Full Name:").getText());
            appointment.setServiceId(fieldInputs.get("Service Description:").getText());
            appointment.setScheduledBy(fieldInputs.get("Email:").getText());
            appointment.setAppointmentDate(fieldInputs.get("Appointment Date:").getText());
            appointment.setAppointmentTime(fieldInputs.get("Appointment Time:").getText());
            appointment.setAppointmentDateTime(
                appointment.getAppointmentDate() + " " + appointment.getAppointmentTime()
            );
            appointment.setStatus(statusComboBox.getSelectedItem().toString());
            
            // Set the internal ID
            if (currentEditingRow >= 0 && currentEditingRow < appointmentIds.size()) {
                appointment.setInternalId(appointmentIds.get(currentEditingRow));
            }
            
            // Validate appointment data
            if (appointment.getPatientId().isEmpty() || 
                appointment.getServiceId().isEmpty() || 
                appointment.getAppointmentDate().isEmpty() || 
                appointment.getAppointmentTime().isEmpty()) {
                
                JOptionPane.showMessageDialog(this, 
                    "Patient ID, Service ID, Date and Time are required fields!", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update appointment in database
            boolean success = appointmentDAO.updateAppointment(appointment);
            
            if (success) {
                // Exit edit mode
                setEditMode(false);
                
                // Refresh the tables to show updated data
                loadData();
                
                JOptionPane.showMessageDialog(this, 
                    "Appointment updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update appointment!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error saving appointment changes: " + ex.getMessage(), 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cancelEdit(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel editing? All changes will be lost.",
            "Confirm Cancel",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            setEditMode(false);
            
            // Refresh the fields from the table data if there's a selected row
            if (currentEditingRow >= 0) {
                viewAppointment(currentEditingRow);
            } else {
                clearSidebarFields();
            }
        }
    }
        
    // Keep the old appointment method for backward compatibility
    public void appointment() {
        initialize();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AppointmentManagementFrame();
        });
    }
}