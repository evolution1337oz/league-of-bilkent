package panels;
import model.*;
import screens.*;
import tools.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
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
