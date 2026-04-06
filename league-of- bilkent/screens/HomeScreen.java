package screens;

import model.*;
import javax.swing.*;

/*
 *   HomeScreen
 * 
 * main screen layout with card navigation
 * 
 *    buildTopBar  buildSideNav  buildContent
 *    showPanel  showFeed
 *    startPolling
 * 
 * TODO add profile navigation
 * TODO add event detail
 */
public class HomeScreen extends JFrame {

    private java.awt.CardLayout cardLayout;
    private JPanel contentPanel;
    private String currentView = "feed";
    private JButton messagesNavBtn;

    public HomeScreen() {
        setTitle("League of Bilkent");
        setSize(1200, 820);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new java.awt.BorderLayout());
        getContentPane().setBackground(new java.awt.Color(244, 248, 251));

        add(buildTopBar(), java.awt.BorderLayout.NORTH);
        add(buildSideNav(), java.awt.BorderLayout.WEST);
        add(buildContent(), java.awt.BorderLayout.CENTER);

        startPolling();
    }

    // gets db state every second to check for changes
    // needed for network sync so all users see updates
    private void startPolling() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int pollCount = 0;
                int lastHash = Database.getDbStateHash();
                while (true) {
                    try {
                        Thread.sleep(1000);
                        pollCount++;
                    } catch (Exception e) {
                        System.out.println("poll error " + pollCount);
                    }
                    if (!isVisible()) {
                        continue;
                    }
                    int newHash = Database.getDbStateHash();
                    if (newHash != lastHash) {
                        lastHash = newHash;
                        // TODO refresh panels when data changes
                    }
                }
            }
        }).start();
    }

    // top bar with brand name and search
    private JPanel buildTopBar() {
        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
        bar.setBackground(java.awt.Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(226, 232, 240)), BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JLabel brand = new JLabel("League of Bilkent");
        brand.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 15));
        brand.setForeground(new java.awt.Color(30, 30, 30));
        bar.add(brand);

        // connection status
        String connTxt;
        if (Database.customDbUrl == null) {
            connTxt = " [Local] ";
        } else {
            // split url to get just the ip part
            connTxt = " [Connected: " + Database.customDbUrl.split("//")[1].split(":")[0] + "] ";
        }
        JLabel statusLbl = new JLabel(connTxt);
        statusLbl.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 11));
        statusLbl.setForeground(new java.awt.Color(100, 116, 139));
        bar.add(statusLbl);

        bar.add(Box.createHorizontalGlue());

        // search field
        JTextField searchField = panels.UIHelper.createPlaceholderField("Search");
        searchField.setMaximumSize(new java.awt.Dimension(200, 34));
        searchField.setPreferredSize(new java.awt.Dimension(200, 34));
        bar.add(searchField);
        bar.add(Box.createHorizontalStrut(10));

        // logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
        bar.add(logoutBtn);

        return bar;
    }

    // side navigation with tabs
    private JPanel buildSideNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(java.awt.Color.WHITE);
        nav.setPreferredSize(new java.awt.Dimension(200, 0));
        nav.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new java.awt.Color(226, 232, 240)),BorderFactory.createEmptyBorder(20, 12, 20, 12)));

        nav.add(createNavLink("Feed", "feed"));
        nav.add(Box.createVerticalStrut(4));
        nav.add(createNavLink("Discover", "discover"));
        nav.add(Box.createVerticalStrut(4));
        nav.add(createNavLink("Calendar", "calendar"));
        nav.add(Box.createVerticalStrut(4));
        messagesNavBtn = createNavLink("Messages", "messages");
        nav.add(messagesNavBtn);
        nav.add(Box.createVerticalStrut(4));
        nav.add(createNavLink("Leaderboard", "leaderboard"));
        nav.add(Box.createVerticalStrut(4));
        nav.add(createNavLink("Notifications", "notifications"));

        nav.add(Box.createVerticalGlue());

        // network settings button at bottom
        JButton netBtn = new JButton("Network Settings");
        netBtn.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 11));
        netBtn.setFocusPainted(false);
        netBtn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        netBtn.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 32));
        netBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                new panels.NetworkDialog(HomeScreen.this).setVisible(true);
            }
        });
        nav.add(netBtn);

        return nav;
    }

    // card layout for switching content
    private JPanel buildContent() {
        cardLayout = new java.awt.CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new java.awt.Color(244, 248, 251));

        // placeholder panels for now
        contentPanel.add(createPlaceholder("Feed Panel"), "feed");
        contentPanel.add(createPlaceholder("Discover Panel"), "discover");
        contentPanel.add(createPlaceholder("Calendar Panel"), "calendar");
        contentPanel.add(createPlaceholder("Messages Panel"), "messages");
        contentPanel.add(createPlaceholder("Leaderboard Panel"), "leaderboard");
        contentPanel.add(createPlaceholder("Notifications Panel"), "notifications");

        return contentPanel;
    }

    private JPanel createPlaceholder(String name) {
        JPanel p = new JPanel(new java.awt.BorderLayout());
        p.setBackground(new java.awt.Color(244, 248, 251));
        JLabel lbl = new JLabel(name + " - TODO", SwingConstants.CENTER);
        lbl.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 18));
        lbl.setForeground(new java.awt.Color(148, 163, 184));
        p.add(lbl, java.awt.BorderLayout.CENTER);
        return p;
    }

    private JButton createNavLink(String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 13));
        btn.setForeground(new java.awt.Color(100, 116, 139));
        btn.setBackground(java.awt.Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        btn.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 38));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        btn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showPanel(panelName);
            }
        });
        return btn;
    }

    public void showPanel(String name) {
        currentView = name;
        cardLayout.show(contentPanel, name);
    }

    public void showFeed() {
        showPanel("feed");
    }
}
