package model;

import tools.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/*
 *   Database
 * 
 *   mysql database stuff
 *   createConnection connects and createTables builds schema
 * 
 *   add:        addToDatabase (User Event Comment)
 *   delete:     deleteFromDatabase (User Event)
 * 
 *   attendance: setAttendance removeAttendance getAttendanceMap
 *   follows:    addFollow deleteFollow getFollowers getFollowing
 * 
 *   tags:       addEventTag getEventTags addInterest getInterests setInterests
 *   notif:      addNotification getNotifications
 *   messages:   sendMessage getMessages getConversationPartners
 * 
 *   queries:    getAllUsers getUserWithUsername getAllEvents
 *               getLeaderboard getUserXP addXP
 *               getPopularEventIds getRecommendedEventIds
 *               isEmailTaken isDatabaseEmpty
 * 
 *   updates:    updateUserVerified updateUserPassword updateUserBio
 *               updateEventImage
 * 
 *  used by pretty much everything
 */
public class Database {

    public static Connection databaseConnection;

    public static String customDbUrl = null;

    public static void createConnection() {
        try {
            // Load mysql driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            String dbUrl = AppConstants.DB_URL;
            String dbUser = AppConstants.DB_USER;
            String dbPass = AppConstants.DB_PASS;

            if (customDbUrl != null) {
                dbUrl = customDbUrl;
            }

            try {

                // get database credentials from properties file
                java.util.Properties creds = new java.util.Properties();
                creds.load(new java.io.FileInputStream("credentials.properties"));

                // if we are connected to an non local host dbUrl is hosts ip
                dbUrl = creds.getProperty("db.url", dbUrl);
                dbUser = creds.getProperty("db.user", dbUser);
                dbPass = creds.getProperty("db.password", dbPass);

            } catch (Exception e) {
            }

            // this connects to the actual database
            databaseConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
            createTables();
        } catch (Exception e) {
            System.out.println("could not connect to database: " + e.getMessage());
        }
    }

    private static void createTables() {
        try (Statement dbST = databaseConnection.createStatement()) {

            // user data
            // (username, display_name, email, password, salt, bio, is_club, verified, xp,
            // tier)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS users" +
                    "(username VARCHAR(50) PRIMARY KEY, " +
                    "display_name VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "password VARCHAR(255), " +
                    "salt VARCHAR(64), " +
                    "bio TEXT, " +
                    "is_club TINYINT DEFAULT 0, " +
                    "verified TINYINT DEFAULT 0, " +
                    "xp INT DEFAULT 0, " +
                    "tier INT DEFAULT 0)");

            // user interests (username, interest)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS user_interests " +
                    "(username VARCHAR(50), " +
                    "interest VARCHAR(55), " +
                    "PRIMARY KEY (username, interest))");

            // events
            // (event_id, title, description, location, date_time, end_date_time,
            // registration_deadline, capacity, creator_username, image_path, xp_reward,
            // min_tier)
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
                    "image_path VARCHAR(500) DEFAULT '', " +
                    "xp_reward INT DEFAULT 5, " +
                    "min_tier INT DEFAULT 0)");

            // event tags
            // (event_id, tag)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS event_tags " +
                    "(event_id INT, " +
                    "tag VARCHAR(50), " +
                    "PRIMARY KEY (event_id, tag))");

            // attendance
            // (event_id, username, status)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS attendance " +
                    "(event_id INT, " +
                    "username VARCHAR(50), " +
                    "status VARCHAR(20) DEFAULT 'GOING', " +
                    "PRIMARY KEY (event_id, username))");

            // comments
            // (comment_id, event_id, username, text, time, parent_comment_id)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS comments " +
                    "(comment_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "event_id INT, " +
                    "username VARCHAR(50), " +
                    "text TEXT, " +
                    "time VARCHAR(20), " +
                    "parent_comment_id INT DEFAULT 0)");

            // follows
            // (follower_username, following_username)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS follows " +
                    "(follower_username VARCHAR(50), " +
                    "following_username VARCHAR(50), " +
                    "PRIMARY KEY (follower_username, following_username))");

            // notifications
            // (notif_id, username, message)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS notifications " +
                    "(notif_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50), " +
                    "message TEXT)");

            // messages
            // (msg_id, sender, receiver, text, time, is_read)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS messages " +
                    "(msg_id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "sender VARCHAR(50), " +
                    "receiver VARCHAR(50), " +
                    "text TEXT, " +
                    "time VARCHAR(30), " +
                    "is_read TINYINT DEFAULT 0)");

            // user tag filters
            // (username, tag)
            dbST.executeUpdate("CREATE TABLE IF NOT EXISTS user_tag_filters " +
                    "(username VARCHAR(50), " +
                    "tag VARCHAR(50), " +
                    "PRIMARY KEY (username, tag))");

            // migration to new version is complete
            // i left ts if i need to remember how to add new things again
            /*
             * // migratre old data to new tables
             * migrateAttendeesTable(dbStatement);
             * addColumnIfNotExists(dbStatement, "users", "salt", "VARCHAR(64) DEFAULT ''");
             * // for password
             * addColumnIfNotExists(dbStatement, "users", "xp", "INT DEFAULT 0"); // for
             * user xp
             * addColumnIfNotExists(dbStatement, "events", "end_date_time", "VARCHAR(50)");
             * // for event end time
             * addColumnIfNotExists(dbStatement, "events", "registration_deadline",
             * "VARCHAR(50)"); // for event
             * // registration
             * // deadline
             * addColumnIfNotExists(dbStatement, "events", "image_path",
             * "VARCHAR(500) DEFAULT ''"); // for event image
             * addColumnIfNotExists(dbStatement, "events", "xp_reward", "INT DEFAULT 5"); //
             * for event xp reward
             * addColumnIfNotExists(dbStatement, "events", "min_tier", "INT DEFAULT 0"); //
             * for event min tier
             * 
             * addColumnIfNotExists(dbStatement, "comments", "parent_comment_id",
             * "INT DEFAULT 0"); // for comment parent
             */
        } catch (Exception e) {
            System.out.println("creation fail: " + e.getMessage());
        }
    }

    // helper methods for migration of new things to database
    /*
     * private static void migrateAttendeesTable(Statement dbStatement) {
     * try {
     * ResultSet rs = databaseConnection.getMetaData().getTables(null, null,
     * "attendees", null);
     * if (rs.next()) {
     * dbStatement.
     * executeUpdate("INSERT IGNORE INTO attendance (event_id, username, status) " +
     * "SELECT event_id, username, 'GOING' FROM attendees");
     * }
     * } catch (Exception e) {
     * }
     * }
     * 
     * private static void addColumnIfNotExists(Statement dbStatement, String table,
     * String col, String def) {
     * try {
     * ResultSet rs = databaseConnection.getMetaData().getColumns(null, null, table,
     * col);
     * if (!rs.next()) {
     * st.executeUpdate("ALTER TABLE " + table + " ADD COLUMN " + col + " "
     * + def);
     * }
     * } catch (Exception e) {
     * }
     * }
     */

    // ---------------------------- add----------------------------

    // user
    public static void addToDatabase(User _user) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT IGNORE INTO users (username,display_name,email,password,salt,bio,is_club,verified) VALUES(?,?,?,?,?,?,?,?)");
            ps.setString(1, _user.getUsername());
            ps.setString(2, _user.getDisplayName());
            ps.setString(3, _user.getEmail());
            ps.setString(4, _user.getPassword());
            ps.setString(5, _user.getSalt());
            ps.setString(6, _user.getBio());
            int clubVal = 0;
            if (_user.isClub()) {
                clubVal = 1;
            }
            ps.setInt(7, clubVal);
            int verifiedVal = 0;
            if (_user.isVerified()) {
                verifiedVal = 1;
            }
            ps.setInt(8, verifiedVal);
            ps.executeUpdate();
            for (String interest : _user.getInterests()) {
                addInterest(_user.getUsername(), interest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Event
    public static int addToDatabase(Event _event) {
        int generatedID = -1;
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT INTO events (title,description,location,date_time,end_date_time," +
                            "registration_deadline,capacity,creator_username,image_path,xp_reward,min_tier) VALUES(?,?,?,?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, _event.getTitle());
            ps.setString(2, _event.getDescription());
            ps.setString(3, _event.getLocation());
            ps.setString(4, _event.getDateTime().toString());
            String endDateTimeValue = null;
            if (_event.getEndDateTime() != null) {
                endDateTimeValue = _event.getEndDateTime().toString();
            }
            ps.setString(5, endDateTimeValue);
            String deadlineValue = null;
            if (_event.getRegistrationDeadline() != null) {
                deadlineValue = _event.getRegistrationDeadline().toString();
            }
            ps.setString(6, deadlineValue);
            ps.setInt(7, _event.getCapacity());
            ps.setString(8, _event.getCreatorUsername());
            ps.setString(9, _event.getImagePath());
            ps.setInt(10, _event.getXpReward());
            ps.setInt(11, _event.getMinTierIndex());
            ps.executeUpdate();
            // we need the generated id to add tags to the event
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedID = rs.getInt(1);
            }
            for (String tag : _event.getTags()) {
                addEventTag(generatedID, tag);
            }
        } catch (Exception e) {
        }
        return generatedID;
    }

    // comment
    public static int addToDatabase(Comment _comment, int _eventID) {
        int generatedID = -1;
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT INTO comments (event_id, username, text, time, parent_comment_id) VALUES(?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, _eventID);
            ps.setString(2, _comment.getUsername());
            ps.setString(3, _comment.getText());
            ps.setString(4, _comment.getTime());
            ps.setInt(5, _comment.getParentId());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                generatedID = rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return generatedID;
    }

    // ----------------------------delete----------------------------

    // user
    public static void deleteFromDatabase(User _user) {
        try {
            String uname = _user.getUsername();
            executeDelete("DELETE FROM user_interests WHERE username=?", uname);
            executeDelete("DELETE FROM follows WHERE follower_username=? OR following_username=?", uname,
                    uname);
            executeDelete("DELETE FROM notifications WHERE username=?", _user.getUsername());
            executeDelete("DELETE FROM attendance WHERE username=?", uname);
            executeDelete("DELETE FROM users WHERE username=?", _user.getUsername());
        } catch (Exception e) {
        }
    }

    // event
    public static void deleteFromDatabase(Event _event) {
        try {
            executeDelete("DELETE FROM comments WHERE event_id=?", _event.getId());
            executeDelete("DELETE FROM attendance WHERE event_id=?", _event.getId());
            executeDelete("DELETE FROM event_tags WHERE event_id=?", _event.getId());
            executeDelete("DELETE FROM events WHERE event_id=?", _event.getId());
        } catch (Exception e) {
        }
    }

    // delete helper
    private static void executeDelete(String sql, Object... args) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {

                if (args[i] instanceof Integer) {
                    ps.setInt(i + 1, (Integer) args[i]);
                } else {
                    ps.setString(i + 1, args[i].toString());
                }
            }
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    // ----------------------------attendance----------------------------

    // on duplicate key update is needed because user might change their status
    public static void setAttendance(int _eventID, String _username, AttendanceStatus _status) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT INTO attendance (event_id,username,status) VALUES(?,?,?) ON DUPLICATE KEY UPDATE status=?");
            ps.setInt(1, _eventID);
            ps.setString(2, _username);
            ps.setString(3, _status.name());
            ps.setString(4, _status.name());
            ps.executeUpdate();
        } catch (Exception e) {
        }
    }

    // remove attendance
    public static void removeAttendance(int _eventID, String _username) {
        try {
            executeDelete("DELETE FROM attendance WHERE event_id=? AND username=?", _eventID, _username);
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getAttendees(int _eventID) {
        return new ArrayList<>(getAttendanceMap(_eventID).keySet());
    }

    // get attendance data
    public static HashMap<String, AttendanceStatus> getAttendanceMap(int _eventID) {
        HashMap<String, AttendanceStatus> attendanceMap = new HashMap<>();
        try {
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT username,status FROM attendance WHERE event_id=" + _eventID);
            while (rs.next()) {
                AttendanceStatus status = AttendanceStatus.fromString(rs.getString("status"));
                if (status != null) {
                    attendanceMap.put(rs.getString("username"), status);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attendanceMap;
    }

    public static void addAttendee(int _eventID, String _username) {
        setAttendance(_eventID, _username, AttendanceStatus.GOING);
    }

    public static void deleteAttendee(int _eventID, String _username) {
        removeAttendance(_eventID, _username);
    }

    // ----------------------------follows----------------------------

    public static void deleteFollow(String _follower, String _following) {
        try {
            executeDelete("DELETE FROM follows WHERE follower_username=? AND following_username=?", _follower,
                    _following);
        } catch (Exception e) {
        }
    }

    public static void addFollow(String _follower, String _following) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("INSERT IGNORE INTO follows VALUES('" + _follower + "','" + _following + "')");
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getFollowers(String username) {
        return qList("SELECT follower_username FROM follows WHERE following_username=?", "follower_username", username);
    }

    public static ArrayList<String> getFollowing(String username) {
        return qList("SELECT following_username FROM follows WHERE follower_username=?", "following_username",
                username);
    }

    // ----------------------------tags----------------------------

    public static void addEventTag(int _eventID, String _tag) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("INSERT IGNORE INTO event_tags VALUES(" + _eventID + ",'" + _tag + "')");
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getEventTags(int eventID) {
        return qList("SELECT tag FROM event_tags WHERE event_id=?", "tag", eventID);
    }

    public static void addInterest(String _username, String _interest) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("INSERT IGNORE INTO user_interests VALUES('" + _username + "','" + _interest + "')");
        } catch (Exception e) {
        }
    }

    // v---------------------------- interesets----------------------------

    public static void removeInterest(String _username, String _interest) {
        try {
            executeDelete("DELETE FROM user_interests WHERE username=? AND interest=?", _username, _interest);
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getInterests(String username) {
        return qList("SELECT interest FROM user_interests WHERE username=?", "interest", username);
    }

    public static void setInterests(String _username, ArrayList<String> _list) {
        try {
            executeDelete("DELETE FROM user_interests WHERE username=?", _username);
            for (String interest : _list) {
                addInterest(_username, interest);
            }
        } catch (Exception e) {
        }
    }

    // ----------------------------notifications----------------------------
    public static void addNotification(String _username, String _message) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("INSERT INTO notifications (username,message) VALUES('" + _username + "','"
                            + _message + "')");
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getNotifications(String username) {
        return qList("SELECT message FROM notifications WHERE username=? ORDER BY notif_id ASC", "message", username);
    }

    // ---------------------------- messages----------------------------

    public static void sendMessage(String _sender, String _receiver, String _text) {
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "INSERT INTO messages (sender,receiver,text,time) VALUES(?,?,?,?)");
            ps.setString(1, _sender);
            ps.setString(2, _receiver);
            ps.setString(3, _text);
            ps.setString(4,
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM HH:mm")));
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static ArrayList<String[]> getMessages(String _user1, String _user2) {
        ArrayList<String[]> msgList = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "SELECT sender,text,time FROM messages WHERE (sender=? AND receiver=?) OR (sender=? AND receiver=?) ORDER BY msg_id ASC");
            ps.setString(1, _user1);
            ps.setString(2, _user2);
            ps.setString(3, _user2);
            ps.setString(4, _user1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                msgList.add(new String[] { rs.getString("sender"), rs.getString("text"), rs.getString("time") });
            }
        } catch (Exception e) {
        }
        if (msgList == null) {
            msgList = new ArrayList<>();
        }
        return msgList;
    }

    // this query gets unique partners i used CASE to get the other persons
    // username
    public static ArrayList<String> getConversationPartners(String username) {
        ArrayList<String> partnerList = new ArrayList<>();
        try {
            ResultSet rs = databaseConnection.createStatement().executeQuery(
                    "SELECT CASE WHEN sender='" + username
                            + "' THEN receiver ELSE sender END as partner FROM messages WHERE sender='" + username
                            + "' OR receiver='" + username + "' ORDER BY msg_id DESC");
            while (rs.next()) {
                String partnerUsername = rs.getString("partner");
                if (!partnerList.contains(partnerUsername)) {
                    partnerList.add(partnerUsername);
                }
            }
        } catch (Exception e) {
        }
        return partnerList;
    }

    // ----------------------------tag filters----------------------------

    public static void setUserTagFilters(String _username, ArrayList<String> _tags) {
        try {
            executeDelete("DELETE FROM user_tag_filters WHERE username=?", _username);
            for (String tag : _tags) {
                databaseConnection.createStatement()
                        .executeUpdate("INSERT IGNORE INTO user_tag_filters VALUES('" + _username + "','" + tag + "')");
            }
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> getUserTagFilters(String username) {
        return qList("SELECT tag FROM user_tag_filters WHERE username=?", "tag", username);
    }

    // ----------------------------user queries----------------------------

    public static ArrayList<User> getAllUsers() {
        ArrayList<User> allUsers = new ArrayList<>();
        try {
            ResultSet rs = databaseConnection.prepareStatement("SELECT * FROM users").executeQuery();
            while (rs.next()) {
                allUsers.add(buildUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allUsers;
    }

    public static User getUserWithUsername(String username) {
        try {
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT * FROM users WHERE username='" + username + "'");
            if (rs.next()) {
                return buildUser(rs);
            }
        } catch (Exception e) {
        }
        return null;
    }

    private static User buildUser(ResultSet rs) {
        try {
            String username = rs.getString("username");
            boolean isClub = rs.getInt(7) == 1;
            String salt = rs.getString("salt");
            if (salt == null) {
                salt = "";
            }
            User user;
            if (isClub) {
                user = new ClubUser(username, rs.getString("display_name"), rs.getString("email"),
                        rs.getString("password"), salt, rs.getString("bio"));
            } else {
                user = new User(username, rs.getString("display_name"), rs.getString("email"), rs.getString("password"),
                        salt, rs.getString("bio"));
            }
            user.setVerified(rs.getInt("verified") == 1);
            try {
                user.setXp(rs.getInt("xp"));
            } catch (Exception e) {
            }
            user.setInterests(getInterests(username));
            user.setFollowing(getFollowing(username));
            user.setFollowers(getFollowers(username));
            user.setNotifications(getNotifications(username));
            return user;
        } catch (Exception e2) {
            System.out.println(e2);
            return null;
        }
    }

    public static void updateUserVerified(String _username, boolean _isVerified) {
        try {
            int verifiedVal = 0;
            if (_isVerified) {
                verifiedVal = 1;
            }
            databaseConnection.createStatement()
                    .executeUpdate("UPDATE users SET verified=" + verifiedVal + " WHERE username='" + _username + "'");
        } catch (Exception e) {
        }
    }

    public static void updateUserPassword(String _username, String _hash, String _salt) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("UPDATE users SET password='" + _hash + "',salt='" + _salt + "' WHERE username='"
                            + _username + "'");
        } catch (Exception e) {
        }
    }

    public static void updateUserBio(String _username, String _bio) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("UPDATE users SET bio='" + _bio + "' WHERE username='" + _username + "'");
        } catch (Exception e) {
        }
    }

    // ----------------------------event Qeries ----------------------------

    public static ArrayList<Event> getAllEvents() {
        ArrayList<Event> allEvents = new ArrayList<>();
        try {
            ResultSet rs = databaseConnection.prepareStatement("SELECT * FROM events ORDER BY event_id DESC")
                    .executeQuery();
            while (rs.next()) {
                allEvents.add(buildEvent(rs));
            }
        } catch (Exception e) {
        }
        return allEvents;
    }

    // get events that a specific user created
    // tried to add date filter but it broke something
    public static ArrayList<Event> getEventsCreatedBy(String username) {
        ArrayList<Event> result = new ArrayList<Event>();
        ArrayList<Event> all = getAllEvents();
        for (int i = 0; i < all.size(); i++) {
            Event ev = all.get(i);
            if (ev.getCreatorUsername().equals(username)) {
                result.add(ev);
            }
        }
        /* tried to filter old events but getting error with the date comparison
        for (int j = result.size() - 1; j >= 0; j--) {
            String dt = result.get(j).getDateTime().toString()
            if (dt.compareTo(java.time.LocalDateTime.now().toString()) < 0) {
                result.remove(j);
            }
        }
        */
        return result;
    }

    private static Event buildEvent(ResultSet rs) {
        try {
            int eventID = rs.getInt("event_id");
            String eventCreator = rs.getString("creator_username");

            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(rs.getString("date_time"));
            java.time.LocalDateTime endDateTime = parseOptDT(rs.getString("end_date_time"));
            java.time.LocalDateTime deadline = parseOptDT(rs.getString("registration_deadline"));

            Event event = new Event(eventID, rs.getString("title"), rs.getString("description"),
                    rs.getString("location"), dateTime, endDateTime, deadline, rs.getInt("capacity"),
                    rs.getString("creator_username"));
            try {
                event.setImagePath(rs.getString("image_path"));
            } catch (Exception e) {
            }
            try {
                event.setXpReward(rs.getInt("xp_reward"));
            } catch (Exception e) {
            }
            try {
                event.setMinTierIndex(rs.getInt("min_tier"));
            } catch (Exception e) {
            }
            event.setTags(getEventTags(eventID));
            event.setAttendanceMap(getAttendanceMap(eventID));
            event.setComments(getComments(eventID));
            return event;
        } catch (Exception e2) {
            System.out.println(e2);
            return null;
        }
    }

    private static java.time.LocalDateTime parseOptDT(String dateString) {
        if (dateString == null || dateString.isEmpty() || dateString.equals("null")) {
            return null;
        }
        try {
            return java.time.LocalDateTime.parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<Comment> getComments(int eventID) {
        ArrayList<Comment> commentList = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection
                    .prepareStatement("SELECT * FROM comments WHERE event_id=? ORDER BY comment_id ASC");
            ps.setInt(1, eventID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int parentCommentID = 0;
                try {
                    parentCommentID = rs.getInt("parent_comment_id");
                } catch (Exception e) {
                }
                commentList.add(new Comment(rs.getInt("comment_id"), rs.getString("username"),
                        rs.getString("text"), rs.getString("time"), parentCommentID));
            }
        } catch (Exception e) {
        }
        return commentList;
    }

    public static ArrayList<Integer> getPopularEventIds(int limit) {
        ArrayList<Integer> eventIDs = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "SELECT event_id, COUNT(*) as cnt FROM attendance GROUP BY event_id ORDER BY cnt DESC LIMIT ?");
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
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
                    "SELECT DISTINCT e.event_id FROM events e " +
                            "JOIN event_tags et ON e.event_id=et.event_id " +
                            "JOIN user_interests ui ON et.tag=ui.interest " +
                            "WHERE ui.username=? AND e.creator_username!=? " +
                            "AND e.event_id NOT IN (SELECT event_id FROM attendance WHERE username=?) " +
                            "ORDER BY e.event_id DESC LIMIT ?");
            ps.setString(1, username);
            ps.setString(2, username);
            ps.setString(3, username);
            ps.setInt(4, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                eventIDs.add(rs.getInt("event_id"));
            }
        } catch (Exception e) {
        }
        return eventIDs;
    }

    // ---------------------------- leaderboard ----------------------------

    public static ArrayList<User> getLeaderboard(int limit) {
        ArrayList<User> userList = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "SELECT * FROM users ORDER BY xp DESC LIMIT ?");
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userList.add(buildUser(rs));
            }
        } catch (Exception e) {
        }
        return userList;
    }

    // ---------------------------- XP ----------------------------

    public static int getUserXP(String _username) {
        try {
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT xp FROM users WHERE username='" + _username + "'");
            if (rs.next()) {
                return rs.getInt("xp");
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public static void addXP(String _username, int _amount) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate(
                            "UPDATE users SET xp=GREATEST(xp+" + _amount + ",0) WHERE username='" + _username + "'");
        } catch (Exception e) {
        }
    }

    // ---------------------------- other ----------------------------

    public static boolean isEmailTaken(String _email) {
        try {
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM users WHERE email='" + _email + "'");
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void updateEventImage(int _eventID, String _path) {
        try {
            databaseConnection.createStatement()
                    .executeUpdate("UPDATE events SET image_path='" + _path + "' WHERE event_id=" + _eventID);
        } catch (Exception e) {
        }
    }

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

    private static ArrayList<String> qList(String _query, String _column, Object _obj) {
        ArrayList<String> resultList = new ArrayList<>();
        try {
            PreparedStatement ps = databaseConnection.prepareStatement(_query);
            if (_obj instanceof Integer) {
                ps.setInt(1, (Integer) _obj);
            } else {
                ps.setString(1, _obj.toString());
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultList.add(rs.getString(_column));
            }
        } catch (Exception e) {
        }
        return resultList;
    }

    // sum of all table counts, if this changes we know something changed in the db
    // its not a real hash but works good enough for our polling system
    public static int getDbStateHash() {
        if (databaseConnection == null) {
            return -1;
        }
        try {
            ResultSet rs = databaseConnection.prepareStatement(
                    "SELECT (" +
                            "(SELECT COUNT(*) FROM users) + " +
                            "(SELECT COUNT(*) FROM events) + " +
                            "(SELECT COUNT(*) FROM comments) + " +
                            "(SELECT COUNT(*) FROM attendance) + " +
                            "(SELECT COUNT(*) FROM follows) + " +
                            "(SELECT COUNT(*) FROM messages) + " +
                            "(SELECT IFNULL(SUM(is_read), 0) FROM messages)) ")
                    .executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return -1;
    }

    public static int getUnreadMessageCount(String _receiver) {
        if (databaseConnection == null) {
            return 0;
        }
        try {
            ResultSet rs = databaseConnection.createStatement()
                    .executeQuery("SELECT COUNT(*) FROM messages WHERE receiver = '" + _receiver + "' AND is_read = 0");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }

    public static int getUnreadCountFromUser(String _receiver, String _sender) {
        if (databaseConnection == null) {
            return 0;
        }
        try {
            ResultSet rs = databaseConnection.createStatement().executeQuery(
                    "SELECT COUNT(*) FROM messages WHERE receiver = '" + _receiver + "' AND sender = '" + _sender
                            + "' AND is_read = 0");
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public static void markMessagesAsRead(String _receiver, String _sender) {
        if (databaseConnection == null) {
            return;
        }
        try {
            databaseConnection.createStatement()
                    .executeUpdate("UPDATE messages SET is_read = 1 WHERE receiver = '" + _receiver + "' AND sender = '"
                            + _sender + "'");
        } catch (Exception e) {
        }
    }
    public static void updateUserDisplayName(String _username, String _newName) {
        try {
            
            PreparedStatement ps = databaseConnection.prepareStatement(
                    "UPDATE users SET display_name=? WHERE username=?");
 
            ps.setString(1, _newName);    
            ps.setString(2, _username);   
 
            ps.executeUpdate(); 
        } 
        catch (Exception e) {
            System.out.println("updateUserDisplayName error: " + e.getMessage());
        }
    }
}
