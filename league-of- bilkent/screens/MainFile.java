package screens;

import model.*;
import tools.*;
import javax.swing.*;

// main entry point
// connects to database and shows home screen
// handles network discovery for connecting to host
public class MainFile {

    // TODO add currentUser field when User class is ready
    // TODO add login screen

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        if (NetworkManager.isClientMode) {
            NetworkManager.startDiscovery();
            try {
                Thread.sleep(2500);
            } catch (Exception e) {
            }
            if (NetworkManager.discoveredHosts.size() > 0) {
                String hostIp = NetworkManager.discoveredHosts.get(0).ip;
                String dbPort = "3306";
                Database.customDbUrl = "jdbc:mysql://" + hostIp + ":" + dbPort + "/league_of_bilkent?createDatabaseIfNotExist=true";
            }
        } else {
            NetworkManager.startBroadcasting();
        }

        Database.createConnection();

        // when a new host is found update database url
        NetworkManager.onHostFound = new Runnable() {
            @Override
            public void run() {
                if (Database.customDbUrl == null && !NetworkManager.discoveredHosts.isEmpty()) {
                    String hostIp = NetworkManager.discoveredHosts.get(0).ip;
                    String dbPort = "3306";
                    Database.customDbUrl = "jdbc:mysql://" + hostIp + ":" + dbPort + "/league_of_bilkent?createDatabaseIfNotExist=true";
                    Database.createConnection();
                }
            }
        };

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                HomeScreen home = new HomeScreen();
                home.setVisible(true);
            }
        });
    }
}
