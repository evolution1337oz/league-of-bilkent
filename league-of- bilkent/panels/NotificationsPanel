package panels;
import model.*;
import screens.*;
import tools.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class NotificationsPanel extends JPanel {

    public NotificationsPanel(HomeScreen home) {


        setLayout(new BorderLayout()); 
        setBackground(Color.WHITE); // background color is set white
        setBorder(BorderFactory.createEmptyBorder(32, 48, 20, 48)); //creating a an empty field to avoid conflict

        JPanel content= new JPanel();

        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS)); // so notifications allign vertically
        content.setBackground(Color.WHITE);

        JLabel title =new JLabel("Notifications"); // header of the page
        title.setFont(new Font("SansSerif", Font.BOLD, 28)); // bold and big font
        title.setAlignmentX(LEFT_ALIGNMENT); // tittle is alligned to the left of the page

        content.add(title);
        content.add(Box.createVerticalStrut(16)); // there is a 16 px empty under the header between the first notification 

        ArrayList<String> notifs = Database.getNotifications(MainFile.currentUser.getUsername()); // notifications are taken from the database according to the username of the current user
        if (notifs.isEmpty()) {
            content.add(UIHelper.createSmallLabel("No notifications yet.")); //when arraylist is empty
        } else {
            for (int i=notifs.size() - 1; i >= 0; i--) { // it is displayed from the end because we want to show the newest notif first
                JLabel lbl= new JLabel("\u2022  " + notifs.get(i)); // u2022 is . at the beggining of each notif

                lbl.setFont(AppConstants.F_NORMAL);
                lbl.setForeground(AppConstants.TEXT_PRI);

                lbl.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0)); // cretaing empty order so there is a space between notifs
                lbl.setAlignmentX(LEFT_ALIGNMENT); //notifs are alligned to the left of the page
                content.add(lbl);
            }
        }

        JScrollPane scroll= new JScrollPane(content); //is there are many notifs we can use the scroll pane 
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16); //adjusting the speed of the scroll pane
        add(scroll, BorderLayout.CENTER); //adding scrollpane to the main panel
    }
}
