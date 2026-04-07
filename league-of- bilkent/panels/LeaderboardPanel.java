package panels;

import model.*;
import screens.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class LeaderboardPanel extends JPanel {

    private HomeScreen home;

    /**
     * Constructs the leaderboard panel with the provided home screen reference.
     * Sets up the layout and builds the UI components for displaying XP rankings.
     * This panel shows the top 50 players sorted by XP, with tier indicators
     * and clickable rows for profile navigation.
     *
     * @param home the parent HomeScreen for navigation callbacks
     */
    public LeaderboardPanel(HomeScreen home) {
        this.home = home;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        buildUI();
    }

    /**
     * Builds the main UI for the leaderboard panel.
     * Creates page title, subtitle, tier legend, separator,
     * and populates the leaderboard with top 50 users from database.
     * Each user is displayed as a ranked row with XP progress bar.
     * 
     * The leaderboard serves as a central hub for competitive visibility,
     * showing players their standing and motivating progression through
     * visual tier indicators and progress bars.
     */
    private void buildUI() {
        JPanel content = UIHelper.createPagePanel();

        content.add(UIHelper.createPageTitle(AppConstants.PAGE_LEADERBOARD));
        content.add(Box.createVerticalStrut(4));
        content.add(UIHelper.createSubtitle(AppConstants.PAGE_LEADER_SUB));
        content.add(Box.createVerticalStrut(20));

        // Create tier legend panel with colored badges for each tier
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        legend.setBackground(Color.WHITE);
        legend.setAlignmentX(LEFT_ALIGNMENT);

        // Tier legend: displays all ranks from Bronze to Diamond with XP thresholds
        for (int i = 0; i < AppConstants.TIER_NAMES.length; i++) {
            JLabel tl = new JLabel(AppConstants.TIER_NAMES[i] + " (" + AppConstants.TIER_THRESHOLDS[i] + "+)");
            tl.setFont(AppConstants.F_TINY);
            tl.setForeground(AppConstants.TIER_COLORS[i]);
            tl.setOpaque(true);
            tl.setBackground(new Color(AppConstants.TIER_COLORS[i].getRed(), AppConstants.TIER_COLORS[i].getGreen(),
                AppConstants.TIER_COLORS[i].getBlue(), 20));
            tl.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            legend.add(tl);
        }
        content.add(legend);
        content.add(Box.createVerticalStrut(14));
        content.add(UIHelper.createSeparator());
        content.add(Box.createVerticalStrut(14));

        // Fetch top 50 users from database and render each as a ranked row
        ArrayList<User> leaders = Database.getLeaderboard(50);
        int rank = 1;
        for (User u : leaders) {
            content.add(createRow(rank++, u));
            content.add(Box.createVerticalStrut(4));
        }

        add(UIHelper.wrapInScroll(content), BorderLayout.CENTER);
    }

    /**
     * Creates a single leaderboard row for a user.
     * Displays rank badge, user info (name, username, tier, XP),
     * and a visual progress bar showing progress toward next tier.
     * Each row is interactive - clicking navigates to the user's profile.
     * 
     * The row provides at-a-glance information: position, identity,
     * current tier status, and visual progress toward the next tier,
     * encouraging friendly competition among players.
     *
     * @param rank the user's rank position (1-based)
     * @param u    the User object containing player data
     * @return JPanel configured as a leaderboard row with click navigation
     */
    private JPanel createRow(int rank, User u) {
        // Row panel with conditional styling: top 3 get subtle highlight
        JPanel row = new JPanel(new BorderLayout());
        if (rank <= 3) {
            row.setBackground(new Color(0xFB, 0xFB, 0xFA));
        } else {
            row.setBackground(Color.WHITE);
        }
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppConstants.BORDER),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        row.setAlignmentX(LEFT_ALIGNMENT);

        // Rank badge: medal emoji for top 3, number for rest
        String rankStr;
        if (rank == 1) {
            rankStr = "\uD83E\uDD47";
        } else if (rank == 2) {
            rankStr = "\uD83E\uDD48";
        } else if (rank == 3) {
            rankStr = "\uD83E\uDD49";
        } else {
            rankStr = "#" + rank;
        }
        JLabel rankLbl = new JLabel(rankStr);
        rankLbl.setFont(AppConstants.F_TITLE);
        rankLbl.setPreferredSize(new Dimension(44, 34));
        row.add(rankLbl, BorderLayout.WEST);

        // User info: display name, username, tier and XP
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        JLabel name = new JLabel(u.getDisplayName() + "  @" + u.getUsername());
        name.setFont(AppConstants.F_NORMAL);
        name.setForeground(AppConstants.TEXT_PRI);
        info.add(name);

        int xp = u.getXp();
        JLabel tierLbl = new JLabel(AppConstants.getTierName(xp) + "  |  " + xp + " XP");
        tierLbl.setFont(AppConstants.F_SMALL);
        tierLbl.setForeground(AppConstants.getTierColor(xp));
        info.add(tierLbl);
        row.add(info, BorderLayout.CENTER);

        // XP progress bar: custom painted to show progress toward next tier
        JPanel xpBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Calculate progress percentage toward next tier
                int nextXP = AppConstants.getNextTierXP(xp);
                if (nextXP > 0) {
                    int currThreshold = AppConstants.TIER_THRESHOLDS[AppConstants.getTierIndex(xp)];
                    double pct = (double)(xp - currThreshold) / (nextXP - currThreshold);
                    // Draw background bar
                    g.setColor(AppConstants.BORDER);
                    g.fillRect(0, 12, getWidth(), 6);
                    // Draw filled portion based on progress
                    g.setColor(AppConstants.getTierColor(xp));
                    g.fillRect(0, 12, (int)(getWidth() * pct), 6);
                }
            }
        };
        xpBar.setOpaque(false);
        xpBar.setPreferredSize(new Dimension(100, 30));
        row.add(xpBar, BorderLayout.EAST);

        // Make row clickable to navigate to user's profile
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.addMouseListener(e -> home.navigateToProfile(u));

        return row;
    }


}
