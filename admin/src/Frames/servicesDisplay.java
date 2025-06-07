package Frames;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class servicesDisplay extends JFrame{
    Font btnFont = new Font("Segoe Ui", Font.PLAIN, 14);
    Font mainFont = new Font("Segoe Ui", Font.BOLD, 20);

    private java.util.Map<String, JTextField> fieldInputs = new java.util.HashMap<>();
    private String selectedMenu = "Services Display"; // Track selected menu

    // Helper to create menu label with or without underline
    private JLabel makeMenuLabel(String text, boolean underline) {
        String html = underline
            ? String.format("<html><a style='color:#1167B1;text-decoration:underline;'>%s</a></html>", text)
            : String.format("<html><a style='color:#1167B1;text-decoration:none;'>%s</a></html>", text);
        JLabel lbl = new JLabel(html);
        lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lbl.setFont(mainFont);
        return lbl;
    }
    public void services() {
        getContentPane().setBackground(Color.WHITE);

        // Sidebar Nav (labels as fields so we can update them)
        JLabel homeLabel = makeMenuLabel("Home", selectedMenu.equals("Home"));
        JLabel servicesLabel = makeMenuLabel("Services Display", selectedMenu.equals("Services Display"));
        JLabel dentistLabel = makeMenuLabel("Dentists Information", selectedMenu.equals("Dentists Information"));
        JLabel accountLabel = makeMenuLabel("Account Information", selectedMenu.equals("Account Information"));
        JLabel clinicLabel = makeMenuLabel("Clinic Information", selectedMenu.equals("Clinic Information"));

        // Mouse listener to update underline on click
        MouseAdapter menuClickHandler = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel clicked = (JLabel) e.getSource();
                // Remove HTML tags to get plain text
                String menuText = clicked.getText().replaceAll("<[^>]*>", "").trim();
                selectedMenu = menuText;

                // Update all menu labels
                homeLabel.setText(makeMenuLabel("Home", selectedMenu.equals("Home")).getText());
                servicesLabel.setText(makeMenuLabel("Services Display", selectedMenu.equals("Services Display")).getText());
                dentistLabel.setText(makeMenuLabel("Dentists Information", selectedMenu.equals("Dentists Information")).getText());
                accountLabel.setText(makeMenuLabel("Account Information", selectedMenu.equals("Account Information")).getText());
                clinicLabel.setText(makeMenuLabel("Clinic Information", selectedMenu.equals("Clinic Information")).getText());
    
            // Navigation logic
            switch (menuText) {
                case "Home":
                    homepage homepageInfo = new homepage();
                    homepageInfo.home();
                    dispose();
                    break;
                case "Services Display":
                    servicesDisplay servicesInfo = new servicesDisplay();
                    servicesInfo.services();
                    dispose();
                    break;
                case "Dentists Information":
                    dentistsInformation dentistInfo = new dentistsInformation();
                    dentistInfo.initialize();
                    dispose();
                    break;
                case "Account Information":
                    // Account Management
                    break;
                case "Clinic Information":
                    clinicInformationFrame clinicInfo = new clinicInformationFrame();
                    clinicInfo.initialize();
                    dispose();
                    break;
                }
            }
        };
        homeLabel.addMouseListener(menuClickHandler);
        servicesLabel.addMouseListener(menuClickHandler);
        dentistLabel.addMouseListener(menuClickHandler);
        accountLabel.addMouseListener(menuClickHandler);
        clinicLabel.addMouseListener(menuClickHandler);

        // Sidebar Panel
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.decode("#E1E3E5"));
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

        /* Menu Button */
        JButton menuBtn = makeButton("Menu");
        menuBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sidebarPanel.setVisible(!sidebarPanel.isVisible());
                // Revalidate and repaint to update layout
                sidebarPanel.revalidate();
                sidebarPanel.repaint();
            }
        });
        // Blue Banner
        JPanel blueHeaderPanel = new JPanel(new BorderLayout());
        blueHeaderPanel.setBackground(Color.decode("#2A9DF4"));
        blueHeaderPanel.setPreferredSize(new Dimension(1200, 50));
        blueHeaderPanel.add(menuBtn, BorderLayout.WEST);
        blueHeaderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  

        // Logo 
        JLabel logoImage = new JLabel();
        ImageIcon icon = new ImageIcon("static/images/EasySmileLogo.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(250, 90, Image.SCALE_SMOOTH));
        logoImage.setIcon(icon);

        //Content for Field Container Panel
        JLabel recordTitle = new JLabel("View a Service");
        recordTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        recordTitle.setForeground(Color.decode("#192F8F"));
        recordTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] fieldLabels = {"Services:", "Description:", "Starting Price:"};
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
            textField.setMaximumSize(new Dimension(650, 26));
            textField.setBackground(Color.WHITE);
            textField.setBorder(BorderFactory.createLineBorder(Color.decode("#C0C0C0")));
            textField.setEditable(true); 

            fieldInputs.put(label, textField); // Store for later access

            fieldPanel.add(fieldLabel);
            fieldPanel.add(textField);
            fieldsContainer.add(fieldPanel);
            fieldsContainer.add(Box.createVerticalStrut(8));
        }

        // Table Panel (CENTER)
        String[] columnNames = {"Services", "Description", "Starting Price", "Actions"};
        Object[][] data = {
            {"Braces", "insert description", "insert price", null}
        }; 
        for (Object[] row : data) {
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            actionPanel.setOpaque(false);
            JButton viewBtn = makeButton("View");
            JButton editBtn = makeButton("Edit");
            JButton deleteBtn = makeButton("Delete");

            actionPanel.add(viewBtn);
            actionPanel.add(editBtn);
            actionPanel.add(deleteBtn);
            row[3] = actionPanel; 
        }


        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.decode("#1167B1"));
        table.getTableHeader().setForeground(Color.WHITE);

        // Custom renderer for the action buttons (so the panel is always shown)
        table.getColumn("Actions").setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> (JPanel) value);

        // Custom editor for the action buttons (so the buttons are clickable)
        table.getColumn("Actions").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JPanel actionPanel;
            private JButton editBtn;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                actionPanel = (JPanel) value;
                // Find the Edit button and re-attach the listener for this row
                for (Component comp : actionPanel.getComponents()) {
                    if (comp instanceof JButton && ((JButton) comp).getText().equals("Edit")) {
                        editBtn = (JButton) comp;
                        // Remove old listeners
                        for (ActionListener al : editBtn.getActionListeners()) {
                            editBtn.removeActionListener(al);
                        }
                        // Add new listener for this row
                        editBtn.addActionListener(e -> {
                            recordTitle.setText("Edit a Service");
                            fireEditingStopped();
                        });
                    }
                }
                return actionPanel;
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Record Panel 
        JPanel recordPanel = new JPanel();
        recordPanel.setLayout(new BoxLayout(recordPanel, BoxLayout.Y_AXIS));
        recordPanel.setBackground(Color.decode("#B8D4F0"));
        recordPanel.setPreferredSize(new Dimension(260, 0 ));
        recordPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, Color.decode("#C0C0C0")),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        recordPanel.add(recordTitle);
        recordPanel.add(Box.createVerticalStrut(15));
        recordPanel.add(fieldsContainer);

        // Container for Record Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(400, 300)); // Fixed width for right panel
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rightPanel.add(recordPanel);
        rightPanel.setOpaque(false);

        /* Tabs/Calendar Panel */
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(tablePanel, BorderLayout.CENTER); 
        contentPanel.add(rightPanel, BorderLayout.EAST);

        /* Main Content Panel */
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);
        mainContentPanel.add(logoImage);
        mainContentPanel.add(Box.createRigidArea(new Dimension(1, 20)));
        mainContentPanel.add (contentPanel);
        mainContentPanel.add(Box.createVerticalGlue());
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        logoImage.setAlignmentX(CENTER_ALIGNMENT);

        /* Main Panel */
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false); 
        mainPanel.add(blueHeaderPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);

        add(mainPanel);
        setTitle("Services Display");
        setSize(1200, 800);
        setResizable(true);
        setLocationRelativeTo(null); // Center on screen
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Add this line for full screen
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);     
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
        servicesDisplay myFrame = new servicesDisplay();
        myFrame.services();
    }
}


