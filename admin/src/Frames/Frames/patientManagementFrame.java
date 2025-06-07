package Frames;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EventObject;

public class patientManagementFrame extends JFrame {
    final private Font mainFont = new Font("Segoe Ui", Font.PLAIN, 16);
    final private Font titleFont = new Font("Segoe Ui", Font.BOLD, 36);
    final private Font btnFont = new Font("Segoe Ui", Font.PLAIN, 14);

    public patientManagementFrame() {
        initialize();
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

        JTextField searchBar = new JTextField();
        searchBar.setFont(mainFont);
        searchBar.setBackground(Color.WHITE);
        searchBar.setForeground(Color.decode("#1167B1"));
        searchBar.setMaximumSize(new Dimension(300, 30));

        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(btnFont);
        searchBtn.setBackground(Color.decode("#1167B1"));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setMaximumSize(new Dimension(100, 30));
        searchBtn.setMargin(new Insets(2, 2, 2, 2));
        searchBtn.setFocusPainted(false);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(btnFont);
        refreshBtn.setBackground(Color.decode("#1167B1"));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setMaximumSize(new Dimension(100, 30));
        refreshBtn.setMargin(new Insets(2, 2, 2, 2));
        refreshBtn.setFocusPainted(false);

        JButton homeBtn = new JButton("Back to Home");
        homeBtn.setFont(btnFont);
        homeBtn.setBackground(Color.decode("#1167B1"));
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setMaximumSize(new Dimension(140, 30));
        homeBtn.setMargin(new Insets(1, 4, 1, 4));
        homeBtn.setFocusPainted(false);

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

        // --- Table Panel (CENTER) ---
        String[] columnNames = {"First Name", "Middle Name", "Last Name", "Birthday", "Gender", "Actions"};
        Object[][] data = {
            {"John", "A.", "Doe", "1990-01-01", "Male", null},
            {"Jane", "B.", "Smith", "1985-05-12", "Female", null},
            {"Alice", "C.", "Johnson", "1992-07-23", "Female", null},
            {"Bob", "D.", "Williams", "1988-03-15", "Male", null},
            {"Maria", "E.", "Garcia", "1995-11-30", "Female", null}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column is editable
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 18));
        table.getTableHeader().setBackground(Color.decode("#1167B1"));
        table.getTableHeader().setForeground(Color.WHITE);

        // --- Sidebar Panel (EAST) ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.decode("#D0EFFF"));
        sidebarPanel.setPreferredSize(new Dimension(260, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(20, 2, 0, 0, Color.decode("#E1E3E5")),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
                // 1. Create the sorter and set it on the table
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // 2. Search button triggers filter
        searchBtn.addActionListener(e -> {
            String text = searchBar.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        // 3. Refresh button clears filter and search bar
        refreshBtn.addActionListener(e -> {
            searchBar.setText("");
            sorter.setRowFilter(null);
        });


        JLabel sidebarTitle = new JLabel("View Patient Record");
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sidebarTitle.setForeground(Color.decode("#192F8F"));
        sidebarTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(sidebarTitle);
        sidebarPanel.add(Box.createVerticalStrut(15));

        // Sidebar fields
        String[] fieldLabels = {"First Name:", "Middle Name:", "Last Name:", "Birthday:", "Gender:"};
        ArrayList<JTextField> sidebarFields = new ArrayList<>();
        for (String label : fieldLabels) {
            JPanel fieldPanel = new JPanel();
            fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
            fieldPanel.setOpaque(false);
            JLabel fieldLabel = new JLabel(label);
            fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fieldLabel.setForeground(Color.BLACK);
            JTextField textField = new JTextField();
            textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            textField.setMaximumSize(new Dimension(450, 30));
            textField.setBackground(Color.WHITE);
            textField.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));
            fieldPanel.add(fieldLabel);
            fieldPanel.add(textField);
            sidebarPanel.add(fieldPanel);
            sidebarPanel.add(Box.createVerticalStrut(8));
            sidebarFields.add(textField);
        }

        // --- Actions column as buttons ---
        table.getColumn("Actions").setCellRenderer(new TableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                JButton viewBtn = new JButton("View");
                JButton deleteBtn = new JButton("Delete");
                Color blue = Color.decode("#1167B1");
                viewBtn.setBackground(blue);
                viewBtn.setForeground(Color.WHITE);
                viewBtn.setOpaque(true);
                viewBtn.setBorderPainted(false);
                deleteBtn.setBackground(blue);
                deleteBtn.setForeground(Color.WHITE);
                deleteBtn.setOpaque(true);
                deleteBtn.setBorderPainted(false);
                viewBtn.setFocusable(false);
                deleteBtn.setFocusable(false);
                panel.add(viewBtn);
                panel.add(deleteBtn);
                panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                return panel;
            }
        });

        // Use a proper inner class for the editor (fixes your error)
        table.getColumn("Actions").setCellEditor(new ActionsEditor(table, sidebarFields));

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

    // Proper inner class for cell editor (fixes your error)
    static class ActionsEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        private JButton viewBtn = new JButton("View");
        private JButton deleteBtn = new JButton("Delete");
        private int editingRow = -1;
        private JTable table;
        private java.util.List<JTextField> sidebarFields;

        public ActionsEditor(JTable table, java.util.List<JTextField> sidebarFields) {
            this.table = table;
            this.sidebarFields = sidebarFields;

            Color blue = Color.decode("#1167B1");
            viewBtn.setBackground(blue);
            viewBtn.setForeground(Color.WHITE);
            viewBtn.setOpaque(true);
            viewBtn.setBorderPainted(false);

            deleteBtn.setBackground(blue);
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setOpaque(true);
            deleteBtn.setBorderPainted(false);

            viewBtn.setFocusable(false);
            deleteBtn.setFocusable(false);

            panel.add(viewBtn);
            panel.add(deleteBtn);

            viewBtn.addActionListener(e -> {
                for (int i = 0; i < sidebarFields.size(); i++) {
                    Object val = table.getValueAt(editingRow, i);
                    sidebarFields.get(i).setText(val != null ? val.toString() : "");
                }
                fireEditingStopped();
            });

            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                    table,
                    "Are you sure you want to delete this record?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION && editingRow >= 0) {
                    ((DefaultTableModel) table.getModel()).removeRow(editingRow);
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editingRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(patientManagementFrame::new);
    }
}
