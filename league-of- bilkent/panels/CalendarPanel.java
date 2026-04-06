package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.AppConstants;
import model.Database;
import model.Event;
import screens.HomeScreen;


public class CalendarPanel extends JPanel {

    private HomeScreen home;
    private YearMonth currentMonth;
    private JPanel calGrid;
    private JLabel monthLabel;

    public CalendarPanel(HomeScreen home) {
        this.home = home;
        this.currentMonth = YearMonth.now();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(32, 48, 20, 48));
        buildUI();
    }

    private void buildUI() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Color.WHITE);

        JLabel title = new JLabel("Calendar");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setAlignmentX(LEFT_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(12));

        // Month nav
        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        navRow.setBackground(Color.WHITE);
        navRow.setAlignmentX(LEFT_ALIGNMENT);
        JButton prev = new JButton("<");
        prev.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); refreshCal(); });//decrease the month by 1 and refresh

        JButton next = new JButton(">");
        next.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); refreshCal(); });//increase the month by 1 and refresh 

        monthLabel = new JLabel();
        monthLabel.setFont(AppConstants.F_TITLE);
        navRow.add(prev); navRow.add(monthLabel); navRow.add(next);
        header.add(navRow);
        header.add(Box.createVerticalStrut(12));

        add(header, BorderLayout.NORTH);

        calGrid = new JPanel(new GridLayout(0, 7, 2, 2));
        calGrid.setBackground(AppConstants.BORDER);
        JScrollPane scroll = new JScrollPane(calGrid);//create a scroll pane if the days wont fit the window
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        refreshCal();//refresh for the first opening
    }
    
    /*  THE CALENDAR REFRESH PROCESS:
     1.Clear the physical grid
     2.Add Mon, Tue, Wed, Thu, Fri, Sat, Sun
     3.Filter database events into [Day -> List of Events]
     4.If the 1st is a Wednesday, add 2 empty boxes
     5. LOOP (1 to 31)
         - Create a square for Day X
         - If today is Day X, make the text Blue
         - If Day X has events, draw a clickable badge for each
         - Add square to the grid
     6.redraw*/
    private void refreshCal() {
        calGrid.removeAll();
        monthLabel.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + currentMonth.getYear());

        // Day headers
        String[] days = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        for (String d : days) {
            JLabel lbl = new JLabel(d, JLabel.CENTER);
            lbl.setFont(AppConstants.F_TINY);
            lbl.setForeground(AppConstants.TEXT_SEC);
            lbl.setOpaque(true);
            lbl.setBackground(Color.WHITE);
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
            calGrid.add(lbl);
        }

        // Get events for this month
        ArrayList<Event> allEvents = Database.getAllEvents();//Fetches every event saved in your database
        Map<Integer, ArrayList<Event>> eventsByDay = new HashMap<>();//A system to group events by their day of month
        for (Event ev : allEvents) {
            if (ev.getDateTime().getYear() == currentMonth.getYear() &&
                ev.getDateTime().getMonthValue() == currentMonth.getMonthValue()) {
                int day = ev.getDateTime().getDayOfMonth();
                eventsByDay.computeIfAbsent(day, k -> new ArrayList<>()).add(ev);//If it's the first event for that day, create a new list; otherwise, add to existing list.
            }
        }

        // Empty cells before first day
        LocalDate first = currentMonth.atDay(1);
        int startDow = first.getDayOfWeek().getValue(); // Mon=1
        for (int i = 1; i < startDow; i++) {
            JPanel empty = new JPanel();
            empty.setBackground(new Color(0xFB, 0xFB, 0xFA));
            calGrid.add(empty);// If the 1st is a Wednesday (3), this adds 2 empty boxes to skip Mon and Tue.
        }

        // Days
        for (int d = 1; d <= currentMonth.lengthOfMonth(); d++) {
            JPanel cell = new JPanel();
            cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));// stacking elements vertically
            cell.setBackground(Color.WHITE);
            cell.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

            boolean isToday = currentMonth.atDay(d).equals(LocalDate.now());
            JLabel dayLbl = new JLabel(String.valueOf(d));
            if (isToday) {
                dayLbl.setFont(AppConstants.F_SECTION);
            } 
            else {
                dayLbl.setFont(AppConstants.F_SMALL);
            }
            
            if (isToday) {
                dayLbl.setForeground(AppConstants.ACCENT);
            } 
            else {
                dayLbl.setForeground(AppConstants.TEXT_PRI);
            }// If the day is today, it uses a special "Accent" font/color to stand out.

            dayLbl.setAlignmentX(LEFT_ALIGNMENT);
            cell.add(dayLbl);

            if (eventsByDay.containsKey(d)) {
                for (Event ev : eventsByDay.get(d)) {
                    JLabel evLbl = new JLabel(ev.getTitle());
                    evLbl.setFont(AppConstants.F_TINY);
                    evLbl.setForeground(Color.WHITE);
                    evLbl.setOpaque(true);
                    evLbl.setBackground(AppConstants.ACCENT);
                    evLbl.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 3));
                    evLbl.setAlignmentX(LEFT_ALIGNMENT);
                    evLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    final Event clicked = ev;
                    evLbl.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent e) { home.showEventDetail(clicked); }
                    });
                    cell.add(Box.createVerticalStrut(1));
                    cell.add(evLbl);
                }
            }

            calGrid.add(cell);
        }

        calGrid.revalidate();//recalculating the layout of grid
        calGrid.repaint();// redraw
    }
}
