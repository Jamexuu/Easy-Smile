package Frames;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class CalendarPanel extends JPanel {
    private JTable calendarTable;
    private DefaultTableModel calendarModel;
    private JLabel monthYearLabel;
    private Calendar currentCalendar;
    private JButton prevButton, nextButton;

    private final String[] DAYS_OF_WEEK = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    public CalendarPanel() {
        setLayout(new BorderLayout());

        // Initialize the calendar to current date
        currentCalendar = Calendar.getInstance();

        // Create the navigation panel with buttons
        createNavigationPanel();

        // Create the calendar table
        createCalendarTable();
    }

    private void createNavigationPanel() {
        JPanel navigationPanel = new JPanel(new BorderLayout());

        // Month and Year display
        monthYearLabel = new JLabel("", JLabel.CENTER);
        monthYearLabel.setFont(new Font("Arial", Font.BOLD, 14));
        updateMonthYearLabel();

        // Previous and Next buttons
        JPanel buttonPanel = new JPanel();
        prevButton = new JButton("< Previous");
        nextButton = new JButton("Next >");

        // Add action listeners to buttons
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentCalendar.add(Calendar.MONTH, -1);
                updateMonthYearLabel();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentCalendar.add(Calendar.MONTH, 1);
                updateMonthYearLabel();
            }
        });

        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        navigationPanel.add(monthYearLabel, BorderLayout.CENTER);
        navigationPanel.add(buttonPanel, BorderLayout.EAST);

        add(navigationPanel, BorderLayout.NORTH);
    }

    private void createCalendarTable() {
        // Create table model
        calendarModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Add day of week column headers
        for (String day : DAYS_OF_WEEK) {
            calendarModel.addColumn(day);
        }

        // Set up 6 rows (max needed for any month)
        calendarModel.setRowCount(6);

        // Create the table
        calendarTable = new JTable(calendarModel);
        calendarTable.setRowHeight(80);
        calendarTable.setDefaultRenderer(Object.class, new CalendarCellRenderer());

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(calendarTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateMonthYearLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        monthYearLabel.setText(sdf.format(currentCalendar.getTime()));
    }

    // Custom renderer for calendar cells
    private class CalendarCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                    boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEtchedBorder());

            if (value instanceof DayCell) {
                DayCell dayCell = (DayCell) value;

                // Day number label at top
                JLabel dayLabel = new JLabel(String.valueOf(dayCell.getDay()));
                dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
                panel.add(dayLabel, BorderLayout.NORTH);

                // Highlight today's date
                Calendar today = Calendar.getInstance();
                Calendar cellDate = Calendar.getInstance();
                cellDate.setTime(dayCell.getDate());

                if (today.get(Calendar.YEAR) == cellDate.get(Calendar.YEAR) &&
                        today.get(Calendar.MONTH) == cellDate.get(Calendar.MONTH) &&
                        today.get(Calendar.DAY_OF_MONTH) == cellDate.get(Calendar.DAY_OF_MONTH)) {
                    panel.setBackground(new Color(230, 230, 250)); // Light lavender for today
                }

                // Add appointments to the cell
                if (!dayCell.getAppointments().isEmpty()) {
                    JPanel appointmentsPanel = new JPanel();
                    appointmentsPanel.setLayout(new BoxLayout(appointmentsPanel, BoxLayout.Y_AXIS));

                    // Show up to 3 appointments
                    int count = Math.min(dayCell.getAppointments().size(), 3);
                    for (int i = 0; i < count; i++) {
                        Object[] appointment = dayCell.getAppointments().get(i);
                        String patientName = (String) appointment[1];
                        String time = ((String) appointment[3]).substring(11, 16); // HH:MM

                        JLabel apptLabel = new JLabel(time + " - " + patientName);
                        apptLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                        appointmentsPanel.add(apptLabel);
                    }

                    // If there are more appointments than shown
                    if (dayCell.getAppointments().size() > 3) {
                        JLabel moreLabel = new JLabel("+" + (dayCell.getAppointments().size() - 3) + " more");
                        moreLabel.setFont(new Font("Arial", Font.ITALIC, 9));
                        appointmentsPanel.add(moreLabel);
                    }

                    panel.add(appointmentsPanel, BorderLayout.CENTER);
                }
            }

            return panel;
        }
    }

    // Helper class to store day information and appointments
     private static class DayCell {
        private int day;
        private Date date;
        private List<Object[]> appointments;

        @SuppressWarnings("unused")
        public DayCell(int day, Date date, List<Object[]> appointments) {
            this.day = day;
            this.date = date;
            this.appointments = appointments;
        }

        public int getDay() {
            return day;
        }

        public Date getDate() {
            return date;
        }

        public List<Object[]> getAppointments() {
            return appointments;
        }

        @SuppressWarnings("unused")
        public void addAppointment(Object[] appointment) {
            appointments.add(appointment);
        }
    }

    // Test method to demonstrate the calendar
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Appointment Calendar");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            CalendarPanel calendar = new CalendarPanel();
            frame.add(calendar);

            frame.setVisible(true);
        });
    }
}