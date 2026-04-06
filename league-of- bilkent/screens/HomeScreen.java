package screens;

import model.*;
import model.Event;
import panels.*;
import tools.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 *     HomeScreen
 * 
 * main screen after login
 * card layout navigation with profile stack
 * 
 *  navigation: showFeed showMyProfile navigateToProfile
 *              goBackFromProfile showEventDetail
 * 
 *  logic:      changeAttendance addComment
 *              followUser unfollowUser createEvent
 * 
 *  ui:         buildTopBar buildSideNav buildContent createNavLink
 * 
 * uses all panels Database UIHelper MainFile
 */
public class HomeScreen extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private String currentView = "feed";
    private MessagingPanel messagingPanel;
    private JButton messagesNavBtn;
    private ProfilePanel myProfilePanel;
    private ProfilePanel viewedProfilePanel;
    private java.util.Stack<User> profileStack = new java.util.Stack<>();
    private User currentProfileTarget = null;
    private Event currentEventTarget = null;

    public HomeScreen() {
        setTitle("League of Bilkent - " + MainFile.currentUser.getDisplayName());
        setSize(AppConstants.WINDOW_WIDTH, AppConstants.WINDOW_HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(AppConstants.BG_MAIN);

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildSideNav(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
        cardLayout.show(contentPanel, "feed");
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
                    // only refresh if the hash actually changed
                    int newHash = Database.getDbStateHash();
                    if (newHash != -1 && newHash != lastHash) {
                        lastHash = newHash;
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                refreshCurrentView();
                            }
                        });
                    }
                }
            }
        }).start();
    }


    private void refreshCurrentView() {
        if (currentView.equals("feed")) {
            showFeed();
        } else if (currentView.equals("discover")) {
            showPanel(new DiscoverPanel(this), "discover");
        } else if (currentView.equals("calendar")) {
            showPanel(new CalendarPanel(this), "calendar");
        } else if (currentView.equals("messages")) {
            if (messagingPanel == null) {
                messagingPanel = new MessagingPanel(this);
                showPanel(messagingPanel, "messages");
            } else {
                messagingPanel.refreshConversations();
                messagingPanel.loadChat();
            }
        } else if (currentView.equals("leaderboard")) {
            showPanel(new LeaderboardPanel(this), "leaderboard");
        } else if (currentView.equals("notif")) {
            showPanel(new NotificationsPanel(this), "notif");
        } else if (currentView.equals("myProfile")) {
            showMyProfile();
        } else if (currentView.equals("viewProfile")) {
            if (!profileStack.isEmpty()) {
                currentProfileTarget = profileStack.peek();
                currentProfileTarget = Database.getUserWithUsername(currentProfileTarget.getUsername());
                viewedProfilePanel = new ProfilePanel(currentProfileTarget, this, true);
                contentPanel.add(viewedProfilePanel, "viewProfile");
                cardLayout.show(contentPanel, "viewProfile");
            }
        } else if (currentView.equals("detail")) {
            if (currentEventTarget != null) {
                for (Event e : Database.getAllEvents()) {
                    if (e.getId() == currentEventTarget.getId()) {
                        currentEventTarget = e;
                        break;
                    }
                }
                showEventDetail(currentEventTarget);
            }
        }
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, AppConstants.BORDER),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        bar.setPreferredSize(new Dimension(0, 48));

        // Brand
        JLabel brand = new JLabel("League of Bilkent");
        brand.setFont(new Font("SansSerif", Font.BOLD, 15));
        brand.setForeground(new Color(30, 30, 30));
        brand.setMaximumSize(brand.getPreferredSize());
        bar.add(brand);

        bar.add(Box.createHorizontalStrut(15));
        String connTxt;
        if (Database.customDbUrl == null) {
            connTxt = " [Local] ";
        } else {
            // split url to get just the ip part
            connTxt = " [Connected: " + Database.customDbUrl.split("//")[1].split(":")[0] + "] ";
        }
        JLabel statusLbl = new JLabel(connTxt);
        statusLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        Color statusColor;
        if (Database.customDbUrl == null) {
            statusColor = AppConstants.TEXT_LIGHT;
        } else {
            statusColor = AppConstants.SUCCESS;
        }
        statusLbl.setForeground(statusColor);
        bar.add(statusLbl);

        bar.add(Box.createHorizontalGlue());

        // Search
        JTextField searchField = UIHelper.createPlaceholderField("Search...");
        searchField.setPreferredSize(new Dimension(160, 32));
        searchField.setMaximumSize(new Dimension(160, 32));
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String q = UIHelper.getFieldText(searchField, "Search...");
                if (!q.isEmpty()) {
                    SearchPanel sp = new SearchPanel(HomeScreen.this);
                    sp.searchFor(q);
                    showPanel(sp, "search");
                }
            }
        });
        bar.add(searchField);
        bar.add(Box.createHorizontalStrut(8));

        // Create
        JButton btnCreate = new JButton("+ Create");
        btnCreate.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setBackground(AppConstants.TEAL);
        btnCreate.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btnCreate.setFocusPainted(false);
        btnCreate.setOpaque(true);
        btnCreate.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCreate.setMaximumSize(new Dimension(100, 32));
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel(new CreateEventPanel(HomeScreen.this), "create");
            }
        });
        bar.add(btnCreate);
        bar.add(Box.createHorizontalStrut(8));

        // Profile
        String displayName = MainFile.currentUser.getDisplayName();
        JButton btnProfile = new JButton(displayName);
        btnProfile.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnProfile.setForeground(AppConstants.TEXT_PRI);
        btnProfile.setBorderPainted(false);
        btnProfile.setContentAreaFilled(false);
        btnProfile.setFocusPainted(false);
        btnProfile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnProfile.setMaximumSize(btnProfile.getPreferredSize());
        btnProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMyProfile();
            }
        });
        bar.add(btnProfile);
        bar.add(Box.createHorizontalStrut(8));

        // Logout
        JButton btnLogout = new JButton("Log out");
        btnLogout.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btnLogout.setForeground(AppConstants.DANGER);
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.setMaximumSize(btnLogout.getPreferredSize());
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MainFile.currentUser = null;
                MainFile.loginScreen.setVisible(true);
            }
        });
        bar.add(btnLogout);

        return bar;
    }



    private JPanel buildSideNav() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, AppConstants.BORDER),
                BorderFactory.createEmptyBorder(16, 14, 16, 14)));
        nav.setPreferredSize(new Dimension(180, 0));

        String[] tabs = { "Feed", "Discover", "Calendar", "Messages", "Leaderboard", "Notifications" };
        String[] actions = { "feed", "discover", "calendar", "messages", "leaderboard", "notif" };
        for (int i = 0; i < tabs.length; i++) {
            JButton btn = createNavLink(tabs[i], i == 0);
            if (tabs[i].equals("Messages")) {
                messagesNavBtn = btn;
            }
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setAlignmentX(LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            final String action = actions[i];
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (action.equals("feed")) {
                        showFeed();
                    } else if (action.equals("discover")) {
                        showPanel(new DiscoverPanel(HomeScreen.this), "discover");
                    } else if (action.equals("calendar")) {
                        showPanel(new CalendarPanel(HomeScreen.this), "calendar");
                    } else if (action.equals("messages")) {
                        if (messagingPanel == null) {
                            messagingPanel = new MessagingPanel(HomeScreen.this);
                        }
                        showPanel(messagingPanel, "messages");
                    } else if (action.equals("leaderboard")) {
                        showPanel(new LeaderboardPanel(HomeScreen.this), "leaderboard");
                    } else if (action.equals("notif")) {
                        showPanel(new NotificationsPanel(HomeScreen.this), "notif");
                    }
                }
            });
            nav.add(btn);
        }

        return nav;
    }

    private JPanel buildContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppConstants.BG_MAIN);
        contentPanel.add(new FeedPanel(this), "feed");
        contentPanel.add(new SearchPanel(this), "search");
        contentPanel.add(new DiscoverPanel(this), "discover");
        contentPanel.add(new CalendarPanel(this), "calendar");
        contentPanel.add(new LeaderboardPanel(this), "leaderboard");
        contentPanel.add(new CreateEventPanel(this), "create");
        contentPanel.add(new MessagingPanel(this), "messages");
        contentPanel.add(new NotificationsPanel(this), "notif");
        return contentPanel;
    }

    private void showPanel(JPanel _panel, String _name) {
        currentView = _name;
        contentPanel.add(_panel, _name);
        cardLayout.show(contentPanel, _name);
    }

    public void showFeed() {
        showPanel(new FeedPanel(this), "feed");
    }

    public void showMyProfile() {
        myProfilePanel = new ProfilePanel(MainFile.currentUser, this, false);
        showPanel(myProfilePanel, "myProfile");
    }

    public void navigateToProfile(User user) {
        currentProfileTarget = user;
        if (user.getUsername().equals(MainFile.currentUser.getUsername())) {
            showMyProfile();
            return;
        }
        // dont push same user twice to the stack
        if (profileStack.isEmpty() || !profileStack.peek().getUsername().equals(user.getUsername())) {
            profileStack.push(user);
        }
        viewedProfilePanel = new ProfilePanel(user, this, true);
        showPanel(viewedProfilePanel, "viewProfile");
    }

    public void goBackFromProfile() {
        if (!profileStack.isEmpty()) {
            profileStack.pop();
        }
        if (!profileStack.isEmpty()) {
            User prev = profileStack.peek();
            currentProfileTarget = prev;
            viewedProfilePanel = new ProfilePanel(prev, this, true);
            showPanel(viewedProfilePanel, "viewProfile");
        } else {
            showFeed();
        }
    }

    public void showEventDetail(Event event) {
        currentEventTarget = event;
        showPanel(new EventDetailPanel(event, this), "detail");
    }

    // handles rsvp for events, also gives xp to the user
    public void changeAttendance(Event event, AttendanceStatus status) {
        String me = MainFile.currentUser.getUsername();
        if (status == null) {
            event.removeAttendance(me);
            Database.removeAttendance(event.getId(), me);
        } else {
            if (status == AttendanceStatus.GOING && event.isFull()) {
                UIHelper.showError(this, "Event is full!");
                return;
            }
            event.setAttendance(me, status);
            Database.setAttendance(event.getId(), me, status);
            Database.addXP(me, AppConstants.XP_ATTEND_EVENT);
            if (event.getXpReward() > 0) {
                Database.addXP(me, event.getXpReward());
            }
        }
    }

    public void addComment(Event event, String text, int parentId) {
        String me = MainFile.currentUser.getUsername();
        String time = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        Comment comment = new Comment(0, me, text, time, parentId);
        int cid = Database.addToDatabase(comment, event.getId());
        comment = new Comment(cid, me, text, time, parentId);
        event.addComment(comment);
        Database.addXP(me, AppConstants.XP_COMMENT);
        if (!me.equals(event.getCreatorUsername())) {
            Database.addNotification(event.getCreatorUsername(),
                    MainFile.currentUser.getDisplayName() + " commented on: " + event.getTitle());
        }
        if (parentId > 0) {
            for (Comment c : event.getComments()) {
                if (c.getId() == parentId && !c.getUsername().equals(me)) {
                    Database.addNotification(c.getUsername(),
                            MainFile.currentUser.getDisplayName() + " replied to your comment on: " + event.getTitle());
                    break;
                }
            }
        }
    }

    public void followUser(String target) {
        String me = MainFile.currentUser.getUsername();
        if (MainFile.currentUser.getFollowing().contains(target)) {
            return;
        }
        Database.addFollow(me, target);
        MainFile.currentUser.getFollowing().add(target);
        Database.addXP(target, AppConstants.XP_GAIN_FOLLOWER);
        Database.addNotification(target, MainFile.currentUser.getDisplayName() + " started following you!");
    }

    public void unfollowUser(String target) {
        String me = MainFile.currentUser.getUsername();
        Database.deleteFollow(me, target);
        MainFile.currentUser.getFollowing().remove(target);
        Database.addXP(target, -AppConstants.XP_GAIN_FOLLOWER);
    }

    public void createEvent(Event event) {
        int id = Database.addToDatabase(event);
        event.setId(id);
        Database.addXP(MainFile.currentUser.getUsername(), AppConstants.XP_CREATE_EVENT);
        for (String follower : MainFile.currentUser.getFollowers()) {
            Database.addNotification(follower,
                    MainFile.currentUser.getDisplayName() + " created a new event: " + event.getTitle());
        }
    }

    private JButton createNavLink(String _text, boolean _active) {
        JButton b = new JButton(_text);
        int fontStyle;
        if (_active) {
            fontStyle = Font.BOLD;
        } else {
            fontStyle = Font.PLAIN;
        }
        b.setFont(new Font("SansSerif", fontStyle, 12));
        Color textColor;
        if (_active) {
            textColor = AppConstants.TEAL;
        } else {
            textColor = AppConstants.TEXT_SEC;
        }
        b.setForeground(textColor);
        b.setBackground(Color.WHITE);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(b.getPreferredSize().width, 38));
        b.setBorder(BorderFactory.createEmptyBorder(16, 14, 16, 14));
        if (_active) {
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, AppConstants.TEAL),
                    BorderFactory.createEmptyBorder(16, 14, 14, 14)));
        }
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!_active) {
                    b.setForeground(AppConstants.TEAL);
                }
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!_active) {
                    b.setForeground(AppConstants.TEXT_SEC);
                }
            }
        });
        return b;
    }
}
