package Frames;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TableModelListener;
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
    private List<Integer> upcomingAppointmentIds = new ArrayList<>();
    private List<Integer> completedAppointmentIds = new ArrayList<>();
    private List<Integer> canceledAppointmentIds = new ArrayList<>();
    private int activeTabIndex = 0;

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

    private boolean editMode = false;

    //appointment status
    private JComboBox<String> statusComboBox;
    private JLabel createdAtLabel;
    private JLabel updatedAtLabel;

    private JTabbedPane tabbedPane;

    private JLabel statusValueLabel;

    private boolean isInitialized = false; 

    private JPanel sidebarPanel;
        
    public AppointmentManagementFrame() {
        appointmentDAO = new AppointmentDAO(); // Fixed: Changed to lowercase
        patientDAO = new PatientDAO(); // Added for patient lookup
        servicesDAO = new ServicesDAO();
        accountDAO = new AccountManagementDAO();
        patientDAO = new PatientDAO();
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

    // Add this method to your AppointmentManagementFrame class
    private void debugStatus(String message) {
        System.out.println("[STATUS DEBUG " + new java.text.SimpleDateFormat("HH:mm:ss.SSS").format(new java.util.Date()) + "] " + message);
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
        
        JTabbedPane tabbedPane = createTabsPanel();
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        sidebarPanel = createSidebarPanel();
        contentPanel.add(sidebarPanel, BorderLayout.EAST);

        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String selectedTitle = tabbedPane.getTitleAt(selectedIndex);
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
        tabbedPane = new JTabbedPane();

        // Create table models - Updated column names to match actual data
        String[] appointmentColumns = {"Appointment ID", "Patient Full Name", "Service Name", "Date", "Time", "Email", "Status", "Actions"};
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

        tabbedPane.addChangeListener(e -> {
            activeTabIndex = tabbedPane.getSelectedIndex();
            // Clear sidebar when switching tabs
            clearSidebarFields();
            debugStatus("Switched to tab index: " + activeTabIndex);
        });

        // Create the TableModelListener as a separate variable
        final TableModelListener[] statusListenerRef = new TableModelListener[1];

        statusListenerRef[0] = e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 6) {
                int row = e.getFirstRow();
                String appointmentId = (String) upcomingModel.getValueAt(row, 0);
                String newStatus = (String) upcomingModel.getValueAt(row, 6);
                
                debugStatus("Status change detected for appointment: " + appointmentId);
                debugStatus("New status requested: " + newStatus);
                
                // Store the old status to revert if needed
                String oldStatus = "";
                try {
                    int internalId = upcomingAppointmentIds.get(row);
                    debugStatus("Looking up appointment with internal ID: " + internalId);
                    
                    Appointment appointment = appointmentDAO.getAppointmentById(internalId);
                    if (appointment != null) {
                        oldStatus = appointment.getStatus();
                        debugStatus("Current status in DB: " + oldStatus);
                        
                        // Show confirmation dialog
                        int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to change the status from \"" + oldStatus + "\" to \"" + newStatus + "\"?",
                            "Confirm Status Change",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                        );
                        
                        debugStatus("User response: " + (confirm == JOptionPane.YES_OPTION ? "YES" : "NO"));
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            // User confirmed, update the status
                            debugStatus("Setting status to: " + newStatus);
                            appointment.setStatus(newStatus);
                            
                            debugStatus("Sending update to database...");
                            boolean updateSuccess = appointmentDAO.updateAppointment(appointment);
                            debugStatus("Database update result: " + (updateSuccess ? "SUCCESS" : "FAILED"));
                            
                            if (updateSuccess) {
                                debugStatus("Refreshing UI data...");
                                loadData();
                                
                                // Switch to the appropriate tab based on the new status
                                if ("Completed".equalsIgnoreCase(newStatus)) {
                                    debugStatus("Switching to Completed tab");
                                    tabbedPane.setSelectedIndex(1); // Index for "Completed" tab
                                } else if ("Canceled".equalsIgnoreCase(newStatus)) {
                                    debugStatus("Switching to Canceled tab");
                                    tabbedPane.setSelectedIndex(2); // Index for "Canceled" tab
                                }
                                debugStatus("Status update complete");
                            }
                        } else {
                            // User canceled, revert the change in the table
                            debugStatus("Reverting UI back to: " + oldStatus);
                            upcomingModel.removeTableModelListener(statusListenerRef[0]);
                            upcomingModel.setValueAt(oldStatus, row, 6); // Revert to old status
                            upcomingModel.addTableModelListener(statusListenerRef[0]);
                        }
                    } else {
                        debugStatus("ERROR: Appointment not found with ID: " + internalId);
                    }
                } catch (Exception ex) {
                    debugStatus("EXCEPTION during status update: " + ex.getMessage());
                    debugStatus("Exception type: " + ex.getClass().getName());
                    ex.printStackTrace();
                    
                    // Revert to old status in case of error
                    debugStatus("Reverting UI due to error");
                    upcomingModel.removeTableModelListener(statusListenerRef[0]);
                    upcomingModel.setValueAt(oldStatus, row, 6);
                    upcomingModel.addTableModelListener(statusListenerRef[0]);
                }
            }
        };

        // Add the listener to the model
        upcomingModel.addTableModelListener(statusListenerRef[0]);

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
        tabbedPane.addTab("UPCOMING", new JScrollPane(upcomingTable));
        tabbedPane.addTab("COMPLETED", new JScrollPane(completedTable));
        tabbedPane.addTab("CANCELED", new JScrollPane(canceledTable));
        tabbedPane.addTab("CALENDAR", new CalendarPanel());

        setupTableListeners();
        return tabbedPane;
    }

    private void setupTableListeners() {
        // Setup table selection listeners for each tab
        upcomingTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = upcomingTable.getSelectedRow();
                if (selectedRow >= 0) {
                    activeTabIndex = 0; // Ensure upcoming tab is active
                    viewAppointment(selectedRow);
                }
            }
        });
        
        completedTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = completedTable.getSelectedRow();
                if (selectedRow >= 0) {
                    activeTabIndex = 1; // Ensure completed tab is active
                    viewAppointment(selectedRow);
                }
            }
        });
        
        canceledTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = canceledTable.getSelectedRow();
                if (selectedRow >= 0) {
                    activeTabIndex = 2; // Ensure canceled tab is active
                    viewAppointment(selectedRow);
                }
            }
        });
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
        
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(buttonPanel);
        
        return sidebarPanel;
    }
    
    private void createSidebarFields(JPanel parent) {
        // Updated to match appointment data structure
        String[] fieldLabels = {
            "Appointment ID:", 
            "Patient Full Name:", 
            "Service Name:", 
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

        upcomingAppointmentIds.clear();
        completedAppointmentIds.clear();
        canceledAppointmentIds.clear();

        for (Appointment appointment : appointments) {
            // Fetch patient full name - improved error handling
            String patientFullName = "";
            try {
                // First try to get from already set patientFullName field if it exists
                if (appointment.getPatientFullName() != null && !appointment.getPatientFullName().isEmpty()) {
                    patientFullName = appointment.getPatientFullName();
                } else {
                    // If not available, try to fetch from patient table
                    var patient = patientDAO.getPatientByFormattedId(appointment.getPatientId());
                    if (patient != null) {
                        patientFullName = patient.getFullName();
                        // Also set it on the appointment object for future use
                        appointment.setPatientFullName(patientFullName);
                    } else {
                        // Last resort, display the ID with a note
                        patientFullName = "Patient: " + appointment.getPatientId();
                    }
                }
            } catch (Exception ex) {
                // If anything fails, at least display something useful
                patientFullName = "Patient: " + appointment.getPatientId();
                System.err.println("Error fetching patient name: " + ex.getMessage());
            }

            // Change from serviceDesc to serviceName
            String serviceName = appointment.getServiceId();
            try {
                var service = servicesDAO.getServiceByFormattedId(appointment.getServiceId());
                if (service != null && service.getServiceName() != null) {
                    serviceName = service.getServiceName();
                }
            } catch (Exception ex) {
                serviceName = appointment.getServiceId();
            }

            // Fetch patient email
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
                serviceName,  // Changed from serviceDesc to serviceName
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                email,
                appointment.getStatus(),
                "Actions"
            };
            // appointmentIds.add(appointment.getInternalId());

            String status = appointment.getStatus();
            if ("Upcoming".equalsIgnoreCase(status)) {
                upcomingModel.addRow(rowData);
                upcomingAppointmentIds.add(appointment.getInternalId());
            } else if ("Completed".equalsIgnoreCase(status)) {
                completedModel.addRow(rowData);
                completedAppointmentIds.add(appointment.getInternalId());
            } else if ("Canceled".equalsIgnoreCase(status)) {
                canceledModel.addRow(rowData);
                canceledAppointmentIds.add(appointment.getInternalId());
            }
        }
        clearSidebarFields();
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

            if(row < 0){
                clearSidebarFields();
                return;
            }

            List<Integer> activeAppointmentIds;

            switch (activeTabIndex) {
                case 0:
                    activeAppointmentIds = upcomingAppointmentIds;
                    break;
                case 1:
                    activeAppointmentIds = completedAppointmentIds;
                    break;
                case 2:
                    activeAppointmentIds = canceledAppointmentIds;
                    break;
                default:
                    activeAppointmentIds = upcomingAppointmentIds;
                    break;
            }

            if (activeAppointmentIds != null && row < activeAppointmentIds.size()) {
                int appointmentInternalId = activeAppointmentIds.get(row);
                Appointment appointment = appointmentDAO.getAppointmentById(appointmentInternalId);

                if (appointment != null) {
                    fieldInputs.get("Appointment ID:").setText(appointment.getAppointmentId() != null ? appointment.getAppointmentId() : "");
                    
                    // Use the patientFullName and patientEmail fields directly
                    fieldInputs.get("Patient Full Name:").setText(appointment.getPatientFullName() != null ? appointment.getPatientFullName() : "");
                    fieldInputs.get("Email:").setText(appointment.getPatientEmail() != null ? appointment.getPatientEmail() : "");
                    
                    // Service name is already handled
                    fieldInputs.get("Service Name:").setText(appointment.getServiceId() != null ? appointment.getServiceId() : "");
                    fieldInputs.get("Appointment Date:").setText(appointment.getAppointmentDate() != null ? appointment.getAppointmentDate() : "");
                    fieldInputs.get("Appointment Time:").setText(appointment.getAppointmentTime() != null ? appointment.getAppointmentTime() : "");

                    String status = appointment.getStatus();
                    if (statusValueLabel != null) {
                        statusValueLabel.setText(status != null ? status : "N/A");
                    }
                    
                    if (createdAtLabel != null) {
                        createdAtLabel.setText(appointment.getCreatedAt() != null ? appointment.getCreatedAt() : "N/A");
                    }
                    
                    if (updatedAtLabel != null) {
                        updatedAtLabel.setText(appointment.getUpdatedAt() != null ? appointment.getUpdatedAt() : "N/A");
                    }
                }
            }else{
                clearSidebarFields();
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
                List<Integer> activeAppointmentIds = null;
                
                switch (activeTabIndex) {
                    case 0:
                        activeAppointmentIds = upcomingAppointmentIds;
                        break;
                    case 1:
                        activeAppointmentIds = completedAppointmentIds;
                        break;
                    case 2:
                        activeAppointmentIds = canceledAppointmentIds;
                        break;
                    default:
                        activeAppointmentIds = upcomingAppointmentIds;
                        break;
                }
                
                if (activeAppointmentIds != null && row >= 0 && row < activeAppointmentIds.size()) {
                    int appointmentInternalId = activeAppointmentIds.get(row);
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
        // Add null checks for these labels
        if (createdAtLabel != null) {
            createdAtLabel.setText("N/A");
        }
        if (updatedAtLabel != null) {
            updatedAtLabel.setText("N/A");
        }
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
            // appt[0]=id, appt[1]=serviceName, appt[2]=time, appt[3]=display, appt[4]=status (add status if not present)
            String id = appt[0].toString();
            String serviceName = appt[1].toString();
            String time = appt[2].toString();
            String status = appt.length > 4 ? appt[4].toString() : "";
            sb.append("<b>").append(id).append("</b><br>")
            .append(time).append(" - ").append(serviceName)
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
        fieldInputs.get("Service Name:").setEditable(enabled);
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
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AppointmentManagementFrame();
        });
    }
}