package Frames;

import DAO.PatientManagementDAO;
import DAO.PatientManagementDAO.Patient;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.EventObject;

public class PatientManagementFrame extends JFrame {
    final private Font mainFont = new Font("Segoe Ui", Font.PLAIN, 16);
    final private Font titleFont = new Font("Segoe Ui", Font.BOLD, 36);
    final private Font btnFont = new Font("Segoe Ui", Font.PLAIN, 14);
    final private Color BLUE_COLOR = Color.decode("#1167B1");
    
    // DAO and data management
    private PatientManagementDAO patientDAO;
    private DefaultTableModel tableModel;
    private JTable table;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private List<Integer> patientIds; // Store patient IDs corresponding to table rows
    
    // UI Components
    private JTextField searchBar;
    private ArrayList<JTextField> sidebarFields;

    public PatientManagementFrame() {
        // Initialize DAO and data structures
        patientDAO = new PatientManagementDAO();
        patientIds = new ArrayList<>();
        
        initialize();
        
        // Load data from database
        SwingUtilities.invokeLater(this::loadData);
    }

    public void initialize() {
        getContentPane().setBackground(Color.decode("#E1E3E5"));

        // --- Logo and Admin label row ---
        JLabel logoImage = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/images/smalllogonotext.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            icon = new ImageIcon(icon.getImage().getScaledInstance(170, 50, Image.SCALE_SMOOTH));
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

        JPanel adminLabelPanel = new JPanel(new BorderLayout());
        adminLabelPanel.setOpaque(false);
        adminLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        adminLabelPanel.add(logoAdmin, BorderLayout.CENTER);

        JPanel topHeaderRow = new JPanel(new BorderLayout());
        topHeaderRow.setOpaque(false);
        topHeaderRow.add(logoPanel, BorderLayout.WEST);
        topHeaderRow.add(adminLabelPanel, BorderLayout.EAST);

        // --- Blue panel with title, subtitle, search, and home button ---
        JLabel mainHeader = new JLabel("Patient Management");
        mainHeader.setFont(titleFont);
        mainHeader.setForeground(Color.WHITE);
        JLabel subHeader = new JLabel("View, Edit, and Delete Patient Records");
        subHeader.setFont(mainFont);
        subHeader.setForeground(Color.WHITE);

        JPanel headers_homebtn_Panel = new JPanel();
        headers_homebtn_Panel.setLayout(new BoxLayout(headers_homebtn_Panel, BoxLayout.Y_AXIS));
        headers_homebtn_Panel.setOpaque(false);
        headers_homebtn_Panel.add(mainHeader);
        headers_homebtn_Panel.add(subHeader);
        headers_homebtn_Panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        searchBar = new JTextField();
        searchBar.setFont(mainFont);
        searchBar.setBackground(Color.WHITE);
        searchBar.setForeground(BLUE_COLOR);
        searchBar.setMaximumSize(new Dimension(300, 30));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(btnFont);
        searchBtn.setBackground(BLUE_COLOR);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setMaximumSize(new Dimension(100, 30));
        searchBtn.setMargin(new Insets(2, 2, 2, 2));
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(this::performSearch);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(btnFont);
        refreshBtn.setBackground(BLUE_COLOR);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setMaximumSize(new Dimension(100, 30));
        refreshBtn.setMargin(new Insets(2, 2, 2, 2));
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(this::refreshData);

        JButton homeBtn = new JButton("Back to Home");
        homeBtn.setFont(btnFont);
        homeBtn.setBackground(BLUE_COLOR);
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setMaximumSize(new Dimension(140, 30));
        homeBtn.setMargin(new Insets(1, 4, 1, 4));
        homeBtn.setFocusPainted(false);
        homeBtn.addActionListener(this::goBackToHome);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        searchPanel.add(searchBar);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(refreshBtn);

        Box blueContentBox = Box.createVerticalBox();
        blueContentBox.setOpaque(false);
        headers_homebtn_Panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        blueContentBox.add(headers_homebtn_Panel);
        blueContentBox.add(Box.createVerticalStrut(18));
        blueContentBox.add(searchPanel);

        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.Y_AXIS));
        adminPanel.setOpaque(false);
        homeBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        adminPanel.add(Box.createVerticalStrut(32));
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.setOpaque(false);
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(homeBtn);
        adminPanel.add(btnPanel);

        JPanel bluePanel = new JPanel(new BorderLayout());
        bluePanel.setBackground(Color.decode("#2A9DF4"));
        bluePanel.add(blueContentBox, BorderLayout.WEST);
        bluePanel.add(adminPanel, BorderLayout.EAST);
        bluePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        bluePanel.setPreferredSize(new Dimension(0, 180));

        // --- Header panel to hold logoPanel and bluePanel vertically ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(topHeaderRow, BorderLayout.NORTH);
        headerPanel.add(bluePanel, BorderLayout.CENTER);

        // --- Create Table ---
        createTable();

        // --- Create Sidebar ---
        JPanel sidebarPanel = createSidebarPanel();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.decode("#E1E3E5"));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 5));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        // --- Add panels to frame ---
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(sidebarPanel, BorderLayout.EAST);

        setTitle("Patient Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void createTable() {
        String[] columnNames = {"Patient ID", "First Name", "Middle Name", "Last Name", "Birthday", "Gender", "Actions"};
        Object[][] data = {};

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only Actions column is editable
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.getTableHeader().setBackground(BLUE_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);

        tableSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(tableSorter);

        // Set column widths
        table.getColumn("Patient ID").setPreferredWidth(80);
        table.getColumn("Patient ID").setMinWidth(80);
        table.getColumn("Patient ID").setMaxWidth(100);
        table.getColumn("Actions").setPreferredWidth(140);
        table.getColumn("Actions").setMinWidth(140);
        table.getColumn("Actions").setMaxWidth(140);

        // Set up actions column
        table.getColumn("Actions").setCellRenderer(new ActionsRenderer());
        table.getColumn("Actions").setCellEditor(new ActionsEditor(table, sidebarFields));
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.decode("#D0EFFF"));
        sidebarPanel.setPreferredSize(new Dimension(260, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(20, 2, 0, 0, Color.decode("#E1E3E5")),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel sidebarTitle = new JLabel("View Patient Record");
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sidebarTitle.setForeground(Color.decode("#192F8F"));
        sidebarTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(sidebarTitle);
        sidebarPanel.add(Box.createVerticalStrut(15));

        createSidebarFields(sidebarPanel);

        return sidebarPanel;
    }

    private void createSidebarFields(JPanel sidebarPanel) {
        String[] fieldLabels = {"Patient ID:", "First Name:", "Middle Name:", "Last Name:", "Birthday:", "Gender:"};
        sidebarFields = new ArrayList<>();

        for (String label : fieldLabels) {
            JPanel fieldPanel = createFieldPanel(label);
            sidebarPanel.add(fieldPanel);
            sidebarPanel.add(Box.createVerticalStrut(8));
        }
    }

    private JPanel createFieldPanel(String labelText) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        
        JLabel fieldLabel = new JLabel(labelText);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldLabel.setForeground(Color.BLACK);
        
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setMaximumSize(new Dimension(450, 30));
        textField.setBackground(Color.WHITE);
        textField.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));

        fieldPanel.add(fieldLabel);
        fieldPanel.add(textField);
        sidebarFields.add(textField);

        return fieldPanel;
    }

    // Data Management Methods - DAO Implementation
    private void loadData() {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<Patient> patients = patientDAO.getAllPatients();
            populateTable(patients);
            System.out.println("Loaded " + patients.size() + " patients from database");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading patient data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void populateTable(List<Patient> patients) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0); // Clear existing data
            patientIds.clear(); // Clear patient IDs
            
            for (Patient patient : patients) {
                tableModel.addRow(new Object[]{
                    patient.getId(), // Patient ID
                    patient.getFirstName(),
                    patient.getMiddleName() != null ? patient.getMiddleName() : "",
                    patient.getLastName(),
                    patient.getBirthday(),
                    patient.getGender(),
                    null // Actions column
                });
                patientIds.add(patient.getId()); // Store patient ID for this row
            }
        });
    }

    // Event Handlers
    private void performSearch(ActionEvent e) {
        String searchText = searchBar.getText().trim();
        
        if (searchText.isEmpty()) {
            loadData(); // Reload all data
            return;
        }

        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            List<Patient> patients = patientDAO.searchPatients(searchText);
            populateTable(patients);
            System.out.println("Search found " + patients.size() + " patients for: " + searchText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error searching patients: " + ex.getMessage(), 
                "Search Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void refreshData(ActionEvent e) {
        searchBar.setText("");
        loadData(); // Reload from database
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
            // TODO: Navigate to home screen
            // new HomeFrame().setVisible(true);
        }
    }

    private void viewPatient(int row) {
        try {
            // Ensure row is valid
            if (row < 0 || row >= table.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Invalid row selected!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Fill sidebar fields with selected row data
            for (int i = 0; i < sidebarFields.size() && i < table.getColumnCount() - 1; i++) {
                Object val = table.getValueAt(row, i);
                sidebarFields.get(i).setText(val != null ? val.toString() : "");
            }
            
            System.out.println("Viewing patient at row: " + row); // Debug output
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error viewing patient: " + e.getMessage(), 
                "View Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deletePatient(int row) {
        try {
            // Ensure row is valid
            if (row < 0 || row >= table.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Invalid row selected!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Get patient info for confirmation
            String firstName = table.getValueAt(row, 1).toString();
            String lastName = table.getValueAt(row, 3).toString();
            String gender = table.getValueAt(row, 5).toString();
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this patient record?\n\n" +
                "Name: " + firstName + " " + lastName + "\n" +
                "Gender: " + gender + "\n\n" +
                "This action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Get the patient ID for this row
                int patientId = patientIds.get(row);
                
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                // Delete from database
                boolean success = patientDAO.deletePatient(patientId);
                
                setCursor(Cursor.getDefaultCursor());
                
                if (success) {
                    // Remove from table and ID list
                    tableModel.removeRow(row);
                    patientIds.remove(row);
                    clearSidebarFields();
                    JOptionPane.showMessageDialog(this, 
                        "Patient record deleted successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete patient record from database!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (Exception ex) {
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, 
                "Error deleting patient: " + ex.getMessage(), 
                "Delete Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearSidebarFields() {
        for (JTextField field : sidebarFields) {
            field.setText("");
        }
    }

    // Inner Classes for Table Actions
    static class ActionsEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private final JTable table;
        private final List<JTextField> sidebarFields;
        private int currentRow;

        public ActionsEditor(JTable table, List<JTextField> sidebarFields) {
            this.table = table;
            this.sidebarFields = sidebarFields;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            this.currentRow = row;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
            panel.setOpaque(true);

            JButton viewBtn = createStyledButton("View");
            JButton deleteBtn = createStyledButton("Delete");

            viewBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    PatientManagementFrame parent = (PatientManagementFrame) SwingUtilities.getWindowAncestor(table);
                    if (parent != null) {
                        parent.viewPatient(currentRow);
                    }
                    fireEditingStopped();
                });
            });

            deleteBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    PatientManagementFrame parent = (PatientManagementFrame) SwingUtilities.getWindowAncestor(table);
                    if (parent != null) {
                        parent.deletePatient(currentRow);
                    }
                    fireEditingStopped();
                });
            });

            panel.add(viewBtn);
            panel.add(deleteBtn);
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }

        private JButton createStyledButton(String text) {
            JButton button = new JButton(text);
            button.setFocusable(false);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button.setBackground(Color.decode("#1167B1"));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setPreferredSize(new Dimension(60, 25));
            button.setMargin(new Insets(2, 4, 2, 4));
            
            // Add hover effects for better UX
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(Color.decode("#0d5a9f"));
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(Color.decode("#1167B1"));
                }
            });
            
            return button;
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

        private JButton createStyledButton(String text) {
            JButton button = new JButton(text);
            button.setFocusable(false);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button.setBackground(Color.decode("#1167B1"));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setPreferredSize(new Dimension(60, 25));
            button.setMargin(new Insets(2, 4, 2, 4));
            return button;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            
            if (isSelected) {
                viewBtn.setBackground(Color.decode("#0d5a9f"));
                deleteBtn.setBackground(Color.decode("#0d5a9f"));
            } else {
                viewBtn.setBackground(Color.decode("#1167B1"));
                deleteBtn.setBackground(Color.decode("#1167B1"));
            }
            
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Test database connection before starting
                PatientManagementDAO testDAO = new PatientManagementDAO();
                if (testDAO.testDatabaseOperations()) {
                    new PatientManagementFrame();
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "Database connection failed. Please check your database configuration.", 
                        "Database Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Error starting application: " + e.getMessage(), 
                    "Application Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}