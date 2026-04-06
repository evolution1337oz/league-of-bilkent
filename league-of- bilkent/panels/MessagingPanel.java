package panels;
import model.*;
import screens.*;
import tools.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.nio.channels.Pipe;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class MessagingPanel extends JPanel {


    private HomeScreen home;
    private JPanel chatPanel;
    private JTextField msgField;
    private String selectedUser = null;
    private JPanel convList;

    public MessagingPanel(HomeScreen home) {

        this.home = home;
        setLayout(new BorderLayout()); // divides the panel into north-south-east-west-center
        setBackground(Color.WHITE); // background color is white 
        buildUI(); //constructor calls the buildUI method
    }

    public String getSelectedUser() {
        return selectedUser; //method returns the selected user
    }

    public void setSelectedUser(String user) { 

        this.selectedUser = user;
        if(user!=null) {
            refreshConversations(); // if a user is chosen the chat is refreshed
            loadChat();
        }
    }

    private void buildUI() {

        JPanel left = new JPanel();

        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS)); // elements are vertically alligned
        left.setBackground(new Color(0xFB, 0xFB, 0xFA)); //it is a light grey color
        left.setPreferredSize(new Dimension(200, 0)); // width is 200px
        left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, AppConstants.BORDER)); // a border is set to the right of the panel

        JLabel title = new JLabel("  Messages"); //header 
        title.setFont(AppConstants.F_TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(16, 8, 8, 8)); // creates an empty space on the top and bottom of the messsages text

        left.add(title);

        JButton btnNew = new JButton("+ New Message"); //new message button under the messages header is added
        btnNew.setFont(AppConstants.F_SMALL);
        btnNew.setBorderPainted(false); // removes the borders of the button

        btnNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                newConversation(); // button calls the newConversation method when clicked
            }
        });

        btnNew.setAlignmentX(LEFT_ALIGNMENT); // button alligned to left
        btnNew.setMaximumSize(new Dimension(190, 28)); // button is maximum sized 190 px width and 28pc height

        left.add(btnNew);
        left.add(Box.createVerticalStrut(8)); // creates an empty place between the button and content below

        convList = new JPanel();
        convList.setLayout(new BoxLayout(convList, BoxLayout.Y_AXIS)); // elements are vertically alligned
        convList.setBackground(new Color(0xFB, 0xFB, 0xFA)); //light grey colored
        refreshConversations(); // refreshConv method is called to fill the convList 

        JScrollPane convScroll = new JScrollPane(convList); //scroll pane is added in case there is to much user 
        convScroll.setBorder(null); //scroll pane has no borders 
        left.add(convScroll);//scroll pane is added to the panel left

        add(left, BorderLayout.WEST); //panel left is placed to the west(left) of the layout

        JPanel right = new JPanel(new BorderLayout()); //new panel to be placed to the east of the layout
        right.setBackground(Color.WHITE); //background color is white 
        right.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16)); //creates an 16px empty place around the panel
        chatPanel=new JPanel(); //chat panel is created 
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS)); //elemensts are alligned vertically 
        chatPanel.setBackground(Color.WHITE); // background color is white 


        JScrollPane chatScroll=new JScrollPane(chatPanel); //scrollpane is added in case there is too much messsages
        chatScroll.setBorder(null); //scrollpane has no borders 
        chatScroll.getVerticalScrollBar().setUnitIncrement(16); //adjusting the speeed of the scrollpane to a 16px per step
        right.add(chatScroll, BorderLayout.CENTER); //scrollpane is added to the center of the right panel

        JPanel inputRow= new JPanel(new BorderLayout(4, 0)); // there is a 4px empty space between elements that stands next to each other
        inputRow.setBackground(Color.WHITE); // background color is set white
        inputRow.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        msgField =new JTextField(); // creating the messeage textfield
        msgField.setFont(AppConstants.F_NORMAL);
        msgField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppConstants.BORDER), //creates a thin border around textfield 
            BorderFactory.createEmptyBorder(6, 8, 6, 8))); //this creates a space between the text and the border

        JButton btnSend = UIHelper.createButton("Send", AppConstants.ACCENT, Color.WHITE); //using uihelper method to create the button

        btnSend.addActionListener(new ActionListener(){ //button calls sendMessage metot when clicked
            public void actionPerformed(ActionEvent e){
                sendMessage();
            }
        });

        msgField.addActionListener(new ActionListener() { //it calls the senMessage metot when enter button is clicked
            public void actionPerformed(ActionEvent e){
                sendMessage();
            }
        });


        inputRow.add(msgField, BorderLayout.CENTER);//msgField is placed to the cenetr
        inputRow.add(btnSend, BorderLayout.EAST);// button is placed to the right
        right.add(inputRow, BorderLayout.SOUTH); //inputRow panel is placed to the bottom of the right panel

        add(right, BorderLayout.CENTER); //right panel is placed at the center of the main panel

        JLabel hint = new JLabel("Select a conversation or start a new one.", JLabel.CENTER); //this text is shown before a user is chosen
        hint.setForeground(AppConstants.TEXT_LIGHT); //text is lighter
        chatPanel.add(hint); // hint is added to the chat panel
    }

    public void refreshConversations() {

        convList.removeAll(); // to prevent duplication all elements are removed first

        ArrayList<String> partners = Database.getConversationPartners(MainFile.currentUser.getUsername()); //we get the people the current user talked with

        for (String p: partners) {

            int unread =Database.getUnreadCountFromUser(MainFile.currentUser.getUsername(), p); //we take the number of unread mesages

            JButton btn= new JButton("@" + p); //names of the contacts are seen on the screen

            if (unread > 0) { // if there is an unread message
                String badgeText;
                if (unread>9){ // if the number of unread text is higher than 9 it is seen on the screen as 9+
                    badgeText = "9+";
                } else {
                    badgeText = String.valueOf(unread); //if number of unread text is less than 9 than the number on the screen is the no of unread messages
                }

                btn.setIcon(new BadgeIcon(badgeText));
                btn.setHorizontalTextPosition(SwingConstants.LEFT); //the name is on the left and the icon is on the right
                btn.setIconTextGap(10);
            }

            btn.setFont(AppConstants.F_NORMAL);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorderPainted(false); //button has no borders 

            Color bg;

            if (p.equals (selectedUser)){ // the selected user is highligthed by the usage of different colors
                bg = AppConstants.PRIMARY_LIGHT;
            }else {
                bg = new Color(0xFB, 0xFB, 0xFA);
            }

            btn.setBackground(bg);
            btn.setMaximumSize(new Dimension(190, 32));

            btn.addActionListener(new ActionListener() { //it loads the chat and refresh the conversation when the button is clicked
                public void actionPerformed (ActionEvent e){
                    selectedUser = p;
                    Database.markMessagesAsRead(MainFile.currentUser.getUsername(), p);
                    loadChat();
                    refreshConversations();
                }
            });

            convList.add(btn);
        }
        convList.revalidate(); //it regulates the layout
        convList.repaint(); // it is drawn on the screen again
    }

    public void loadChat() {

        chatPanel.removeAll(); //old text is deleted to prevent complication 
        
        if (selectedUser == null) return; // if no one is selected method stops

        Database.markMessagesAsRead(MainFile.currentUser.getUsername(), selectedUser); // messages of the sleecteduser is marked as read

        JLabel header = new JLabel("Chat with @" + selectedUser); // the selected user name is seen at the top as @eylül
        header.setFont(AppConstants.F_SECTION); 
        header.setAlignmentX(LEFT_ALIGNMENT); //name is left alligned


        chatPanel.add(header); //name is added to the panel 
        chatPanel.add(Box.createVerticalStrut(8)); // 8px space under the name

        ArrayList<String[]> msgs =Database.getMessages(MainFile.currentUser.getUsername(), selectedUser); //the messages came from the selected user is held in this arraylist 

        for (String[] m: msgs) {

            boolean isMe = m[0].equals(MainFile.currentUser.getUsername()); // turn true if the message by sended by the cureentuser or the chosenuser


            JPanel bubble = new JPanel();
            bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS)); //for each message a message bubble is created
            // the text and the time is alligned vertically

            if(isMe){
                bubble.setBackground(new Color(0xE8, 0xF0, 0xFE));
                bubble.setAlignmentX(RIGHT_ALIGNMENT); //if message is sended by me it is right alligned and has different color
            }else{
                bubble.setBackground(new Color(0xF5, 0xF5, 0xF3)); // if message is sent by someone else the message is left alligned
                bubble.setAlignmentX(LEFT_ALIGNMENT);
            }

            bubble.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10)); //creates space between the text and the border
            bubble.setMaximumSize(new Dimension(400, 100)); //prevents the bubble to be too big

            JLabel text = new JLabel("<html>" + m[1] + "</html>"); //the message is taken 
            text.setFont(AppConstants.F_NORMAL);
            bubble.add(text); //the message is added to the bubble


            JLabel time = new JLabel(m[2]); //the time message sent is taken
            time.setFont(AppConstants.F_TINY);
            time.setForeground(AppConstants.TEXT_SEC);
            bubble.add(time); //time is added to the bubble

            FlowLayout layout;
            if(isMe){
                layout = new FlowLayout(FlowLayout.RIGHT); // if message is sent by me right allignment
            }else {
                layout = new FlowLayout(FlowLayout.LEFT); //vice verse
            }

            JPanel row = new JPanel(layout);

            row.setBackground(Color.WHITE); //background color white
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            row.add(bubble);
            chatPanel.add(row);
        }

        chatPanel.revalidate(); //adjusts the layout
        chatPanel.repaint(); // draws the screan again 
    }

    private void sendMessage() {
        if (selectedUser == null || msgField.getText().trim().isEmpty()) return; // if no one is chosen or the message is empty metot stops
        Database.sendMessage(MainFile.currentUser.getUsername(), selectedUser, msgField.getText().trim()); // the message is send to the database

        msgField.setText(""); // message field get empty after the message is sent
        loadChat(); // loadchat method is called so the message can be seen on the screen
    }

    private void newConversation() {

        String user = JOptionPane.showInputDialog(this, "Enter username to message:", "New Message", JOptionPane.PLAIN_MESSAGE); //if it is the first time the program asks the user to enter the name of the person you are sending message to

        if (user != null && !user.trim().isEmpty()) { //if user name is not empty

            User target = Database.getUserWithUsername(user.trim()); // the username is searched in the database
            if (target == null) { 
                UIHelper.showError(this, "User not found!"); 
                return; } // if the name of the user didnot found metot stops

            selectedUser = user.trim(); //so the chat of that person pops up

            loadChat();
            refreshConversations();
        }
    }

    private static class BadgeIcon implements Icon {

        private String text;

        public BadgeIcon(String text) { //constructor for BadgeIcon
            this.text = text; 
        
        }

        public int getIconWidth() { //the width of the icon is 24px
            return 24; 
        }
        
        public int getIconHeight() {  // the height of the icon is 24 px
            return 24; 
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //so borders are smoother

            g2.setColor(AppConstants.DANGER); // draws a red circle
            g2.fillOval(x, y + 2, 20, 20); // moves 2 px down
            g2.setColor(Color.WHITE); //text is white
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));

            FontMetrics fm = g2.getFontMetrics(); //measures the width of the text 
            int w = fm.stringWidth(text);
            g2.drawString(text, x + 10 - w / 2, y + 15);
        }
    }
}
