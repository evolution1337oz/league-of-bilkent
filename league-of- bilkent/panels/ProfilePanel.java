package panels;

import model.*;
import model.Event;
import screens.*;
import tools.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ProfilePanel extends JPanel {

    private User user;
    private HomeScreen home;
    private boolean isOtherUser; // checks whether it is your account that you are viewing or someone elses

    public ProfilePanel(User user, HomeScreen home, boolean isOtherUser) {

        this.user = user;
        this.home = home;
        this.isOtherUser = isOtherUser;

        setLayout(new BorderLayout()); //divides the panel into north west south east and cenetr
        setBackground(Color.WHITE); //sets the background color to white
        setBorder(BorderFactory.createEmptyBorder(32, 48, 20, 48)); //creates a space between the elements and the borders
        buildUI();
    }

    private void buildUI() {


        JPanel content = new JPanel(); // main panel
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS)); //elements are vertically alligned
        content.setBackground(Color.WHITE); //background color is white

        if (isOtherUser) { //if we are viewing someone elses profile

            JButton back= UIHelper.createOutlineButton("< Back", AppConstants.ACCENT); //creates a "back" button
            back.setAlignmentX(LEFT_ALIGNMENT); // button is alligned to left

            back.addActionListener(new ActionListener (){
                public void actionPerformed(ActionEvent e){


                    home.goBackFromProfile(); //when back button is clicked goBackFromProfile metot is called
                }
            });

            content.add(back); //button added to the main panel

            content.add(Box.createVerticalStrut(12)); // there is a space 12px under the btton
        }


        JPanel header = new JPanel(new BorderLayout()); 

        header.setBackground(new Color(0xFB, 0xFB, 0xFA)); //background color is a light grey

        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConstants.BORDER), //a thin border is added
            BorderFactory.createEmptyBorder(20, 24, 20, 24))); // space is left

        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        header.setAlignmentX(LEFT_ALIGNMENT); // header alligned to left

        JPanel nameCol = new JPanel(); // panel that has the user info
        nameCol.setLayout(new BoxLayout(nameCol, BoxLayout.Y_AXIS)); // vertically alligned
        nameCol.setOpaque(false);

        JLabel nameLbl = new JLabel(user.getDisplayName());

        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        nameLbl.setForeground(AppConstants.TEXT_PRI); // main text color
        nameCol.add(nameLbl); // added to the left panel

        String labelText = "@" + user.getUsername();


        if (user.isVerified()){
            labelText = labelText + " \u2713"; //gives a tick if verified
        }
        if (user.isClub()){
            labelText = labelText + " [CLUB]"; // writes club is the user is a club 
        }
        JLabel userLbl = new JLabel(labelText);

     
        userLbl.setFont(AppConstants.F_SMALL);
        userLbl.setForeground(AppConstants.TEXT_SEC);

        nameCol.add(userLbl); // name is added to nameCol
        nameCol.add(Box.createVerticalStrut(6)); // there is a 6px space under name

    
        int xp = Database.getUserXP(user.getUsername()); // gets the xp from the database

        String tierName =AppConstants.getTierName(xp); //gets the tier name according to the xp level
        Color tierColor = AppConstants.getTierColor(xp);

        JLabel tierLbl= new JLabel(tierName + "  |  " + xp + " XP"); //prints the xp 

        tierLbl.setFont(AppConstants.F_SECTION);
        tierLbl.setForeground(tierColor);
        nameCol.add(tierLbl);

        int nextXP = AppConstants.getNextTierXP(xp); //gets the xp required for the next tier

        if (nextXP>0) { //if there is a next tier


            int currThreshold = AppConstants.TIER_THRESHOLDS[AppConstants.getTierIndex(xp)]; //gets the minimum score needed for the current tier
            double pct= (double)(xp - currThreshold) / (nextXP - currThreshold); //calculate the percentage 

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue((int)(pct * 100)); // fills the bar according to the percentage
            bar.setStringPainted(true);

            bar.setString(xp + " / " + nextXP + " XP to " + AppConstants.getNextTierName(xp)); // prints the rquired xp to the next tier
            bar.setFont(AppConstants.F_TINY);
            bar.setMaximumSize(new Dimension(250, 16));

            nameCol.add(Box.createVerticalStrut(4)); //leaves a 4px space
            nameCol.add(bar);
        }

        header.add(nameCol, BorderLayout.CENTER);

        JPanel rightCol =new JPanel();

        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS)); //places the elements vertically 
        rightCol.setOpaque(false);

        ArrayList<String> followers =Database.getFollowers(user.getUsername()); //gets the followers from the database
        ArrayList<String> following = Database.getFollowing(user.getUsername()); //gets the followings from the database

        JLabel statsLbl= new JLabel("<html><u>" + followers.size() + " Followers</u>  |  <u>" + following.size() + " Following</u></html>"); //followers and followings are underlined

        statsLbl.setFont(AppConstants.F_SMALL); // small font
        statsLbl.setForeground(AppConstants.ACCENT);

        statsLbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // so it seems clickable 
        statsLbl.setAlignmentX(RIGHT_ALIGNMENT); //alligned to right

        statsLbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { 
                showFollowDialog(followers, following);  // it opens up the followers and following list
            }
        });


        rightCol.add(statsLbl); //added to the right column 

        if (isOtherUser) { // if you are viewing someone elses profile

            rightCol.add(Box.createVerticalStrut(8)); // 8px space

            boolean isFollowing = MainFile.currentUser.getFollowing().contains(user.getUsername()); //checks if you are following the profile you view

            JButton followBtn; //follow and unfollow button 

            if (isFollowing) { //if you are following

                followBtn = UIHelper.createOutlineButton("Unfollow", AppConstants.DANGER); // adds a red button


                followBtn.addActionListener(new ActionListener() { //if button is clicked it unfollows the users and refresh the profile
                    public void actionPerformed(ActionEvent e){
                        home.unfollowUser(user.getUsername());
                        home.navigateToProfile(user);
                    }
                }); 
                    
            } else {

                followBtn = UIHelper.createButton("Follow", AppConstants.ACCENT, Color.WHITE); // if you are not following a filled button 

                followBtn.addActionListener(new ActionListener() { //when clicked it starts to follow the user and refreshes the profile
                    public void actionPerformed(ActionEvent e){
                       home.followUser(user.getUsername());
                       home.navigateToProfile(user);  
                    }  
                });
            }

            followBtn.setAlignmentX(RIGHT_ALIGNMENT); // follow button is alligned to right
            rightCol.add(followBtn); //added to the right column
            rightCol.add(Box.createVerticalStrut(4)); // 4px space under follow button 


            JButton msgBtn = UIHelper.createOutlineButton("Message", AppConstants.TEXT_SEC); //message button is created

            msgBtn.setAlignmentX(RIGHT_ALIGNMENT); //alligned to right

            msgBtn.addActionListener(new ActionListener() { //when button is clicked
                public void actionPerformed(ActionEvent e) {

                    String text = JOptionPane.showInputDialog(ProfilePanel.this,
                        "Send message to @" + user.getUsername() + ":", "Message", JOptionPane.PLAIN_MESSAGE); // a popup screen is open to send text 


                    if (text != null && !text.trim().isEmpty()) { //check if the message is empty

                        Database.sendMessage(MainFile.currentUser.getUsername(), user.getUsername(), text.trim()); //message is sent to the database
                        UIHelper.showSuccess(ProfilePanel.this, "Message sent!"); // user is informed 
                    }
                }
            });
            
            rightCol.add(msgBtn); //message button is added to the right column 
        }

        header.add(rightCol, BorderLayout.EAST); // right column is placed to the right of the header

        content.add(header);
        content.add(Box.createVerticalStrut(12)); // 12px space under 

        if (user.getBio() != null && !user.getBio().isEmpty()) { // if bio is not empty and it exist 

            JLabel bioLbl = new JLabel("<html>" + user.getBio() + "</html>"); //bio of the user is printed inside the label

            bioLbl.setFont(AppConstants.F_NORMAL);
            bioLbl.setForeground(AppConstants.TEXT_SEC); //has a faded color
            bioLbl.setAlignmentX(LEFT_ALIGNMENT); //alligned to left


            content.add(bioLbl);
            content.add(Box.createVerticalStrut(8)); //8 px spacing
        }


        if (!isOtherUser){ // if you are viewing your own prodile

            JButton editBio =UIHelper.createOutlineButton("Edit Bio", AppConstants.TEXT_SEC); // an edit bio button is created

            editBio.setAlignmentX(LEFT_ALIGNMENT); // button alligned to left

            editBio.addActionListener(new ActionListener() { //when edit bio button is clicked
                public void actionPerformed(ActionEvent e) {

                    String newBio = JOptionPane.showInputDialog(ProfilePanel.this, "Enter new bio:", user.getBio()); // it gets the current bio

                    if (newBio!=null) { // if bio is not empty
                        Database.updateUserBio(user.getUsername(), newBio);
                        user.setBio(newBio); //bio is updated
                        home.showMyProfile(); // profile is refreshed
                    }
                }
            });

            content.add(editBio);

            JButton editInterests= UIHelper.createOutlineButton("Edit Interests", AppConstants.ACCENT); // edit interest button is created

            editInterests.setAlignmentX(LEFT_ALIGNMENT); // edit interest button is alligned to left

            editInterests.addActionListener(new ActionListener() { // when edit interest button is clicked
                public void actionPerformed(ActionEvent e) {

                    ArrayList<String> current = Database.getInterests(user.getUsername()); // current interests are taken from the database

                    InterestSelectionDialog dialog = new InterestSelectionDialog(
                        SwingUtilities.getWindowAncestor(ProfilePanel.this), current); // checkbox list including all categories pop up
                    dialog.setVisible(true); // pop up is set visible

                    if (dialog.isConfirmed()) { // if user confirms the changes

                        Database.setInterests(user.getUsername(), dialog.getSelectedInterests()); // new interests are saved to the database
                        home.showMyProfile(); // refreshes the profile page
                    }
                }
            });


            content.add(editInterests); // edit interest button is added
            content.add(Box.createVerticalStrut(12)); // 12 px spacing
        }

        ArrayList<String> interests= Database.getInterests(user.getUsername()); // gets the interests of the user from database

        if (!interests.isEmpty()) { // if list is not empty

            content.add(UIHelper.createSectionLabel("Interests")); // add label of interests

            JPanel tagRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4)); //alligned to left elements are placed side to side
            tagRow.setOpaque(false); //background is not visible
            tagRow.setAlignmentX(LEFT_ALIGNMENT); //alligned to left

            for (String i : interests) 
                tagRow.add(UIHelper.createBadgeLabel(i, new Color(230,240,255), AppConstants.ACCENT)); // for each interest a badge label is created

            content.add(tagRow);
            content.add(Box.createVerticalStrut(12)); // 12 px spacing
        }



        content.add(UIHelper.createSectionLabel("Followers")); //creates a header of followers
        content.add(Box.createVerticalStrut(4)); // 4 px spacing under the header

        if (followers.isEmpty()) { // if there is no followers 
            content.add(UIHelper.createSmallLabel("No followers yet."));

        } else {
            JPanel fRow= new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4)); //horizontally alligned

            fRow.setOpaque(false);
             fRow.setAlignmentX(LEFT_ALIGNMENT);


            for (String f : followers) { // for each follower
                User u = Database.getUserWithUsername(f); // f is comverted to user object

                if (u != null) 
                    fRow.add(UIHelper.createClickableUsername(u, home)); //username is added and it is clickable
            }


            content.add(fRow);
        }

        content.add(Box.createVerticalStrut(12)); // 12 px spacing

       
        content.add(UIHelper.createSectionLabel("Following")); // following header
        content.add(Box.createVerticalStrut(4)); // 4 px spacing under the ehader

        if (following.isEmpty()) { // if there is no following
            content.add(UIHelper.createSmallLabel("Not following anyone yet."));

        } else {

            JPanel fRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
            fRow.setOpaque(false); 
            fRow.setAlignmentX(LEFT_ALIGNMENT);

            for (String f : following) {
                User u = Database.getUserWithUsername(f);
                if (u != null) 
                    fRow.add(UIHelper.createClickableUsername(u, home));
            }

            content.add(fRow);
        }


        content.add(Box.createVerticalStrut(12)); // 12 px spacing

        
        content.add(UIHelper.createSectionLabel("Events")); // creates a events label
        content.add(Box.createVerticalStrut(4)); // 4px spacing under header

        ArrayList<Event> allEvents = Database.getAllEvents(); // all events are get from the datebase

        boolean hasEvents = false;

        for (Event ev : allEvents) { //for each. event

            if (ev.getCreatorUsername().equals(user.getUsername())) { // if event is created by the user

                hasEvents = true;

                JButton evBtn = new JButton(ev.getTitle() + "  |  " + ev.getDateStr()); // button with date and event on it

                evBtn.setFont(AppConstants.F_SMALL);
                evBtn.setHorizontalAlignment(SwingConstants.LEFT); //alligned to left
                evBtn.setBorderPainted(false); // no borders 
                evBtn.setBackground(Color.WHITE);
                evBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // it seems to be clickable
                evBtn.setAlignmentX(LEFT_ALIGNMENT);
                evBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

                evBtn.addActionListener(new ActionListener() { // when event is clicked

                    public void actionPerformed(ActionEvent e) {
                        home.showEventDetail(ev); // it goes to event detail page
                    }
                });

                content.add(evBtn);//button is added
            }
        }


        if (!hasEvents) content.add(UIHelper.createSmallLabel("No events created yet.")); // if there is no event a message plays

        JScrollPane scroll = new JScrollPane(content); // scrollpane is added in case there is too many evemts
        scroll.setBorder(null); // no borders
        scroll.getVerticalScrollBar().setUnitIncrement(16); // speed of the scrollpane

        add(scroll, BorderLayout.CENTER); //scrollpane is added to the center
    }

    private void showFollowDialog(ArrayList<String> followers, ArrayList<String> following) {


        JDialog dlg = new JDialog( // a diolog is created 
            SwingUtilities.getWindowAncestor(this), "Connections", //with title connections
            java.awt.Dialog.ModalityType.APPLICATION_MODAL); // it is modal so you cannot click to anywhere else

        dlg.setSize(380, 480);
        dlg.setLocationRelativeTo(this); // its size is adjusted


        JPanel root =new JPanel(new BorderLayout()); //main container

        root.setBackground(Color.WHITE); //background color is white
        root.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16)); // spacing between the borders

       
        
        JPanel tabRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); //elemensta are horizontally placed
        tabRow.setBackground(Color.WHITE); // background color is white

        JButton tabFollowers = UIHelper.createButton( // creating followers button
            "Followers (" + followers.size() + ")", AppConstants.ACCENT, Color.WHITE); // prints the number of followers

        JButton tabFollowing = UIHelper.createOutlineButton( //creates followings button
            "Following (" + following.size() + ")", AppConstants.TEXT_SEC); //prints the number of followingf

        tabRow.add(tabFollowers);
        tabRow.add(tabFollowing);

        

        JTextField search = UIHelper.createStyledField(); // text field to search users
        search.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32)); // width max and height 32 px

        
        JPanel top = new JPanel(); // top panel is created
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS)); // vertically alligned
        top.setBackground(Color.WHITE); // backgroun is white
        tabRow.setAlignmentX(LEFT_ALIGNMENT);
        search.setAlignmentX(LEFT_ALIGNMENT);

        top.add(tabRow);
        top.add(Box.createVerticalStrut(8)); // 8 px spacing under it
        top.add(search);

        root.add(top, BorderLayout.NORTH); // placed at the top

    
        JPanel list = new JPanel(); //list panel is cerated
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS)); // elements are placed verticallh
        list.setBackground(Color.WHITE); // white background

        JScrollPane sc = new JScrollPane(list); // scroll is added
        sc.setBorder(null); // scroll has no borders
        root.add(sc, BorderLayout.CENTER);

        final boolean[] showFollowers = { // when true shows the followers and when false shows the followings
            true
        };

        Runnable refresh = new Runnable() {

            public void run() {

                list.removeAll(); // old users are removed to be added again

                String query = search.getText().trim().toLowerCase();

                ArrayList<String> source; // if true followers and if false following
                if (showFollowers[0]){
                    source = followers;
                }else{
                    source = following;
                }


                for (String username : source) { // for each user name
                    
                    if (!query.isEmpty() && !username.toLowerCase().contains(query)) //if there is no match after search
                        continue;


                    User u = Database.getUserWithUsername(username); // username is taken from database
                    if (u == null) 
                        continue; // if there is no name skip

                    int xp = Database.getUserXP(username); // xp of the user is taken for tier

                    JPanel row = new JPanel(new BorderLayout(8, 0)); // each user has a row

                    row.setBackground(Color.WHITE); // white
                    row.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4)); //spacing
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44)); // constanst height of 44
                    row.add(UIHelper.createAvatar(u.getDisplayName(),
                        AppConstants.getTierColor(xp), 28), BorderLayout.WEST); // adds avatar to the left displays tier color


                    JPanel nameCol = new JPanel();
                    nameCol.setLayout(new BoxLayout(nameCol, BoxLayout.Y_AXIS)); // vertical
                    nameCol.setOpaque(false);


                    String displayName = u.getDisplayName();
                    String verifiedMark = "";
                    if(u.isVerified()){
                        verifiedMark = " \u2713"; // adds a tick if account is verified
                    }else{
                        verifiedMark = "";
                    }
                    String finalLabelText = displayName + verifiedMark;

                    JLabel nameLabel =new JLabel(finalLabelText);

                    nameCol.add(nameLabel);

                    JLabel sub= new JLabel("@" + username + "  " + AppConstants.getTierName(xp)); // displays the name with tier

                    sub.setFont(AppConstants.F_TINY);
                    sub.setForeground(AppConstants.getTierColor(xp));
                    nameCol.add(sub);

                    row.add(nameCol, BorderLayout.CENTER);

                    row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // seems clickable

                    row.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(java.awt.event.MouseEvent e) {
                            row.setBackground(AppConstants.PRIMARY_LIGHT); // it changes color when mouse entered
                        }
                        public void mouseExited(java.awt.event.MouseEvent e) {
                            row.setBackground(Color.WHITE); // goes to original color when mouse leaves
                        }
                        public void mouseClicked(java.awt.event.MouseEvent e) {
                            dlg.dispose(); // diolog is closed if clicked and goes to the profile of that user
                            home.navigateToProfile(u);
                        }
                    });


                    list.add(row);
                }
                list.revalidate();
                list.repaint(); // refreshes the page
            }
        };

        tabFollowers.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) { // show followers
                showFollowers[0] = true;
                refresh.run(); // refresh the list
            }
        });

        tabFollowing.addActionListener(new ActionListener() { // show followings 

            public void actionPerformed(ActionEvent e) {
                showFollowers[0] = false;
                refresh.run(); // refresh the list
            }
        });

        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() { // works when user types
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                 refresh.run(); 
                }

            public void removeUpdate(javax.swing.event.DocumentEvent e) { 
                refresh.run(); 
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) { 
                refresh.run(); 
            }
        });


        refresh.run();
        dlg.setContentPane(root);
        dlg.setVisible(true); // pop up is shown
    }
}
