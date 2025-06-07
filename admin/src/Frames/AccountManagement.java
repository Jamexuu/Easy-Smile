package Frames;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import java.util.List;
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
    private List<Integer> accountIds;

    public AccountManagement() {
        // Initialize DAO
        accountDAO = new AccountManagementDAO();
        accountIds = new ArrayList<>();
    }

    public void initialize() {
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
                            "Email", "Phone Num", "Actions"};
        Object[][] data = {};

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions column is editable
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
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(260, 0));
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
        
        // Set up actions column after sidebar fields are created
        table.getColumn("Actions").setCellRenderer(new ActionsRenderer());
        table.getColumn("Actions").setCellEditor(new ActionsEditor(table, sidebarFields));
        
        return sidebarPanel;
    }

    private void createSidebarFields(JPanel sidebarPanel) {
        String[] fieldLabels = {"User ID", "First Name:", "Middle Name:", "Last Name:", "Birthday:", 
                            "Email:", "Phone Number:"};
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

            if (row < 0 || row >= table.getRowCount()) {
                JOptionPane.showMessageDialog(this,
                    "Invalid row selected for viewing account.",
                    "View Error",
                    JOptionPane.ERROR_MESSAGE);
            }

            for (int i = 0; i < sidebarFields.size() && i < table.getColumnCount() - 1; i++) {
            Object val = table.getValueAt(row, i);
            sidebarFields.get(i).setText(val != null ? val.toString() : "");

            System.out.println("Viewing account at row:" + row);

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
        try{
            if(row < 0 || row >= table.getRowCount()) {
                JOptionPane.showMessageDialog(this,
                    "Invalid row selected for deletion.",
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String firstName = table.getValueAt(row, 1).toString();
            String lastName = table.getValueAt(row, 3).toString();
            String email = table.getValueAt(row, 5).toString();

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this record?\n\n"+
                "Name: " + firstName + " " + lastName + "\n" +
                "Email: " + email + "\n\n" +
                "This action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // Get the account ID for this row
                int accountId = accountIds.get(row);

                // Delete from database
                boolean success = accountDAO.deleteAccount(accountId);
                
                setCursor(Cursor.getDefaultCursor());
                    
                if (success) {
                    // Remove from table and ID list
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
        }catch (Exception ex){
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
        }
    }

    // Inner Classes
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

            JButton viewBtn = createStyledButton("View");
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
            button.setPreferredSize(new Dimension(60, 25));
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