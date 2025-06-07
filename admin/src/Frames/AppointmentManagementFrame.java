package Frames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.EventObject;

import DAO.AppointmentDAO;
import DAO.AppointmentDAO.Appointment;
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

    private boolean isInitialized = false; 
    
    public AppointmentManagementFrame() {
        appointmentDAO = new AppointmentDAO(); // Fixed: Changed to lowercase
        patientDAO = new PatientDAO(); // Added for patient lookup
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
        
        JButton calendarBtn = createStyledButton("View Calendar");
        JButton homeBtn = createStyledButton("Back to Home");
        
        homeBtn.addActionListener(this::goBackToHome);
        
        btnPanel.add(calendarBtn);
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
        
        contentPanel.add(createTabsPanel(), BorderLayout.CENTER);
        contentPanel.add(createSidebarPanel(), BorderLayout.EAST);
        
        return contentPanel;
    }
    
    private JPanel createTabsPanel() {
        JTabbedPane appointmentTabs = new JTabbedPane();
        
        // Create table models - Updated column names to match actual data
        String[] appointmentColumns = {"Appointment ID", "Patient ID", "Service ID", "Date", "Time", "Scheduled By", "Actions"};
        
        upcomingModel = new DefaultTableModel(appointmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Actions only
            }
        };
        
        completedModel = new DefaultTableModel(appointmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Actions only
            }
        };
        
        canceledModel = new DefaultTableModel(appointmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Actions only
            }
        };
        
        // Create tables
        upcomingTable = createTable(upcomingModel);
        completedTable = createTable(completedModel);
        canceledTable = createTable(canceledModel);
        
        // Set up renderers and editors
        setupTableActionsColumn(upcomingTable, 6);
        setupTableActionsColumn(completedTable, 6);
        setupTableActionsColumn(canceledTable, 6);
        
        // Add tables to tabs
        appointmentTabs.addTab("ALL APPOINTMENTS", new JScrollPane(upcomingTable));
        appointmentTabs.addTab("COMPLETED", new JScrollPane(completedTable));
        appointmentTabs.addTab("CANCELED", new JScrollPane(canceledTable));
        
        JPanel tabsPanel = new JPanel(new BorderLayout());
        tabsPanel.setOpaque(false);
        tabsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        tabsPanel.add(appointmentTabs, BorderLayout.CENTER);
        
        return tabsPanel;
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
        
        return table;
    }
    
    private void setupTableActionsColumn(JTable table, int actionsColumn) {
        table.getColumn("Actions").setCellRenderer(new ActionsRenderer());
        table.getColumn("Actions").setCellEditor(new ActionsEditor(table, sidebarFields));
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
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
        
        return sidebarPanel;
    }
    
    private void createSidebarFields(JPanel parent) {
        // Updated to match appointment data structure
        String[] fieldLabels = {
            "Appointment ID:", 
            "Patient ID:", 
            "Service ID:", 
            "Scheduled By:", 
            "Appointment Date:", 
            "Appointment Time:", 
            "Created At:", 
            "Updated At:"
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
            // Fixed: Updated to use actual appointment data structure
            Object[] rowData = {
                appointment.getAppointmentId(),    // Hidden column
                appointment.getPatientId(),
                appointment.getServiceId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getScheduledBy(),
                "Actions"
            };
            
            appointmentIds.add(appointment.getInternalId()); // Fixed: Use getInternalId()
            
            // For now, add all appointments to the main tab
            // Later you can implement status-based filtering
            upcomingModel.addRow(rowData);
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
            if (row >= 0 && row < appointmentIds.size()) {
                int appointmentInternalId = appointmentIds.get(row);
                Appointment appointment = appointmentDAO.getAppointmentById(appointmentInternalId);
                
                if (appointment != null) {
                    // Updated to display appointment data instead of patient data
                    fieldInputs.get("Appointment ID:").setText(appointment.getAppointmentId() != null ? appointment.getAppointmentId() : "");
                    fieldInputs.get("Patient ID:").setText(appointment.getPatientId() != null ? appointment.getPatientId() : "");
                    fieldInputs.get("Service ID:").setText(appointment.getServiceId() != null ? appointment.getServiceId() : "");
                    fieldInputs.get("Scheduled By:").setText(appointment.getScheduledBy() != null ? appointment.getScheduledBy() : "");
                    fieldInputs.get("Appointment Date:").setText(appointment.getAppointmentDate() != null ? appointment.getAppointmentDate() : "");
                    fieldInputs.get("Appointment Time:").setText(appointment.getAppointmentTime() != null ? appointment.getAppointmentTime() : "");
                    fieldInputs.get("Created At:").setText(appointment.getCreatedAt() != null ? appointment.getCreatedAt() : "N/A");
                    fieldInputs.get("Updated At:").setText(appointment.getUpdatedAt() != null ? appointment.getUpdatedAt() : "N/A");
                    
                    // Optional: Show a success message
                    // JOptionPane.showMessageDialog(this, "Appointment details loaded successfully!", "View Appointment", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    clearSidebarFields();
                    JOptionPane.showMessageDialog(this, 
                        "Appointment not found.", 
                        "Appointment Not Found", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
        }catch (Exception e) {
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
    }
    
    // Inner Classes for Table Actions
    static class ActionsRenderer extends JPanel implements TableCellRenderer {
        private final JButton viewBtn;
        private final JButton deleteBtn;

        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 0));
            setOpaque(true);

            viewBtn = createStyledButton("View");
            deleteBtn = createStyledButton("Delete");

            add(viewBtn);
            add(deleteBtn);
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
                deleteBtn.setBackground(Color.decode("#0E4A7A"));
            } else {
                setBackground(table.getBackground());
                viewBtn.setBackground(BLUE_COLOR);
                deleteBtn.setBackground(BLUE_COLOR);
            }
            return this;
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
            JButton deleteBtn = ActionsRenderer.createStyledButton("Delete");

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