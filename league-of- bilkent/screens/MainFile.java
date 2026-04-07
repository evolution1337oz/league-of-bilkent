package screens;

import model.*;
import tools.*;
import javax.swing.*;

public class MainFile {

    public static User currentUser;
    public static LoginScreen loginScreen;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        NetworkManager.isClientMode = true;

        if (NetworkManager.isClientMode) {
            // wait a bit for discovery to find hosts before connecting
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
        // load sample data if we are running for the first time
        if (Database.isDatabaseEmpty() && Database.customDbUrl == null) {
            SampleData.loadSampleData();
        }

        // this runs when a new host is found on the network
        NetworkManager.onHostFound = new Runnable() {
            @Override
            public void run() {
                if (Database.customDbUrl == null && !NetworkManager.discoveredHosts.isEmpty()) {
                    String hostIp = NetworkManager.discoveredHosts.get(0).ip;
                    String dbPort = "3306";
                    Database.customDbUrl = "jdbc:mysql://" + hostIp + ":" + dbPort + "/league_of_bilkent?createDatabaseIfNotExist=true";
                    Database.createConnection();
                    if (loginScreen != null) {
                        loginScreen.refreshUsers();
                    }
                }
            }
        };

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
                loginScreen.refreshUsers();
            }
        });
    }
}
