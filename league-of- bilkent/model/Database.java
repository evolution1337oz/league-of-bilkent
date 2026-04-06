package model;

import java.sql.*;
import java.util.ArrayList;

/*
 *   Database
 * 
 *   mysql connection and queries
 *   createConnection  createTables
 *   testInsert  printAll  isDatabaseEmpty
 *   addUserInterest  addEventTag
 */
public class Database {

    public static Connection databaseConnection;

    public static String customDbUrl = null;

    // connects to mysql database
    public static void createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String dbUrl = "jdbc:mysql://localhost:3306/league_of_bilkent?createDatabaseIfNotExist=true";
            String dbUser = "root";
            String dbPass = "1234";

            // if we are connecting to another host use their url
            if (customDbUrl != null) {
                dbUrl = customDbUrl;
            }

            databaseConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            createTables();
        } catch (Exception e) {
            System.out.println("could not connect to database: " + e.getMessage());
        }
    }

    private static void createTables() {

        try (Statement st = databaseConnection.createStatement()) {

            // users table
            // (username, display_name, email, password, bio, is_club)
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users" +
                    "(username VARCHAR(50) PRIMARY KEY, " +
                    "display_name VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "password VARCHAR(255), " +
                    "bio TEXT, " +
                    "is_club TINYINT DEFAULT 0)");

            // events table
            // (event_id, title, description, location, date_time, capacity,
            // creator_username)
            st.executeUpdate("CREATE TABLE IF NOT EXISTS events " +
                    "(event_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(200), " +
                    "description TEXT, " +
                    "location VARCHAR(200), " +
                    "date_time VARCHAR(50), " +
                    "capacity INT, " +
                    "creator_username VARCHAR(50))");

            // user interests (username, interest)
            st.executeUpdate("CREATE TABLE IF NOT EXISTS user_interests " +
                    "(username VARCHAR(50), " +
                    "interest VARCHAR(55), " +
                    "PRIMARY KEY (username, interest))");

            // event tags (event_id, tag)
            st.executeUpdate("CREATE TABLE IF NOT EXISTS event_tags " +
                    "(event_id INT, " +
                    "tag VARCHAR(50), " +
                    "PRIMARY KEY (event_id, tag))");

        } catch (Exception e) {
            System.out.println("table creation fail: " + e.getMessage());
        }
    }

    // // ----------------------------test methods----------------------------

    // // inserts a user directly for testing
    // public static void testInsertUser(String username, String displayName, String
    // email, String password) {
    // try {
    // PreparedStatement ps = databaseConnection.prepareStatement(
    // "INSERT IGNORE INTO users (username,display_name,email,password)
    // VALUES(?,?,?,?)");
    // ps.setString(1, username);
    // ps.setString(2, displayName);
    // ps.setString(3, email);
    // ps.setString(4, password);
    // ps.executeUpdate();
    // System.out.println("inserted user: " + username);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // // inserts an event directly for testing
    // public static int testInsertEvent(String title, String desc, String location,
    // String dateTime, int capacity, String creator) {
    // int generatedID = -1;
    // try {
    // PreparedStatement ps = databaseConnection.prepareStatement(
    // "INSERT INTO events
    // (title,description,location,date_time,capacity,creator_username)
    // VALUES(?,?,?,?,?,?)",
    // Statement.RETURN_GENERATED_KEYS);
    // ps.setString(1, title);
    // ps.setString(2, desc);
    // ps.setString(3, location);
    // ps.setString(4, dateTime);
    // ps.setInt(5, capacity);
    // ps.setString(6, creator);
    // ps.executeUpdate();
    // ResultSet rs = ps.getGeneratedKeys();
    // if (rs.next()) {
    // generatedID = rs.getInt(1);
    // }
    // System.out.println("inserted event: " + title + " id=" + generatedID);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return generatedID;
    // }

    // ----------------------------interests/tags----------------------------

    public static void addUserInterest(String username, String interest) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("INSERT IGNORE INTO user_interests VALUES('" + username + "','" + interest + "')");
        } catch (Exception e) {
        }
    }

    public static void addEventTag(int eventId, String tag) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("INSERT IGNORE INTO event_tags VALUES(" + eventId + ",'" + tag + "')");
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getUserInterests(String username) {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "SELECT interest FROM user_interests WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("interest"));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public static ArrayList<String> getEventTags(int eventId) {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "SELECT tag FROM event_tags WHERE event_id=?");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("tag"));
            }
        } catch (Exception e) {
        }
        return list;
    }

    // ----------------------------queries----------------------------

    // prints all users from db
    public static void printAllUsers() {

        try {
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT * FROM users");
            while (rs.next()) {
                System.out.println("  " + rs.getString("username") + " - " +
                        rs.getString("display_name") + " - " + rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // prints all events from db
    public static void printAllEvents() {
        try {
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT * FROM events");
            while (rs.next()) {
                System.out.println("  [" + rs.getInt("event_id") + "] " +
                        rs.getString("title") + " at " + rs.getString("location"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // checks if no users exist
    public static boolean isDatabaseEmpty() {
        try {
            ResultSet rs = databaseConnection.prepareStatement("SELECT COUNT(*) FROM users").executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
        }
        return true;
    }

    // deletes a user by username
    public static void deleteUser(String username) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("DELETE FROM user_interests WHERE username='" + username + "'");
            databaseConnection.createStatement()
                    .executeUpdate("DELETE FROM users WHERE username='" + username + "'");
        } catch (Exception e) {
        }
    }

    // deletes an event by id
    public static void deleteEvent(int eventId) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("DELETE FROM event_tags WHERE event_id=" + eventId);
            databaseConnection.createStatement()
                    .executeUpdate("DELETE FROM events WHERE event_id=" + eventId);
        } catch (Exception e) {
        }
    }
}
