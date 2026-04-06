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


  
}
