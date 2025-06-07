import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

    public class appointmentManagement extends JFrame {
        final private Font mainFont = new Font("Segoe Ui", Font.PLAIN, 16);
        final private Font titleFont = new Font("Segoe Ui", Font.BOLD, 36);
        final private Font btnFont = new Font("Segoe Ui", Font.PLAIN, 14);

        private java.util.Map<String, JTextField> fieldInputs = new java.util.HashMap<>();
    
    public void appointment() {
        getContentPane().setBackground(Color.decode("#E1E3E5"));

        // Left Logo
        JLabel logoImage = new JLabel();
        ImageIcon icon = new ImageIcon("static/images/smalllogonotext.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(170, 50, Image.SCALE_SMOOTH));
        logoImage.setIcon(icon);
        // Right Logo
        JLabel logoAdmin = new JLabel("Admin");
        logoAdmin.setFont(new Font("Segoe Ui", Font.BOLD, 28));
        logoAdmin.setForeground(Color.decode("#192F8F"));
        // Logo Panel
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        logoPanel.add(logoImage, BorderLayout.WEST);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 10, 10));
        logoPanel.add(logoAdmin, BorderLayout.EAST);

        // Main Header
        JLabel mainHeader = new JLabel("Appointment Management");
        mainHeader.setFont(titleFont);
        mainHeader.setForeground(Color.WHITE);
        // Sub Header
        JLabel subHeader = new JLabel("Schedule, View, Edit, and Cancel Appointments");
        subHeader.setFont(mainFont);
        subHeader.setForeground(Color.WHITE);

        // Patient Record 
        JLabel recordTitle = new JLabel("View Patient Record");
        recordTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        recordTitle.setForeground(Color.decode("#192F8F"));
        recordTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] fieldLabels = {"First Name:", "Middle Name:", "Last Name:", "Birthday:", "Email:", "Phone Number:", "Address:"};
        JPanel fieldsContainer = new JPanel();
        fieldsContainer.setLayout(new BoxLayout(fieldsContainer, BoxLayout.Y_AXIS));
        fieldsContainer.setOpaque(false);

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

            fieldInputs.put(label, textField); // Store for later access

            fieldPanel.add(fieldLabel);
            fieldPanel.add(textField);
            fieldsContainer.add(fieldPanel);
            fieldsContainer.add(Box.createVerticalStrut(8));
        }
        // Record Title + Fields Labels Panel
        JPanel recordPanel = new JPanel();
        recordPanel.setLayout(new BoxLayout(recordPanel, BoxLayout.Y_AXIS));
        recordPanel.setBackground(Color.decode("#D0EFFF"));
        recordPanel.setPreferredSize(new Dimension(260, 0 ));
        recordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, Color.decode("#C0C0C0")),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        recordPanel.add(recordTitle);
        recordPanel.add(Box.createVerticalStrut(15));
        recordPanel.add(fieldsContainer);

        showPatientRecord(recordPanel, recordTitle, fieldsContainer);

        //Calendar Panel;
        final CalendarPanel calendar = new CalendarPanel();
        final boolean[] showingCalendar = {false};
        // Calendar Button
        JButton calendarBtn = makeButton("View Calendar");
        calendarBtn.addActionListener(e -> {
            if (!showingCalendar[0]) {
                recordPanel.removeAll();
                recordPanel.add(calendar);
                recordPanel.revalidate();
                recordPanel.repaint();
                calendarBtn.setText("Exit Calendar");
                showingCalendar[0] = true;
            } else {
                showPatientRecord(recordPanel, recordTitle, fieldsContainer);
                calendarBtn.setText("View Calendar");
                showingCalendar[0] = false;
            }
        });
        // Home Button
        JButton homeBtn = makeButton("Back to Home");
        homeBtn.addActionListener(e -> {
            homepage homepageInfo = new homepage();
            homepageInfo.home();
            dispose();
        });
        // Calendar + Home Button Panel
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        btnPanel.add(calendarBtn);
        btnPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        btnPanel.add(homeBtn);

        // Headers + Main Buttons Panel
        JPanel headers_homebtn_Panel = new JPanel(new BorderLayout());
        headers_homebtn_Panel.setOpaque(false);
        headers_homebtn_Panel.add(mainHeader, BorderLayout.WEST);
        headers_homebtn_Panel.add(btnPanel, BorderLayout.EAST);
        headers_homebtn_Panel.add(subHeader, BorderLayout.SOUTH);
        headers_homebtn_Panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // Tab Pane
        String[] tabNames = {"UPCOMING", "CANCELED", "COMPLETED"};
        String[] columnNames = {"First Name", "Middle Name", "Last Name", "Purpose of Visit", "Date and Time", "Scheduled By", "Actions"};
        String[] upcomingColumnNames = {"First Name", "Middle Name", "Last Name", "Purpose of Visit", "Date and Time", "Scheduled By", "Status", "Actions"};
        // Models for both tabs so we can update them
        DefaultTableModel[] models = new DefaultTableModel[tabNames.length];
        JTable[] tables = new JTable[tabNames.length];

        // Store completed data 
        Object[][] completedData = {};

        JTabbedPane appointmentTabs = new JTabbedPane();
        for (int tabIdx = 0; tabIdx < tabNames.length; tabIdx++) {
            String tabName = tabNames[tabIdx];
            JPanel tabPanel = new JPanel(new BorderLayout());
            DefaultTableModel model;
            JTable table;

            if (tabName.equals("UPCOMING")) {
                Object[][] upcomingData = {
                    {"Juan", "B.", "Dela Cruz", "Deep Cleaning", "2025-06-10", "--", "Completed", null},
                    {"Jose", "M.", "Marichan", "Deep Cleaning", "2025-06-05", "--", "Confirmed", null}
                };
                // JCombo for Status + Buttons for Actions
                for (Object[] row : upcomingData) {
                    String[] s1 = {"Completed", "Canceled"};
                    JComboBox<String> statusCombo = new JComboBox<>(s1);
                    statusCombo.setSelectedItem(row[6] != null ? row[6] : "Completed");
                    statusCombo.setMaximumSize(new Dimension(120, 28));
                    statusCombo.setBackground(Color.decode("#E1E3E5"));

                    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                    actionPanel.setOpaque(false);
                    JButton viewBtn = makeButton("View");
                    JButton deleteBtn = makeButton("Delete");
                    actionPanel.add(viewBtn);
                    actionPanel.add(deleteBtn);

                    row[6] = statusCombo;
                    row[7] = actionPanel;
                }
                model = new DefaultTableModel(upcomingData, upcomingColumnNames) {
                    public boolean isCellEditable(int row, int column) { return column == 6 || column == 7; }
                };
                table = new JTable(model) {
                    public TableCellRenderer getCellRenderer(int row, int column) {
                        if (column == 6) return (table, value, isSelected, hasFocus, row1, col1) -> (JComboBox<?>) value;
                        if (column == 7) return (table, value, isSelected, hasFocus, row1, col1) -> (JPanel) value;
                        return super.getCellRenderer(row, column);
                    }
                };
                // Make the Status column editable with a JComboBox
                String[] status = {"Completed", "Canceled"};
                table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JComboBox<>(status)));

                // Listen for editing stopped (status changed)
                table.getDefaultEditor(Object.class).addCellEditorListener(new javax.swing.event.CellEditorListener() {
                    public void editingStopped(javax.swing.event.ChangeEvent e) {
                        int editingRow = table.getEditingRow();
                        int editingCol = table.getEditingColumn();
                        if (editingCol == 6 && editingRow != -1) {
                            Object editorValue = table.getCellEditor().getCellEditorValue();
                            if ("Completed".equals(editorValue)) {
                                // Prepare row for completed tab (remove JComboBox, add actions panel)
                                Object[] movedRow = new Object[7];
                                for (int i = 0; i < 6; i++) {
                                    movedRow[i] = table.getValueAt(editingRow, i);
                                }
                                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                                JButton viewBtn = makeButton("View");
                                JButton deleteBtn = makeButton("Delete");
                                actionPanel.add(viewBtn);
                                actionPanel.add(deleteBtn);
                                movedRow[6] = actionPanel;
                                // Add to completed tab
                                models[2].addRow(movedRow); // index 2 = COMPLETED
                                // Remove from upcoming tab
                                model.removeRow(editingRow);
                            }
                        }
                    }
                    public void editingCanceled(javax.swing.event.ChangeEvent e) {}
                });

            } else if (tabName.equals("COMPLETED")) {
                // Sample data for completed tab (can be empty)
                Object[][] data = completedData;
                for (Object[] row : data) {
                    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                    actionPanel.setOpaque(false);
                    JButton viewBtn = makeButton("View");
                    JButton deleteBtn = makeButton("Delete");
                    actionPanel.add(viewBtn);
                    actionPanel.add(deleteBtn);
                    row[6] = actionPanel;
                }
                model = new DefaultTableModel(data, columnNames) {
                    public boolean isCellEditable(int row, int column) { return column == 6; }
                };
                table = new JTable(model) {
                    public TableCellRenderer getCellRenderer(int row, int column) {
                        if (column == 6) return (table, value, isSelected, hasFocus, row1, col1) -> (JPanel) value;
                        return super.getCellRenderer(row, column);
                    }
                };
            } else {
                Object[][] data = {
                    //canceled
                };
                for (Object[] row : data) {
                    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                    actionPanel.setOpaque(false);
                    JButton viewBtn = makeButton("View");
                    JButton deleteBtn = makeButton("Delete");
                    actionPanel.add(viewBtn);
                    actionPanel.add(deleteBtn);
                    row[6] = actionPanel;
                }
                model = new DefaultTableModel(data, columnNames) {
                    public boolean isCellEditable(int row, int column) { return column == 6; }
                };
                table = new JTable(model) {
                    public TableCellRenderer getCellRenderer(int row, int column) {
                        if (column == 6) return (table, value, isSelected, hasFocus, row1, col1) -> (JPanel) value;
                        return super.getCellRenderer(row, column);
                    }
                };
            }
            table.setRowHeight(32);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.getTableHeader().setBackground(Color.decode("#1167B1"));
            table.getTableHeader().setForeground(Color.WHITE);

            JScrollPane tableScroll = new JScrollPane(table);
            tableScroll.setBorder(BorderFactory.createEmptyBorder());
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setBackground(Color.decode("#E1E3E5"));
            tablePanel.add(tableScroll, BorderLayout.CENTER);

            tabPanel.add(tablePanel, BorderLayout.CENTER);
            tabPanel.setOpaque(false);

            appointmentTabs.add(tabName, tabPanel);

            // Store models and tables for later use
            models[tabIdx] = model;
            tables[tabIdx] = table;
        }
        // All Tabs Panel
        JPanel tabsPanel = new JPanel(new BorderLayout());
        tabsPanel.setOpaque(false);
        tabsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        tabsPanel.add(appointmentTabs, BorderLayout.CENTER);

        // Search Bar
        JTextField searchBar = new JTextField();
        searchBar.setFont(mainFont);
        searchBar.setBackground(Color.WHITE);
        searchBar.setForeground(Color.decode("#1167B1"));
        searchBar.setMaximumSize(new Dimension(250, 30));

        // Search Button
        JButton searchBtn = makeButton("Search");

        // Refresh Button
        JButton refreshBtn = makeButton("Refresh");

        // Search Panel (Search Bar + Search Button + Refresh Button)
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setOpaque(false);
        searchPanel.add(searchBar);
        searchPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        searchPanel.add(searchBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        searchPanel.add(refreshBtn);

        // Blue Panel (Headers + Search Panel)
        JPanel bluePanel = new JPanel(new BorderLayout());
        bluePanel.setBackground(Color.decode("#2A9DF4"));
        bluePanel.add(headers_homebtn_Panel, BorderLayout.NORTH);
        bluePanel.add(searchPanel, BorderLayout.CENTER);
        bluePanel.setMaximumSize(new Dimension(6000, 150));
        bluePanel.setPreferredSize(new Dimension(6000, 150));
        bluePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Fixed Layout for logo & blue panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.add(logoPanel);
        headerPanel.add(bluePanel);

        // Container for View Record and Calendar (EAST)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(500, 300)); // Fixed width for right panel
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        rightPanel.add(recordPanel);
        rightPanel.setOpaque(false);

        // Fixed Layout for Tab Panel and Record Panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE); 
        contentPanel.setOpaque(true);
        contentPanel.add(tabsPanel, BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);

        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setTitle("Appointment Management");
        setSize(1200, 700);
        setResizable(true);
        setLocationRelativeTo(null); // Center on screen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Add this line for full screen
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private void showPatientRecord(JPanel recordPanel, JLabel recordTitle, JPanel fieldsContainer) {
        recordPanel.removeAll();
        recordPanel.add(recordTitle);
        recordPanel.add(Box.createVerticalStrut(15));
        recordPanel.add(fieldsContainer);
        recordPanel.revalidate();
        recordPanel.repaint();
    }
    private JButton makeButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(btnFont);
        btn.setBackground(Color.decode("#1167B1"));
        btn.setForeground(Color.WHITE);
        btn.setMaximumSize(new Dimension(110, 30));
        btn.setMargin(new Insets(2, 2, 2, 2));
        btn.setFocusPainted(false);
        return btn;
    }
    
    public static void main(String[] args) {
        appointmentManagement myFrame = new appointmentManagement();
        myFrame.appointment();
    }
}