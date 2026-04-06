package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
 *   Database
 * 
 *   mysql database
 *   createConnection  createTables
 * 
 *   test:       testInsertUser testInsertEvent printAllUsers printAllEvents
 *   interests:  addUserInterest getUserInterests addEventTag getEventTags
 *   attendance: insertAttendance removeAttendance getAttendance
 *   comments:   insertComment printComments
 *   follows:    insertFollow deleteFollow getFollowers getFollowing
 *   notif:      insertNotification getNotifications
 *   messages:   sendMessage getMessages getConversationPartners
 *   xp:         getUserXP addXP
 *   queries:    isDatabaseEmpty isEmailTaken getLeaderboard
 *               getPopularEventIds getRecommendedEventIds getDbStateHash
 *   updates:    updateUserBio updateUserVerified updateUserPassword
 *               updateEventImage
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

            // messages (id, sender, receiver, text, timestamp, read_at)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS messages " +
                    "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "sender_username VARCHAR(50), " +
                    "receiver_username VARCHAR(50), " +
                    "text TEXT, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "read_at TIMESTAMP NULL)");

        } catch (Exception e) {
            System.out.println("table creation error: " + e.getMessage());
        }
    }

    // helper for running a single column query and returning list
    private static ArrayList<String> qList(String sql, String column, String... params) {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString(column));
            }
        } catch (Exception e) {
        }
        return list;
    }

    // helper for running delete with prepared statement
    private static void executeDelete(String sql, String... params) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            ps.executeUpdate();
        } catch (Exception e) {
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
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT INTO events (title,description,location,date_time,capacity,creator_username) VALUES(?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
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
        return qList("SELECT interest FROM user_interests WHERE username=?", "interest", username);
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

    // clears and re-inserts interests for a user
    public static void setInterests(String username, ArrayList<String> interests) {
        try {
            executeDelete("DELETE FROM user_interests WHERE username=?", username);
            for (String interest : interests) {
                addUserInterest(username, interest);
            }
        } catch (Exception e) {
        }
    }

    // ----------------------------attendance----------------------------

    public static void insertAttendance(int eventId, String username, String status) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT INTO attendance (event_id,username,status) VALUES(?,?,?) " +
                            "ON DUPLICATE KEY UPDATE status=?");
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
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "DELETE FROM attendance WHERE event_id=? AND username=?");
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
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT username,status FROM attendance WHERE event_id=" + eventId);
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
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT INTO comments (event_id,username,text,timestamp) VALUES(?,?,?,?)",
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
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT * FROM comments WHERE event_id=" + eventId + " ORDER BY comment_id");
            while (rs.next()) {
                System.out.println("  " + rs.getString("username") + ": " + rs.getString("text"));
            }
        } catch (Exception e) {
        }
    }

    // ----------------------------follows----------------------------

    public static void deleteFollow(String follower, String following) {
        try {
            executeDelete("DELETE FROM follows WHERE follower_username=? AND following_username=?", follower,
                    following);
        } catch (Exception e) {
        }
    }

    public static void insertFollow(String follower, String following) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate(
                            "INSERT IGNORE INTO follows VALUES('" + follower + "','" + following + "')");
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getFollowers(String username) {
        return qList("SELECT follower_username FROM follows WHERE following_username=?", "follower_username", username);
    }

    public static ArrayList<String> getFollowing(String username) {
        return qList("SELECT following_username FROM follows WHERE follower_username=?", "following_username", username);
    }

    // ----------------------------notifications----------------------------

    public static void insertNotification(String username, String message) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT INTO notifications (username,message) VALUES(?,?)");
            ps.setString(1, username);
            ps.setString(2, message);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getNotifications(String username) {
        return qList("SELECT message FROM notifications WHERE username=? ORDER BY timestamp DESC", "message", username);
    }

    // ----------------------------messages----------------------------

    // sends a message from sender to receiver
    public static void sendMessage(String sender, String receiver, String text) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT INTO messages (sender_username,receiver_username,text) VALUES(?,?,?)");
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, text);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    // gets messages between two users
    public static ArrayList<String> getMessages(String user1, String user2) {
        ArrayList<String> list = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "SELECT sender_username,text,timestamp FROM messages " +
                            "WHERE (sender_username=? AND receiver_username=?) " +
                            "OR (sender_username=? AND receiver_username=?) " +
                            "ORDER BY timestamp ASC");
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("sender_username") + ": " + rs.getString("text"));
            }
        } catch (Exception e) {
        }
        return list;
    }

    // gets list of users this user has messaged with
    public static ArrayList<String> getConversationPartners(String username) {
        ArrayList<String> partners = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "SELECT DISTINCT CASE WHEN sender_username=? THEN receiver_username ELSE sender_username END AS partner " +
                            "FROM messages WHERE sender_username=? OR receiver_username=? ORDER BY partner");
            ps.setString(1, username);
            ps.setString(2, username);
            ps.setString(3, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                partners.add(rs.getString("partner"));
            }
        } catch (Exception e) {
        }
        return partners;
    }

    // counts unread messages for a user
    public static int getUnreadMessageCount(String username) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "SELECT COUNT(*) FROM messages WHERE receiver_username=? AND read_at IS NULL");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }

    // marks messages from a specific sender as read
    public static void markMessagesAsRead(String receiver, String sender) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "UPDATE messages SET read_at=CURRENT_TIMESTAMP " +
                            "WHERE receiver_username=? AND sender_username=? AND read_at IS NULL");
            ps.setString(1, receiver);
            ps.setString(2, sender);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    // ----------------------------xp----------------------------

    public static int getUserXP(String username) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "SELECT xp FROM users WHERE username=?");
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
                System.out.println("  " + rs.getString("username") + " - " +
                        rs.getString("display_name") + " xp=" + rs.getInt("xp"));
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
                        rs.getString("title") + " at " + rs.getString("location") +
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
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "SELECT COUNT(*) FROM users WHERE email=?");
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
        }
        return false;
    }

    // gets top users by xp for leaderboard
    public static ArrayList<String> getLeaderboard(int limit) {
        ArrayList<String> list = new ArrayList<>();
        try {
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT username,display_name,xp FROM users ORDER BY xp DESC LIMIT " + limit);
            while (rs.next()) {
                list.add(rs.getString("username") + " - " + rs.getString("display_name") + " (" + rs.getInt("xp") + " xp)");
            }
        } catch (Exception e) {
        }
        return list;
    }

    // gets most attended event ids
    public static ArrayList<Integer> getPopularEventIds(int limit) {
        ArrayList<Integer> eventIDs = new ArrayList<>();
        try {
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT event_id, COUNT(*) as cnt FROM attendance GROUP BY event_id ORDER BY cnt DESC LIMIT " + limit);
            while (rs.next()) {
                eventIDs.add(rs.getInt("event_id"));
            }
        } catch (Exception e) {
        }
        return eventIDs;
    }

    // Matches user interests with event tags to find matching events
    public static ArrayList<Integer> getRecommendedEventIds(String username, int limit) {
        ArrayList<Integer> eventIDs = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "SELECT DISTINCT et.event_id FROM event_tags et " +
                            "INNER JOIN user_interests ui ON et.tag = ui.interest " +
                            "WHERE ui.username=? " +
                            "AND et.event_id NOT IN (SELECT event_id FROM attendance WHERE username=?) " +
                            "LIMIT ?");
            ps.setString(1, username);
            ps.setString(2, username);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                eventIDs.add(rs.getInt("event_id"));
            }
        } catch (Exception e) {
        }
        return eventIDs;
    }

    // computes a hash of database state for polling
    public static int getDbStateHash() {
        try {
            int hash = 0;
            ResultSet rs = databaseConnection.createStatement().executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) hash += rs.getInt(1) * 31;
            rs = databaseConnection.createStatement().executeQuery("SELECT COUNT(*) FROM events");
            if (rs.next()) hash += rs.getInt(1) * 37;
            rs = databaseConnection.createStatement().executeQuery("SELECT COUNT(*) FROM follows");
            if (rs.next()) hash += rs.getInt(1) * 41;
            rs = databaseConnection.createStatement().executeQuery("SELECT COUNT(*) FROM attendance");
            if (rs.next()) hash += rs.getInt(1) * 43;
            rs = databaseConnection.createStatement().executeQuery("SELECT COUNT(*) FROM comments");
            if (rs.next()) hash += rs.getInt(1) * 47;
            rs = databaseConnection.createStatement().executeQuery("SELECT COUNT(*) FROM messages");
            if (rs.next()) hash += rs.getInt(1) * 53;
            return hash;
        } catch (Exception e) {
        }
        return -1;
    }

    // ----------------------------delete----------------------------

    // deletes a user and their related data
    public static void deleteUser(String username) {
        try {
            String uname = username;
            executeDelete("DELETE FROM user_interests WHERE username=?", uname);
            executeDelete("DELETE FROM follows WHERE follower_username=? OR following_username=?", uname, uname);
            executeDelete("DELETE FROM notifications WHERE username=?", username);
            executeDelete("DELETE FROM attendance WHERE username=?", uname);
            executeDelete("DELETE FROM users WHERE username=?", username);
        } catch (Exception e) {
        }
    }

    // deletes an event and its related data
    public static void deleteEvent(int eventId) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("DELETE FROM event_tags WHERE event_id=" + eventId);
            databaseConnection.createStatement()
                    .executeUpdate("DELETE FROM attendance WHERE event_id=" + eventId);
            databaseConnection.createStatement()
                    .executeUpdate("DELETE FROM comments WHERE event_id=" + eventId);
            databaseConnection.createStatement()
                    .executeUpdate("DELETE FROM events WHERE event_id=" + eventId);
        } catch (Exception e) {
        }
    }

    // ----------------------------update----------------------------

    public static void updateUserBio(String username, String bio) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "UPDATE users SET bio=? WHERE username=?");
            ps.setString(1, bio);
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public static void updateUserVerified(String username, boolean verified) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("UPDATE users SET verified=" + (verified ? 1 : 0) +
                            " WHERE username='" + username + "'");
        } catch (Exception e) {
        }
    }

    public static void updateUserPassword(String username, String hashedPassword, String salt) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "UPDATE users SET password=?, salt=? WHERE username=?");
            ps.setString(1, hashedPassword);
            ps.setString(2, salt);
            ps.setString(3, username);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    public static void updateEventImage(int eventId, String imagePath) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "UPDATE events SET image_path=? WHERE event_id=?");
            ps.setString(1, imagePath);
            ps.setInt(2, eventId);
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }
}
