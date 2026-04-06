package panels;

import model.*;
import model.Event;
import screens.*;
import tools.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


public class FeedPanel extends JPanel {

    private HomeScreen home;
    private JPanel gridPanel;
    private String currentFilter = "All";
    private String currentSort = "Date";

    public FeedPanel(HomeScreen home) {
        this.home = home;
        setLayout(new BorderLayout());
        setBackground(AppConstants.BG_MAIN);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buildUI();
    }

    private void buildUI() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(AppConstants.BG_MAIN);
        body.setBorder(BorderFactory.createEmptyBorder(24, 48, 20, 48));

        // Hero
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setOpaque(false);
        hero.setAlignmentX(CENTER_ALIGNMENT);

        JLabel eyebrow = new JLabel("your feed");
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 11));
        eyebrow.setForeground(AppConstants.TEAL);
        eyebrow.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(eyebrow);
        hero.add(Box.createVerticalStrut(6));

        JLabel heroTitle = new JLabel("What's happening around you.");
        heroTitle.setFont(AppConstants.F_HERO);
        heroTitle.setForeground(AppConstants.TEXT_PRI);
        heroTitle.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(heroTitle);
        hero.add(Box.createVerticalStrut(6));

        JLabel heroSub = new JLabel("Events from people you follow, personalized for you.");
        heroSub.setFont(AppConstants.F_SMALL);
        heroSub.setForeground(AppConstants.TEXT_SEC);
        heroSub.setAlignmentX(CENTER_ALIGNMENT);
        hero.add(heroSub);
        hero.add(Box.createVerticalStrut(16));
        body.add(hero);

        // XP Strip
        int myXP = Database.getUserXP(MainFile.currentUser.getUsername());
        String tierName = AppConstants.getTierName(myXP);
        int nextXP = AppConstants.getNextTierXP(myXP);
        String nextName = AppConstants.getNextTierName(myXP);

        JPanel xpStrip = new JPanel(new BorderLayout(12, 0));
        xpStrip.setBackground(Color.WHITE);
        xpStrip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConstants.BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)));
        xpStrip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        xpStrip.setAlignmentX(LEFT_ALIGNMENT);

        JPanel xpLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        xpLeft.setOpaque(false);
        JLabel tierLbl = new JLabel("\u2728 " + tierName);
        tierLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        tierLbl.setForeground(AppConstants.getTierColor(myXP));
        xpLeft.add(tierLbl);
        JLabel ptsLbl = new JLabel(myXP + " XP");
        ptsLbl.setFont(AppConstants.F_SMALL);
        ptsLbl.setForeground(AppConstants.TEXT_SEC);
        xpLeft.add(ptsLbl);
        xpStrip.add(xpLeft, BorderLayout.WEST);

        if (nextXP > 0) {
            JPanel xpRight = new JPanel();
            xpRight.setLayout(new BoxLayout(xpRight, BoxLayout.Y_AXIS));
            xpRight.setOpaque(false);
            JProgressBar bar = new JProgressBar(0, nextXP);
            bar.setValue(myXP);
            bar.setStringPainted(false);
            bar.setForeground(AppConstants.TEAL);
            bar.setBackground(AppConstants.TEAL_LIGHT);
            bar.setPreferredSize(new Dimension(200, 8));
            bar.setMaximumSize(new Dimension(200, 8));
            xpRight.add(bar);
            JLabel nextLbl = new JLabel((nextXP - myXP) + " XP to " + nextName);
            nextLbl.setFont(AppConstants.F_TINY);
            nextLbl.setForeground(AppConstants.TEXT_LIGHT);
            xpRight.add(nextLbl);
            xpStrip.add(xpRight, BorderLayout.EAST);
        }
        body.add(xpStrip);
        body.add(Box.createVerticalStrut(20));

        // Pills
        JPanel pillRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pillRow.setOpaque(false);
        pillRow.setAlignmentX(LEFT_ALIGNMENT);
        String[][] pills = {
            {"\uD83C\uDFB5 All", "All"}, {"\uD83C\uDFB6 Music", "TAG:music"},
            {"\uD83C\uDFC3 Sports", "TAG:sports"}, {"\uD83D\uDCDA Study", "TAG:algorithms"},
            {"\uD83C\uDFA8 Arts", "TAG:art"}, {"\uD83C\uDF55 Food", "TAG:food"},
            {"\uD83D\uDCBB Tech", "TAG:software"}, {"\uD83C\uDF3F Outdoor", "TAG:environment"}
        };
        for (String[] pill : pills) {
            JButton p = createPill(pill[0], currentFilter.equals(pill[1]) || (pill[1].equals("All") && currentFilter.equals("All")));
            p.addActionListener(e -> { currentFilter = pill[1]; refreshGrid(); });
            pillRow.add(p);
        }
        body.add(pillRow);
        body.add(Box.createVerticalStrut(18));

        // Section: Upcoming
        body.add(createSectionHeader("01", "Upcoming events", ""));
        body.add(Box.createVerticalStrut(10));

        gridPanel = new JPanel(new GridLayout(0, AppConstants.FEED_COLUMNS, 16, 16));
        gridPanel.setOpaque(false);
        gridPanel.setAlignmentX(LEFT_ALIGNMENT);
        body.add(gridPanel);

        // For You section
        ArrayList<Integer> recIds = Database.getRecommendedEventIds(MainFile.currentUser.getUsername(), 3);
        if (!recIds.isEmpty()) {
            body.add(Box.createVerticalStrut(24));
            body.add(createSectionHeader("02", "For you", "Based on your interests"));
            body.add(Box.createVerticalStrut(10));

            ArrayList<Event> allEvts = Database.getAllEvents();
            ArrayList<String> myI = Database.getInterests(MainFile.currentUser.getUsername());
            for (int rid : recIds) {
                for (Event rev : allEvts) {
                    if (rev.getId() == rid) {
                        body.add(createRecRow(rev, myI));
                        break;
                    }
                }
            }
        }

        add(UIHelper.wrapInScroll(body), BorderLayout.CENTER);
        refreshGrid();
    }

    
}
