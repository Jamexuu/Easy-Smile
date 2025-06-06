import javax.swing.*;
import java.awt.*;
/*import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; */

public class PatientManagement extends JFrame {
    final private Font mainFont = new Font("Segoe Ui", Font.PLAIN, 16);
    final private Font titleFont = new Font("Segoe Ui", Font.BOLD, 36);
    final private Font btnFont = new Font("Segoe Ui", Font.PLAIN, 14);

    public void initialize() { 
        getContentPane().setBackground(Color.decode("#E1E3E5"));

        /* Left Logo */
        JLabel logoImage = new JLabel();
        ImageIcon icon = new ImageIcon("Easy-Smile/AdminUI/src/images/smalllogonotext.png");
        icon = new ImageIcon(icon.getImage().getScaledInstance(170, 50, Image.SCALE_SMOOTH));
        logoImage.setIcon(icon);
        /* Right Logo */
        JLabel logoAdmin = new JLabel("Admin");
        logoAdmin.setFont(new Font("Segoe Ui", Font.BOLD, 28));
        logoAdmin.setForeground(Color.decode("#192F8F"));

        /* Logo Panel */
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false); 
        logoPanel.add(logoImage, BorderLayout.WEST);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 10, 10)); // optional padding
        logoPanel.add(logoAdmin, BorderLayout.EAST);

        /* Main Header */
        JLabel mainHeader = new JLabel("Patient Management");
        mainHeader.setFont(titleFont);
        mainHeader.setForeground(Color.WHITE);
        /* Sub Header */
        JLabel subHeader = new JLabel("Schedule, View, Edit, and Delete Patient Records");
        subHeader.setFont(mainFont);
        subHeader.setForeground(Color.WHITE);

        /* Home Button */
        JButton homeBtn = new JButton("Back to Home");
        homeBtn.setFont(btnFont);
        homeBtn.setBackground(Color.decode("#1167B1"));
        homeBtn.setForeground(Color.WHITE);
        homeBtn.setMaximumSize(new Dimension(100, 30));
        homeBtn.setMargin(new Insets(1, 4, 1, 4));
        homeBtn.setFocusPainted(false);
        /*homeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame newFrame = new JFrame("New Frame");
                newFrame.setSize(400, 300);
                newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                newFrame.setVisible(true);
            }
        });*/

        /* Home Button Panel */
        JPanel homeBtnPanel = new JPanel(new BorderLayout());
        homeBtnPanel.setLayout(new BoxLayout(homeBtnPanel, BoxLayout.Y_AXIS));
        homeBtnPanel.setOpaque(false); 
        homeBtnPanel.add(homeBtn, BorderLayout.EAST);
        homeBtnPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        homeBtnPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0)); // optional padding

        /* Headers and Home Button Panel */
        JPanel headers_homebtn_Panel = new JPanel(new BorderLayout());
        headers_homebtn_Panel.setOpaque(false);
        headers_homebtn_Panel.add(mainHeader, BorderLayout.WEST);
        headers_homebtn_Panel.add(homeBtnPanel, BorderLayout.EAST);
        headers_homebtn_Panel.add(subHeader, BorderLayout.SOUTH);
        headers_homebtn_Panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // optional padding
       

        /* SearchBar */
        JTextField searchBar = new JTextField();
        searchBar.setFont(mainFont);
        searchBar.setBackground(Color.WHITE);
        searchBar.setForeground(Color.decode("#1167B1"));
        searchBar.setMaximumSize(new Dimension(230, 30));

        /* Search Button  */
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(btnFont);
        searchBtn.setBackground(Color.decode("#1167B1"));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setMaximumSize(new Dimension(100, 30));
        searchBtn.setMargin(new Insets(2, 2, 2, 2));
        searchBtn.setFocusPainted(false);

        /* Refresh Button */
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(btnFont);
        refreshBtn.setBackground(Color.decode("#1167B1"));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setMaximumSize(new Dimension(100, 30));
        refreshBtn.setMargin(new Insets(2, 2, 2, 2));
        refreshBtn.setFocusPainted(false); 



        /* Search Bar Panel */
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setOpaque(false); 
        searchPanel.add(searchBar);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 00))); // Spacer
        searchPanel.add(searchBtn);
        searchPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Spacer
        searchPanel.add(refreshBtn); 
    
        /* Blue Panel */
        JPanel patientPanel = new JPanel(new BorderLayout());
        patientPanel.setBackground(Color.decode("#2A9DF4"));
        patientPanel.add(headers_homebtn_Panel, BorderLayout.NORTH);
        patientPanel.add(searchPanel, BorderLayout.CENTER);
        patientPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10)); 

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false); 
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 455, 00)); 

        /* Main Panel */
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setOpaque(false); 
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(patientPanel, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setTitle("Patient Management");
        setSize(1200, 800);
        setLocationRelativeTo(null); // Center on screen
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        PatientManagement myFrame = new PatientManagement();
        myFrame.initialize();
    }
}