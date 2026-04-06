package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
 *   Database
 * 
 *   mysql database
 *   createConnection  createTables
 *   raw insert and query methods for testing
 * 
 *   TODO refactor with model classes when ready
 */
public class Database {

    public static Connection databaseConnection;

    public static String customDbUrl = null;

    // ----------------------------connection----------------------------

    // connects to mysql database
    public static void createConnection() {
        try {
            // Load mysql driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            String dbUrl = "jdbc:mysql://localhost:3306/league_of_bilkent?createDatabaseIfNotExist=true";
            String dbUser = "root";
            String dbPass = "1234";

            // if we are connecting to another host use their url
            if (customDbUrl != null) {
                dbUrl = customDbUrl;
            }

            // try to read credentials from file
            try {
                java.util.Properties props = new java.util.Properties();
                props.load(new java.io.FileInputStream("credentials.properties"));
                if (props.getProperty("password") != null) {
                    dbPass = props.getProperty("password");
                }
            } catch (Exception e) {
                // no credentials file, use defaults
            }

            databaseConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            createTables();
        } catch (Exception e) {
            System.out.println("could not connect to database: " + e.getMessage());
        }
    }

    // ----------------------------tables----------------------------

    private static void createTables() {
        try (Statement dbST = databaseConnection.createStatement()) {

            // users
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS users " +
                    "(username VARCHAR(50) PRIMARY KEY, " +
                    "display_name VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "password VARCHAR(255), " +
                    "salt VARCHAR(64), " +
                    "bio TEXT, " +
                    "is_club TINYINT DEFAULT 0, " +
                    "verified TINYINT DEFAULT 0, " +
                    "xp INT DEFAULT 0)");

            // events
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS events " +
                    "(event_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(200), " +
                    "description TEXT, " +
                    "location VARCHAR(200), " +
                    "date_time VARCHAR(50), " +
                    "end_date_time VARCHAR(50), " +
                    "registration_deadline VARCHAR(50), " +
                    "capacity INT, " +
                    "creator_username VARCHAR(50), " +
                    "image_path VARCHAR(500), " +
                    "xp_reward INT DEFAULT 10, " +
                    "min_tier INT DEFAULT 0)");

            // user interests (username, interest)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS user_interests " +
                    "(username VARCHAR(50), " +
                    "interest VARCHAR(55), " +
                    "PRIMARY KEY (username, interest))");

            // event tags (event_id, tag)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS event_tags " +
                    "(event_id INT, " +
                    "tag VARCHAR(50), " +
                    "PRIMARY KEY (event_id, tag))");

            // attendance (event_id, username, status)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS attendance " +
                    "(event_id INT, " +
                    "username VARCHAR(50), " +
                    "status VARCHAR(20), " +
                    "PRIMARY KEY (event_id, username))");

            // comments (comment_id, event_id, username, text, timestamp, parent_id)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS comments " +
                    "(comment_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "event_id INT, " +
                    "username VARCHAR(50), " +
                    "text TEXT, " +
                    "timestamp VARCHAR(50), " +
                    "parent_id INT DEFAULT 0)");

            // follows (follower_username, following_username)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS follows " +
                    "(follower_username VARCHAR(50), " +
                    "following_username VARCHAR(50), " +
                    "PRIMARY KEY (follower_username, following_username))");

            // notifications (id, username, message, timestamp)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS notifications " +
                    "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50), " +
                    "message TEXT, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        } catch (Exception e) {
            System.out.println("table creation error: " + e.getMessage());
        }
    }

    // ----------------------------test methods----------------------------

    // inserts a user directly for testing
    public static void testInsertUser(String username, String displayName, String email, String password) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT IGNORE INTO users (username,display_name,email,password) VALUES(?,?,?,?)");
            ps.setString(1, username);
            ps.setString(2, displayName);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // inserts an event directly for testing
    public static int testInsertEvent(String title, String desc, String location,
            String dateTime, int capacity, String creator) {
        int generatedID = -1;
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("INSERT INTO events (title,description,location,date_time,capacity,creator_username) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, desc);
            ps.setString(3, location);
            ps.setString(4, dateTime);
            ps.setInt(5, capacity);
            ps.setString(6, creator);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedID = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generatedID;
    }

    // ----------------------------interests/tags----------------------------

    public static void addUserInterest(String username, String interest) {
        try {
            databaseConnection.createStatement().executeUpdate("INSERT IGNORE INTO user_interests VALUES('" + username + "','" + interest + "')");
        } catch (Exception e) {
        }
    }

    public static void addEventTag(int eventId, String tag) {
        try {
            databaseConnection.createStatement().executeUpdate("INSERT IGNORE INTO event_tags VALUES(" + eventId + ",'" + tag + "')");
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getUserInterests(String username) {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT interest FROM user_interests WHERE username=?");
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
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT tag FROM event_tags WHERE event_id=?");
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("tag"));
            }
        } catch (Exception e) {
        }
        return list;
    }

    // ----------------------------attendance----------------------------

    public static void insertAttendance(int eventId, String username, String status) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT INTO attendance (event_id,username,status) VALUES(?,?,?) " +"ON DUPLICATE KEY UPDATE status=?");
            ps.setInt(1, eventId);
            ps.setString(2, username);
            ps.setString(3, status);
            ps.setString(4, status);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public static void removeAttendance(int eventId, String username) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("DELETE FROM attendance WHERE event_id=? AND username=?");
            ps.setInt(1, eventId);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    // returns username -> status map
    public static HashMap<String, String> getAttendance(int eventId) {
        HashMap<String, String> map = new HashMap<>();
        try {
            ResultSet rs = databaseConnection.createStatement().executeQuery("SELECT username,status FROM attendance WHERE event_id=" + eventId);
            while (rs.next()) {
                map.put(rs.getString("username"), rs.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    // ----------------------------comments----------------------------

    public static int insertComment(int eventId, String username, String text, String time) {
        int generatedID = -1;
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("INSERT INTO comments (event_id,username,text,timestamp) VALUES(?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, eventId);
            ps.setString(2, username);
            ps.setString(3, text);
            ps.setString(4, time);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedID = rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return generatedID;
    }

    // prints comments for an event
    public static void printComments(int eventId) {
        try {
            ResultSet rs = databaseConnection.createStatement().executeQuery("SELECT * FROM comments WHERE event_id=" + eventId + " ORDER BY comment_id");
            while (rs.next()) {
                System.out.println("  " + rs.getString("username") + ": " + rs.getString("text"));
            }
        } catch (Exception e) {
        }
    }

    // ----------------------------follows----------------------------

    
    public static void insertFollow(String follower, String following) {
        try {
            databaseConnection.createStatement().executeUpdate("INSERT IGNORE INTO follows VALUES('" + follower + "','" + following + "')");
        } catch (Exception e) {
        }
    }


    public static void deleteFollow(String follower, String following) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("DELETE FROM follows WHERE follower_username=? AND following_username=?");
            ps.setString(1, follower);
            ps.setString(2, following);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getFollowers(String username) {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT follower_username FROM follows WHERE following_username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("follower_username"));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public static ArrayList<String> getFollowing(String username) {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT following_username FROM follows WHERE follower_username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("following_username"));
            }
        } catch (Exception e) {
        }
        return list;
    }

    // ----------------------------notifications----------------------------

    public static void insertNotification(String username, String message) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("INSERT INTO notifications (username,message) VALUES(?,?)");
            ps.setString(1, username);
            ps.setString(2, message);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getNotifications(String username) {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT message FROM notifications WHERE username=? ORDER BY timestamp DESC");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("message"));
            }
        } catch (Exception e) {
        }
        return list;
    }

    // ----------------------------xp----------------------------

    public static int getUserXP(String username) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT xp FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("xp");
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public static void addXP(String username, int amount) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("UPDATE users SET xp = xp + " + amount + " WHERE username='" + username + "'");
        } catch (Exception e) {
        }
    }

    // ----------------------------queries----------------------------

    // prints all users from db
    public static void printAllUsers() {
        try {
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT * FROM users");
            while (rs.next()) {
                System.out.println("  " + rs.getString("username") + " - " +rs.getString("display_name") + " xp=" + rs.getInt("xp"));
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
                System.out.println("  [" + rs.getInt("event_id") + "] " +rs.getString("title") + " at " + rs.getString("location") +
                        " by " + rs.getString("creator_username"));
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

    // checks if email is already taken
    public static boolean isEmailTaken(String email) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("SELECT COUNT(*) FROM users WHERE email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
        }
        return false;
    }

    // ----------------------------delete----------------------------

    // deletes a user and their related data
    public static void deleteUser(String username) {
        try {
            databaseConnection.createStatement().executeUpdate("DELETE FROM user_interests WHERE username='" + username + "'");
            databaseConnection.createStatement().executeUpdate("DELETE FROM follows WHERE follower_username='" + username + "' OR following_username='"+ username + "'");
            databaseConnection.createStatement().executeUpdate("DELETE FROM notifications WHERE username='" + username + "'");
            databaseConnection.createStatement().executeUpdate("DELETE FROM attendance WHERE username='" + username + "'");
            databaseConnection.createStatement().executeUpdate("DELETE FROM users WHERE username='" + username + "'");
        } catch (Exception e) {
        }
    }

    // deletes an event and its related data
    public static void deleteEvent(int eventId) {
        try {
            databaseConnection.createStatement().executeUpdate("DELETE FROM event_tags WHERE event_id=" + eventId);
            databaseConnection.createStatement().executeUpdate("DELETE FROM attendance WHERE event_id=" + eventId);
            databaseConnection.createStatement().executeUpdate("DELETE FROM comments WHERE event_id=" + eventId);
            databaseConnection.createStatement().executeUpdate("DELETE FROM events WHERE event_id=" + eventId);
        } catch (Exception e) {
        }
    }

    // ----------------------------update----------------------------

    public static void updateUserBio(String username, String bio) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement("UPDATE users SET bio=? WHERE username=?");
            ps.setString(1, bio);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public static void updateUserVerified(String username, boolean verified) {
        try {
            databaseConnection.createStatement().executeUpdate("UPDATE users SET verified=" + (verified ? 1 : 0) + " WHERE username='" + username + "'");
        } catch (Exception e) {
        }
    }
}
