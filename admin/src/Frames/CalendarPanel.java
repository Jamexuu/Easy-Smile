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

        populateCalendar();
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
                populateCalendar();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentCalendar.add(Calendar.MONTH, 1);
                updateMonthYearLabel();
                populateCalendar();
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

        calendarTable.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = calendarTable.rowAtPoint(e.getPoint());
                int col = calendarTable.columnAtPoint(e.getPoint());
                Object value = calendarTable.getValueAt(row, col);
                if (value instanceof DayCell) {
                    DayCell dayCell = (DayCell) value;
                    // Notify parent frame
                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(CalendarPanel.this);
                    if (topFrame instanceof Frames.AppointmentManagementFrame) {
                        ((Frames.AppointmentManagementFrame) topFrame).showAppointmentsForDay(dayCell.getAppointments());
                    }
                }
            }
        });
    }

    private void updateMonthYearLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        monthYearLabel.setText(sdf.format(currentCalendar.getTime()));
    }

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
                        // appointment[3] is the display string
                        JLabel apptLabel = new JLabel("<html>" + appointment[3] + "</html>");
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

    private List<DAO.AppointmentDAO.Appointment> getAppointmentsForMonth(int year, int month) {
        List<DAO.AppointmentDAO.Appointment> appointments = new ArrayList<>();
        try {
            String startDate = String.format("%04d-%02d-01", year, month + 1);
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, 1);
            int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            String endDate = String.format("%04d-%02d-%02d", year, month + 1, lastDay);

            DAO.AppointmentDAO dao = new DAO.AppointmentDAO();
            appointments = dao.getAppointmentsByDateRange(startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appointments;
    }

    private void populateCalendar() {
        // Clear previous data
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                calendarModel.setValueAt(null, row, col);
            }
        }

        Calendar cal = (Calendar) currentCalendar.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0-based (Sunday = 0)
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Fetch all appointments for this month
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        List<DAO.AppointmentDAO.Appointment> monthAppointments = getAppointmentsForMonth(year, month);

        int row = 0, col = firstDayOfWeek;
        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(Calendar.DAY_OF_MONTH, day);
            Date date = cal.getTime();
            String dateStr = String.format("%04d-%02d-%02d", year, month + 1, day);

            // Filter appointments for this date
            List<Object[]> appointments = new ArrayList<>();
            for (DAO.AppointmentDAO.Appointment appt : monthAppointments) {
                if (dateStr.equals(appt.getAppointmentDate())) {
                    // Format time to 12-hour with AM/PM
                    String time = appt.getAppointmentTime();
                    String formattedTime = "";
                    try {
                        java.text.SimpleDateFormat sdf24 = new java.text.SimpleDateFormat("HH:mm:ss");
                        java.text.SimpleDateFormat sdf12 = new java.text.SimpleDateFormat("h:mma");
                        formattedTime = sdf12.format(sdf24.parse(time)).toLowerCase();
                    } catch (Exception ex) {
                        formattedTime = time;
                    }
                    // You may need to fetch ServiceDesc from ServiceDAO if not present in Appointment
                    String serviceDesc = appt.getServiceId(); // Replace with actual service description if available
                    String display = appt.getAppointmentId().toUpperCase() + "<br>" + formattedTime + " - " + serviceDesc;
                    String status = appt.getStatus();
                    appointments.add(new Object[]{appt.getAppointmentId(), serviceDesc, formattedTime, display, status});
                }
            }

            DayCell dayCell = new DayCell(day, date, appointments);
            calendarModel.setValueAt(dayCell, row, col);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
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