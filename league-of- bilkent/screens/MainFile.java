package screens;

import model.*;
import javax.swing.*;

// main entry point
// connects to database and shows the home screen
// TODO add login screen
// TODO add network discovery
public class MainFile {

    // TODO add currentUser field when User class is ready

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        Database.createConnection();

        // for now just show home screen directly
        // will add login screen later
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                HomeScreen home = new HomeScreen();
                home.setVisible(true);
            }
        });
    }
}
