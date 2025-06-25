package Frames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import DAO.AccountManagementDAO;
import DAO.AccountManagementDAO.Account;

public class AccountManagement extends JFrame {
    // Constants
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font BTN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color BACKGROUND_COLOR = Color.decode("#E1E3E5");
    private static final Color BLUE_COLOR = Color.decode("#1167B1");
    private static final Color LIGHT_BLUE_COLOR = Color.decode("#2A9DF4");
    private static final Color SIDEBAR_COLOR = Color.decode("#D0EFFF");
    private static final Color DARK_BLUE_COLOR = Color.decode("#192F8F");

    // Components for DAO interaction
    private JTextField searchBar;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private List<JTextField> sidebarFields;
    
    // DAO instance
    private AccountManagementDAO accountDAO;
    
    // Store account IDs for each row (for deletion)
    private List<String> accountIds;

    private boolean isInitialized = false;

    private boolean isEditMode = false;
    private Map<String, JTextField> fieldInputs;
    // Add these instance variables
    private JButton saveButton;
    private JButton cancelButton;

    public AccountManagement() {
        // Initialize DAO
        accountDAO = new AccountManagementDAO();
        accountIds = new ArrayList<>();
        sidebarFields = new ArrayList<>();
    }

    public void initialize() {
        if (isInitialized) {
            return;
        }

        isInitialized = true;

        getContentPane().removeAll();

        getContentPane().setBackground(Color.decode("#E1E3E5"));

        setupFrame();
        createComponents();
        loadData(); // Load data from database
    }

    private void setupFrame() {
        setTitle("Account Management");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void createComponents() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createSidebarPanel(), BorderLayout.EAST);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(createTopHeaderRow(), BorderLayout.NORTH);
        headerPanel.add(createBluePanel(), BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTopHeaderRow() {
        JPanel topHeaderRow = new JPanel(new BorderLayout());
        topHeaderRow.setOpaque(false);
        topHeaderRow.add(createLogoPanel(), BorderLayout.WEST);
        topHeaderRow.add(createAdminLabelPanel(), BorderLayout.EAST);
        return topHeaderRow;
    }

    private JPanel createLogoPanel() {
        JLabel logoImage = new JLabel();
        java.net.URL logoUrl = getClass().getResource("/images/smalllogonotext.png");
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            icon = new ImageIcon(icon.getImage().getScaledInstance(170, 50, Image.SCALE_SMOOTH));
            logoImage.setIcon(icon);
        } else {
            logoImage.setText("Logo");
        }

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.add(logoImage, BorderLayout.WEST);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 10, 10));
        return logoPanel;
    }

    private JPanel createAdminLabelPanel() {
        JLabel logoAdmin = new JLabel("Admin");
        logoAdmin.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoAdmin.setForeground(DARK_BLUE_COLOR);

        JPanel adminLabelPanel = new JPanel(new BorderLayout());
        adminLabelPanel.setOpaque(false);
        adminLabelPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
        adminLabelPanel.add(logoAdmin, BorderLayout.CENTER);
        return adminLabelPanel;
    }

    private JPanel createBluePanel() {
        JPanel bluePanel = new JPanel(new BorderLayout());
        bluePanel.setBackground(LIGHT_BLUE_COLOR);
        bluePanel.add(createBlueContentBox(), BorderLayout.WEST);
        bluePanel.add(createAdminPanel(), BorderLayout.EAST);
        bluePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        return bluePanel;
    }

    private Box createBlueContentBox() {
        Box blueContentBox = Box.createVerticalBox();
        blueContentBox.setOpaque(false);

        JPanel headersPanel = createHeadersPanel();
        headersPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        blueContentBox.add(headersPanel);
        blueContentBox.add(Box.createVerticalStrut(18));

        JPanel searchPanel = createSearchPanel();
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        blueContentBox.add(searchPanel);

        return blueContentBox;
    }

    private JPanel createHeadersPanel() {
        JLabel mainHeader = new JLabel("Account Management");
        mainHeader.setFont(TITLE_FONT);
        mainHeader.setForeground(Color.WHITE);

        JLabel subHeader = new JLabel("View, Edit, and Delete Account Records");
        subHeader.setFont(MAIN_FONT);
        subHeader.setForeground(Color.WHITE);

        JPanel headersPanel = new JPanel();
        headersPanel.setLayout(new BoxLayout(headersPanel, BoxLayout.Y_AXIS));
        headersPanel.setOpaque(false);
        headersPanel.add(mainHeader);
        headersPanel.add(subHeader);
        return headersPanel;
    }

    private JPanel createSearchPanel() {
        searchBar = new JTextField();
        searchBar.setFont(MAIN_FONT);
        searchBar.setBackground(Color.WHITE);
        searchBar.setForeground(BLUE_COLOR);
        searchBar.setMaximumSize(new Dimension(300, 30));

        JButton searchBtn = createStyledButton("Search", 100);
        JButton refreshBtn = createStyledButton("Refresh", 100);

        searchBtn.addActionListener(this::performSearch);
        refreshBtn.addActionListener(this::refreshData);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        searchPanel.add(searchBar);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(searchBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(refreshBtn);
        return searchPanel;
    }

    private JPanel createAdminPanel() {
        JButton homeBtn = createStyledButton("Back to Home", 140);
        homeBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        homeBtn.addActionListener(this::goBackToHome);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.setOpaque(false);
        btnPanel.add(Box.createHorizontalGlue());
        btnPanel.add(homeBtn);

        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.Y_AXIS));
        adminPanel.setOpaque(false);
        adminPanel.add(Box.createVerticalStrut(32));
        adminPanel.add(btnPanel);
        return adminPanel;
    }

    private JButton createStyledButton(String text, int width) {
        JButton button = new JButton(text);
        button.setFont(BTN_FONT);
        button.setBackground(BLUE_COLOR);
        button.setForeground(Color.WHITE);
        button.setMaximumSize(new Dimension(width, 30));
        button.setMargin(new Insets(2, 2, 2, 2));
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createTablePanel() {
        createTable();
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 5));
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        return tablePanel;
    }

    private void createTable() {
        String[] columnNames = {"User ID", "First Name", "Middle Name", "Last Name", "Birthday", 
                            "Email", "Phone Num", "Barangay", "City", "Province", "Actions"};
        Object[][] data = {};

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 10; // Only Actions column is editable
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(BLUE_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);

        tableSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(tableSorter);

        // Set column widths for better display
        table.getColumn("User ID").setPreferredWidth(70);
        table.getColumn("User ID").setMinWidth(70);
        table.getColumn("User ID").setMaxWidth(100);
        table.getColumn("Actions").setPreferredWidth(140);
        table.getColumn("Actions").setMinWidth(140);
        table.getColumn("Actions").setMaxWidth(140);

        table.getColumn("Actions").setCellRenderer(new ActionsRenderer());
        table.getColumn("Actions").setCellEditor(new ActionsEditor(table, sidebarFields));
    }

    private JPanel createFieldPanel(String labelText) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(DARK_BLUE_COLOR);

        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        textField.setBackground(Color.WHITE);
        textField.setForeground(BLUE_COLOR);
        textField.setBorder(BorderFactory.createLineBorder(DARK_BLUE_COLOR, 1));

        // Add the label and text field to the panel
        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(5)); // Add spacing between label and text field
        fieldPanel.add(textField);

        // Add the text field to the sidebarFields list for later access
        sidebarFields.add(textField);

        return fieldPanel;
    }

    private void createSidebarFields(JPanel sidebarPanel) {
        String[] fieldLabels = {"Account ID:", "First Name:", "Middle Name:", "Last Name:", "Birthday:", 
                                "Email:", "Phone Number:", "Barangay:", "City:", "Province:"};
        fieldInputs = new HashMap<>();
        
        // Ensure sidebarFields is initialized
        if (sidebarFields == null) {
            sidebarFields = new ArrayList<>();
        } else {
            sidebarFields.clear(); // Clear existing entries if reusing
        }

        for (String label : fieldLabels) {
            JPanel fieldPanel = new JPanel();
            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
            fieldPanel.setOpaque(false);

            JLabel fieldLabel = new JLabel(label);
            fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fieldLabel.setForeground(Color.decode("#192F8F"));

            JTextField textField = new JTextField();
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            textField.setBackground(Color.WHITE);
            textField.setForeground(Color.decode("#1167B1"));
            textField.setBorder(BorderFactory.createLineBorder(Color.decode("#192F8F"), 1));

            // Add field to both collections
            fieldInputs.put(label, textField);
            sidebarFields.add(textField); // Add to sidebarFields list

            // Make the "Account ID" field non-editable
            if (label.equals("Account ID:")) {
                textField.setEditable(false);
                textField.setBackground(Color.LIGHT_GRAY);
            }

            // Add the label and text field to the panel
            fieldPanel.add(fieldLabel);
            fieldPanel.add(Box.createVerticalStrut(5)); // Add spacing between label and text field
            fieldPanel.add(textField);
            sidebarPanel.add(fieldPanel);
            sidebarPanel.add(Box.createVerticalStrut(8)); // Add spacing between fields
        }

        // Add Save and Cancel buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);

        saveButton = createStyledButton("Save", 100);
        cancelButton = createStyledButton("Cancel", 100);

        saveButton.addActionListener(this::saveAccountChanges);
        cancelButton.addActionListener(this::cancelEdit);

        saveButton.setEnabled(false); // Disabled by default
        cancelButton.setEnabled(false); // Disabled by default

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        sidebarPanel.add(Box.createVerticalStrut(15));
        sidebarPanel.add(buttonPanel);
    }

    private void saveAccountChanges(ActionEvent e) {
        try {
            // Get the account ID from the first field
            String accountId = fieldInputs.get("Account ID:").getText();

            if (accountId == null || accountId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Invalid account ID!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create account object from fields
            Account account = new Account();
            account.setId(accountId);
            account.setFirstName(fieldInputs.get("First Name:").getText());
            account.setMiddleName(fieldInputs.get("Middle Name:").getText());
            account.setLastName(fieldInputs.get("Last Name:").getText());
            account.setBirthday(fieldInputs.get("Birthday:").getText());
            account.setEmail(fieldInputs.get("Email:").getText());
            account.setPhoneNumber(fieldInputs.get("Phone Number:").getText());
            account.setBarangay(fieldInputs.get("Barangay:").getText());
            account.setCity(fieldInputs.get("City:").getText());
            account.setProvince(fieldInputs.get("Province:").getText());

            // Validate account data
            if (account.getFirstName().isEmpty() || account.getLastName().isEmpty() ||
                account.getEmail().isEmpty() || account.getPhoneNumber().isEmpty()) {

                JOptionPane.showMessageDialog(this,
                    "First Name, Last Name, Email, and Phone Number are required fields!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update account in database
            boolean success = accountDAO.updateAccount(account);

            if (success) {
                // Exit edit mode
                isEditMode = false;
                saveButton.setEnabled(false);
                cancelButton.setEnabled(false);

                // Refresh the table to show updated data
                loadData();

                JOptionPane.showMessageDialog(this,
                    "Account updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update account!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error saving account changes: " + ex.getMessage(),
                "Save Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void cancelEdit(ActionEvent e) {
        if (!isEditMode) {
            return; // Do nothing if not in edit mode
        }

        // Clear fields and reset to view mode
        clearSidebarFields();
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);
        isEditMode = false;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(20, 2, 0, 0, BACKGROUND_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel sidebarTitle = new JLabel("View Account Record");
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sidebarTitle.setForeground(DARK_BLUE_COLOR);
        sidebarTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(sidebarTitle);
        sidebarPanel.add(Box.createVerticalStrut(15));

        createSidebarFields(sidebarPanel);

        // Wrap the sidebarPanel in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(sidebarPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // Disable horizontal scrolling
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // Enable vertical scrolling
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove default border

        // Create a container panel to hold the scroll pane
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(SIDEBAR_COLOR);
        containerPanel.setPreferredSize(new Dimension(260, 0));
        containerPanel.add(scrollPane, BorderLayout.CENTER);

        return containerPanel;
    }

    // Data Management Methods - DAO Implementation
    private void loadData() {
        try {
            SwingUtilities.invokeLater(() -> {
                // Show loading cursor
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            });

            List<Account> accounts = accountDAO.getAllAccounts();
            populateTable(accounts);
            
            SwingUtilities.invokeLater(() -> {
                // Reset cursor
                setCursor(Cursor.getDefaultCursor());
                System.out.println("Loaded " + accounts.size() + " accounts from database");
            });
            
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> {
                setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, 
                    "Error loading data from database: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            });
            e.printStackTrace();
        }
    }

    private void populateTable(List<Account> accounts) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0); // Clear existing data
            accountIds.clear(); // Clear account IDs
            
            for (Account account : accounts) {
                tableModel.addRow(new Object[]{
                    account.getId(),
                    account.getFirstName(),
                    account.getMiddleName() != null ? account.getMiddleName() : "",
                    account.getLastName(),
                    account.getBirthday(),
                    account.getEmail(),
                    account.getPhoneNumber(),
                    account.getBarangay() != null ? account.getBarangay() : "",
                    account.getCity() != null ? account.getCity() : "",
                    account.getProvince() != null ? account.getProvince() : "",
                    null // Actions column
                });
                accountIds.add(account.getId()); // Store account ID for this row
            }
        });
    }

    // Event Handlers
    private void performSearch(ActionEvent e) {
        String searchText = searchBar.getText().trim();
        
        if (searchText.isEmpty()) {
            loadData(); // Load all data if search is empty
            return;
        }

        try {
            SwingUtilities.invokeLater(() -> {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            });

            // Perform database search
            List<Account> searchResults = accountDAO.searchAccounts(searchText);
            populateTable(searchResults);
            
            SwingUtilities.invokeLater(() -> {
                setCursor(Cursor.getDefaultCursor());
                System.out.println("Search found " + searchResults.size() + " accounts for: " + searchText);
            });
            
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, 
                    "Error searching database: " + ex.getMessage(), 
                    "Search Error", 
                    JOptionPane.ERROR_MESSAGE);
            });
            ex.printStackTrace();
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
            new homepage().home(); // Navigate back to homepage
        }
    }

    private void viewAccount(int row) {
        try{

            if (sidebarFields == null) {
                JOptionPane.showMessageDialog(this,
                    "Sidebar fields not initialized.",
                    "View Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (row < 0 || row >= table.getRowCount()) {
                JOptionPane.showMessageDialog(this,
                    "Invalid row selected for viewing account.",
                    "View Error",
                    JOptionPane.ERROR_MESSAGE);
            }

            for (int i = 0; i < sidebarFields.size() && i < table.getColumnCount() - 1; i++) {
                Object val = table.getValueAt(row, i);
                sidebarFields.get(i).setText(val != null ? val.toString() : "");
                sidebarFields.get(i).setEditable(false); // Make fields read-only

            System.out.println("Viewing account at row:" + row);

            // Disable Save and Cancel buttons
            saveButton.setEnabled(false);
            cancelButton.setEnabled(false);

            isEditMode = false;

        }
        }catch (Exception e){
            JOptionPane.showMessageDialog(this,
                "Error viewing account: " + e.getMessage(),
                "View Error",
                JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
        }
    }

    private void deleteAccount(int row) {
        try {
            if (row < 0 || row >= table.getRowCount()) {
                JOptionPane.showMessageDialog(this,
                    "Invalid row selected for deletion.",
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get account info for confirmation dialog
            String accountId = accountIds.get(row);
            String firstName = table.getValueAt(row, 1).toString();
            String lastName = table.getValueAt(row, 3).toString();
            String email = table.getValueAt(row, 5).toString();

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this record?\n\n" +
                "User ID: " + accountId + "\n" +
                "Name: " + firstName + " " + lastName + "\n" +
                "Email: " + email + "\n\n" +
                "This action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = accountDAO.deleteAccount(accountId);

                if (success) {
                    tableModel.removeRow(row);
                    accountIds.remove(row);
                    clearSidebarFields();
                    JOptionPane.showMessageDialog(this,
                        "Account deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete account from database!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this,
                "Error deleting account: " + ex.getMessage(),
                "Delete Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void clearSidebarFields() {
        for (JTextField field : sidebarFields) {
            field.setText("");
            field.setEditable(false);
        }
    }

    // Inner Classes
    static class ActionsRenderer extends JPanel implements TableCellRenderer {
        private final JButton viewBtn;
        private final JButton editBtn;
        private final JButton deleteBtn;

        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));
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
            button.setPreferredSize(new Dimension(40, 25));
            button.setMargin(new Insets(2, 4, 2, 4));
            return button;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            
            // Update button states based on selection
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

    private void editAccount(int row) {
        try {
            if (row < 0 || row >= table.getRowCount()) {
                JOptionPane.showMessageDialog(this,
                    "Invalid row selected for editing.",
                    "Edit Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Populate fields with account data
            for (int i = 0; i < sidebarFields.size() && i < table.getColumnCount() - 1; i++) {
                Object val = table.getValueAt(row, i);
                sidebarFields.get(i).setText(val != null ? val.toString() : "");
                sidebarFields.get(i).setEditable(true); // Make fields editable
            }

            // Enable Save and Cancel buttons
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);

            isEditMode = true; // Set mode to edit
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error editing account: " + e.getMessage(),
                "Edit Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
            panel.setOpaque(true);

            JButton viewBtn = createStyledButton("View");
            JButton editBtn = createStyledButton("Edit");
            JButton deleteBtn = createStyledButton("Delete");

            viewBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    AccountManagement parent = (AccountManagement) SwingUtilities.getWindowAncestor(table);
                    if (parent != null) {
                        parent.viewAccount(currentRow);
                    }
                    fireEditingStopped();
                });
            });

            editBtn.addActionListener(e -> { // Define Edit button behavior
                SwingUtilities.invokeLater(() -> {
                    AccountManagement parent = (AccountManagement) SwingUtilities.getWindowAncestor(table);
                    if (parent != null) {
                        parent.editAccount(currentRow); // Call editAccount method
                    }
                    fireEditingStopped();
                });
            });
            deleteBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    AccountManagement parent = (AccountManagement) SwingUtilities.getWindowAncestor(table);
                    if (parent != null) {
                        parent.deleteAccount(currentRow);
                    }
                    fireEditingStopped();
                });
            });

            panel.add(viewBtn);
            panel.add(editBtn);
            panel.add(deleteBtn);
            panel.setBackground(isSelected ? tableParam.getSelectionBackground() : tableParam.getBackground());
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
            button.setPreferredSize(new Dimension(40, 25));
            button.setMargin(new Insets(2, 4, 2, 4));
            
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
            return false; // Prevent cell selection when clicking buttons
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Test database connection before starting
                AccountManagementDAO testDAO = new AccountManagementDAO();
                if (testDAO.testDatabaseOperations()) {
                    AccountManagement frame = new AccountManagement();
                    frame.initialize();
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "Database connection failed!\nPlease check your database configuration.", 
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