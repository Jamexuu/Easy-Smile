package Frames;

import DAO.PatientDAO;
import DAO.PatientDAO.Patient;
import DAO.AccountManagementDAO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.EventObject;
import java.util.HashMap;

public class PatientManagementFrame extends JFrame {
    final private Font mainFont = new Font("Segoe Ui", Font.PLAIN, 16);
    final private Font titleFont = new Font("Segoe Ui", Font.BOLD, 36);
    final private Font btnFont = new Font("Segoe Ui", Font.PLAIN, 14);
    final private Color BLUE_COLOR = Color.decode("#1167B1");
    
    // DAO and data management
    private PatientDAO patientDAO;
    private DefaultTableModel tableModel;
    private JTable table;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private List<Integer> patientIds; // Store patient IDs corresponding to table rows
    
    // UI Components
    private JTextField searchBar;
    private ArrayList<JTextField> sidebarFields;

    private boolean isInitialized = false; 

    private int editingRow = -1; // Track currently selected row for editing

    public PatientManagementFrame() {
        patientDAO = new PatientDAO();
        patientIds = new ArrayList<>();
        
        initialize();
        
        // Load data from database
        SwingUtilities.invokeLater(this::loadData);
    }

    public void initialize() {
        getContentPane().setBackground(Color.decode("#E1E3E5"));

        if (isInitialized) {
            return;
        }

        isInitialized = true;

        getContentPane().removeAll();

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
        String[] columnNames = {"Patient ID", "First Name", "Middle Name", "Last Name", "Birthday", "Gender", "Barangay", "City", "Province", "Created By:", "Actions"};
        Object[][] data = {};
        
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 10; // Only Actions column is editable
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.getTableHeader().setBackground(BLUE_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);

        tableSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(tableSorter);

        // Set column widths
        table.getColumn("Patient ID").setPreferredWidth(100);
        table.getColumn("Patient ID").setMinWidth(100);
        table.getColumn("Patient ID").setMaxWidth(100);

        table.getColumn("Actions").setPreferredWidth(180);
        table.getColumn("Actions").setMinWidth(180);
        table.getColumn("Actions").setMaxWidth(180);

        // Set up actions column
        table.getColumn("Actions").setCellRenderer(new ActionsRenderer());
        table.getColumn("Actions").setCellEditor(new ActionsEditor(table, sidebarFields));

        // Make all columns unresizable
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setResizable(false);
        }
    }

    private JButton saveButton;
    private JButton cancelButton;
    @SuppressWarnings("unused")
    private boolean editMode = false;

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.decode("#D0EFFF"));
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

        // Add Save and Cancel buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        saveButton = new JButton("Save Changes");
        saveButton.setFont(btnFont);
        saveButton.setBackground(BLUE_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setEnabled(false);
        saveButton.addActionListener(this::savePatientChanges);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(btnFont);
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(this::cancelEdit);

        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cancelButton);

        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(buttonPanel);

        // Wrap the sidebarPanel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(sidebarPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scrolling
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Enable vertical scrolling
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove default border

        // Create a container panel to hold the scroll pane
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(Color.decode("#D0EFFF"));
        containerPanel.setPreferredSize(new Dimension(260, 0));
        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return containerPanel;
    }

    private void createSidebarFields(JPanel sidebarPanel) {
        String[] fieldLabels = {"Patient ID:", "First Name:", "Middle Name:", "Last Name:", "Birthday:", 
                                "Gender:", "Created By:", "Barangay:", "City:", "Province:"};
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
        textField.setEditable(false);

        fieldPanel.add(fieldLabel);
        fieldPanel.add(textField);
        sidebarFields.add(textField);

        return fieldPanel;
    }

    private void mapTableDataToSidebar(int row) {
        if (row < 0 || row >= table.getRowCount()) {
            return;
        }
        
        // Define explicit mappings: table column index -> sidebar field index
        Map<Integer, Integer> columnToFieldMap = new HashMap<>();
        columnToFieldMap.put(0, 0);   // Patient ID -> Patient ID field
        columnToFieldMap.put(1, 1);   // First Name -> First Name field
        columnToFieldMap.put(2, 2);   // Middle Name -> Middle Name field
        columnToFieldMap.put(3, 3);   // Last Name -> Last Name field
        columnToFieldMap.put(4, 4);   // Birthday -> Birthday field
        columnToFieldMap.put(5, 5);   // Gender -> Gender field
        columnToFieldMap.put(9, 6);   // Created By -> Created By field
        columnToFieldMap.put(6, 7);   // Barangay -> Barangay field
        columnToFieldMap.put(7, 8);   // City -> City field
        columnToFieldMap.put(8, 9);   // Province -> Province field
        
        // Update sidebar fields using the mapping
        columnToFieldMap.forEach((tableCol, fieldIndex) -> {
            Object value = table.getValueAt(row, tableCol);
            sidebarFields.get(fieldIndex).setText(value != null ? value.toString() : "");
        });
    }

    private void editPatient(int row) {
        try {
            if (row < 0 || row >= table.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Invalid row selected!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            mapTableDataToSidebar(row);
            editingRow = row;
            setEditMode(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error editing patient: " + e.getMessage(), "Edit Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void setEditMode(boolean enabled) {
        editMode = enabled;
        
        // Make patient ID field always read-only (should not be edited)
        sidebarFields.get(0).setEditable(false);
        sidebarFields.get(6).setEditable(false);
        
        // Make other fields editable or read-only based on edit mode
        for (int i = 1; i < sidebarFields.size(); i++) {
            if (i != 6) {
                sidebarFields.get(i).setEditable(enabled);
                
                // Change background color to indicate editable state
                if (enabled) {
                    sidebarFields.get(i).setBackground(new Color(255, 255, 220)); // Light yellow for edit mode
                } else {
                    sidebarFields.get(i).setBackground(Color.WHITE);
                }
            }
        }
        
        // Enable/disable save and cancel buttons
        saveButton.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
    }

    private void savePatientChanges(ActionEvent e) {
        try {
            if (editingRow < 0 || editingRow >= patientIds.size()) {
                JOptionPane.showMessageDialog(this, "No Patient selected!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int patientId = patientIds.get(editingRow);

            PatientDAO.Patient patient = new PatientDAO.Patient();
            patient.setInternalId(patientId);
            patient.setFirstName(sidebarFields.get(1).getText().trim());
            patient.setMiddleName(sidebarFields.get(2).getText().trim());
            patient.setLastName(sidebarFields.get(3).getText().trim());
            patient.setBirthDate(sidebarFields.get(4).getText().trim());
            patient.setGender(sidebarFields.get(5).getText().trim());
            // Don't update CreatedBy - just keep it for reference
            patient.setBarangay(sidebarFields.get(7).getText().trim());
            patient.setCity(sidebarFields.get(8).getText().trim());
            patient.setProvince(sidebarFields.get(9).getText().trim());

            // Basic validation
            if (patient.getFirstName().isEmpty() || patient.getLastName().isEmpty()) {
                JOptionPane.showMessageDialog(this, "First Name and Last Name cannot be empty!", 
                                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            boolean success = patientDAO.updatePatient(patient);
            setCursor(Cursor.getDefaultCursor());

            if (success) {
                setEditMode(false);
                populateTable(patientDAO.getAllPatients());
                JOptionPane.showMessageDialog(this, "Patient record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update patient record. No changes were made.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, 
                "Error saving patient data: " + ex.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
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

    // Add method to handle cancel button
    private void cancelEdit(ActionEvent e) {
        // Just disable edit mode and reset form from current selection
        setEditMode(false);

        if (editingRow >= 0) {
            viewPatient(editingRow);
        } else {
            clearSidebarFields();
            editingRow = -1;
        }
    }

    private void populateTable(List<Patient> patients) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0); // Clear existing data
            patientIds.clear(); // Clear patient IDs

            for (Patient patient : patients) {
                tableModel.addRow(new Object[]{
                    patient.getPatientId(),
                    patient.getFirstName(),
                    patient.getMiddleName() != null ? patient.getMiddleName() : "",
                    patient.getLastName(),
                    patient.getBirthDate(),
                    patient.getGender(),
                    patient.getBarangay() != null ? patient.getBarangay() : "",
                    patient.getCity() != null ? patient.getCity() : "",
                    patient.getProvince() != null ? patient.getProvince() : "",
                    patient.getCreatedBy(),
                    null // Actions column
                });
                patientIds.add(patient.getInternalId()); // Store patient ID for this row
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
            SwingUtilities.invokeLater(() -> {
                homepage home = new homepage();
                home.home();
            });
        }
    }

    private void viewPatient(int row) {
        try {
            // Ensure row is valid
            if (row < 0 || row >= table.getRowCount()) {
                JOptionPane.showMessageDialog(this, "Invalid row selected!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            setEditMode(false); // Disable edit mode if it was active

            // Map table data to correct sidebar fields
            sidebarFields.get(0).setText(String.valueOf(table.getValueAt(row, 0))); // Patient ID
            sidebarFields.get(1).setText(String.valueOf(table.getValueAt(row, 1))); // First Name
            sidebarFields.get(2).setText(String.valueOf(table.getValueAt(row, 2))); // Middle Name
            sidebarFields.get(3).setText(String.valueOf(table.getValueAt(row, 3))); // Last Name
            sidebarFields.get(4).setText(String.valueOf(table.getValueAt(row, 4))); // Birthday
            sidebarFields.get(5).setText(String.valueOf(table.getValueAt(row, 5))); // Gender
            sidebarFields.get(6).setText(String.valueOf(table.getValueAt(row, 9))); // Created By (column 9)
            sidebarFields.get(7).setText(String.valueOf(table.getValueAt(row, 6))); // Barangay (column 6)
            sidebarFields.get(8).setText(String.valueOf(table.getValueAt(row, 7))); // City (column 7)
            sidebarFields.get(9).setText(String.valueOf(table.getValueAt(row, 8))); // Province (column 8)

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
            
            Object firstNameObj = table.getValueAt(row, 1);
            Object lastNameObj = table.getValueAt(row, 3);
            // Object middleNameObj = table.getValueAt(row, 2);
            Object genderObj = table.getValueAt(row, 5);
            
            // Get patient info for confirmation
            String firstName = (firstNameObj != null) ? firstNameObj.toString() : "";
            
            String lastName = (lastNameObj != null) ? lastNameObj.toString() : "";
            String gender = (genderObj != null) ? genderObj.toString() : "";
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this patient record?\n\n" +
                "Name: " + firstName + " " + lastName + "\n" +
                "Gender: " + gender + "\n\n" +
                "WARNING: All appointments for this patient will also be deleted.\n" +
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
        private final JTable tableInstance;
        private int currentRow;

        public ActionsEditor(JTable table, List<JTextField> sidebarFields) {
            this.tableInstance = table;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            this.currentRow = row;
            panel = new JPanel(new GridLayout(1, 3, 3, 0));
            panel.setOpaque(true);

            JButton viewBtn = createStyledButton("View");
            JButton editBtn = createStyledButton("Edit");
            JButton deleteBtn = createStyledButton("Delete");

            viewBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    PatientManagementFrame parent = (PatientManagementFrame) SwingUtilities.getWindowAncestor(tableInstance);
                    if (parent != null) {
                        parent.viewPatient(currentRow);
                    }
                    fireEditingStopped();
                });
            });

            editBtn.addActionListener(e -> {  // Add action for edit button
                SwingUtilities.invokeLater(() -> {
                    PatientManagementFrame parent = (PatientManagementFrame) SwingUtilities.getWindowAncestor(tableInstance);
                    if (parent != null) {
                        parent.editPatient(currentRow);
                    }
                    fireEditingStopped();
                });
            });

            deleteBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    PatientManagementFrame parent = (PatientManagementFrame) SwingUtilities.getWindowAncestor(tableInstance);
                    if (parent != null) {
                        parent.deletePatient(currentRow);
                    }
                    fireEditingStopped();
                });
            });

            panel.add(viewBtn);
            panel.add(editBtn);
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
            button.setPreferredSize(new Dimension(60, 20));
            button.setMargin(new Insets(0, 4, 0, 4));
            
            if("Delete".equals(text)) {
                button.setBackground(Color.decode("#D9534F")); // Red for delete
            } else {
                button.setBackground(Color.decode("#1167B1")); // Default blue for view/edit

            }

            // Add hover effects for better UX
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if("Delete".equals(text)) {
                        button.setBackground(Color.decode("#C9302C")); // Darker red for delete
                    } else {
                        button.setBackground(Color.decode("#0d5a9f"));
                    }
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if ("Delete".equals(text)) {
                        button.setBackground(Color.decode("#DC3545")); // Back to normal red
                    } else {
                        button.setBackground(Color.decode("#1167B1")); // Back to normal blue
                    }
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
        private final JButton editBtn;
        private final JButton deleteBtn;

        public ActionsRenderer() {
            setLayout(new GridLayout(1, 3, 3, 0));
            setOpaque(true);

            viewBtn = createStyledButton("View");
            editBtn = createStyledButton("Edit");
            deleteBtn = createStyledButton("Delete");

            add(viewBtn);
            add(editBtn);
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
            button.setPreferredSize(new Dimension(60, 20));
            button.setMargin(new Insets(0, 4, 0, 4));
            if ("Delete".equals(text)) {
                button.setBackground(Color.decode("#DC3545")); // Red color for delete
            } 
            return button;  
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            
            if (isSelected) {
                viewBtn.setBackground(Color.decode("#0d5a9f"));
                editBtn.setBackground(Color.decode("#0d5a9f"));
                deleteBtn.setBackground(Color.decode("#C82333"));
            } else {
                viewBtn.setBackground(Color.decode("#1167B1"));
                editBtn.setBackground(Color.decode("#1167B1"));
                deleteBtn.setBackground(Color.decode("#DC3545"));
            }
            
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Test database connection before starting
                PatientDAO testDAO = new PatientDAO();
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