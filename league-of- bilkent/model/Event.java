package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

// holds everything about an event - info, who's attending, comments, tags
// attendanceMap maps username -> their rsvp status (going/interested/maybe)
// minTierIndex is 0 if anyone can join, otherwise the minimum tier needed
public class Event implements Searchable {

    private int id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime dateTime;         // when it starts
    private LocalDateTime endDateTime;      // when it ends
    private LocalDateTime registrationDeadline;
    private int capacity;
    private String creatorUsername;
    private ArrayList<String> tags;
    private ArrayList<Comment> comments;
    private HashMap<String, AttendanceStatus> attendanceMap;
    private String imagePath;               // poster image path
    private int xpReward;                   // xp given to attendees
    private int minTierIndex;               // 0 = anyone can join

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm");

    public Event(int id, String title, String description, String location,
                 LocalDateTime dateTime, LocalDateTime endDateTime,
                 LocalDateTime registrationDeadline, int capacity, String creatorUsername) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.dateTime = dateTime;
        this.endDateTime = endDateTime;
        this.registrationDeadline = registrationDeadline;
        this.capacity = capacity;
        this.creatorUsername = creatorUsername;
        this.tags = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.attendanceMap = new HashMap<>();
        this.imagePath = "";
        this.xpReward = AppConstants.DEFAULT_EVENT_XP;
        this.minTierIndex = 0;
    }

    // shorter version, auto sets end to +2hrs and deadline to 1 day before
    public Event(int id, String title, String description, String location,
                 LocalDateTime dateTime, int capacity, String creatorUsername) {
        this(id, title, description, location, dateTime,
             dateTime.plusHours(2), dateTime.minusDays(1), capacity, creatorUsername);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public HashMap<String, AttendanceStatus> getAttendanceMap() {
        return attendanceMap;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getXpReward() {
        return xpReward;
    }

    public int getMinTierIndex() {
        return minTierIndex;
    }

    // returns formatted date strings for display
    public String getDateStr() {
        return dateTime.format(FMT);
    }

    public String getEndDateStr() {
        if (endDateTime != null) {
            return endDateTime.format(FMT);
        }
        return "";
    }

    public String getDeadlineStr() {
        if (registrationDeadline != null) {
            return registrationDeadline.format(FMT);
        }
        return "";
    }

    // returns the display name for the minimum tier, like "Silver" or "Anyone"
    public String getMinTierName() {
        if (minTierIndex <= 0) {
            return "Anyone";
        }
        return AppConstants.TIER_NAMES[Math.min(minTierIndex, AppConstants.TIER_NAMES.length - 1)];
    }

    // returns all usernames in the attendance map regardless of status
    public ArrayList<String> getAttendees() {
        return new ArrayList<>(attendanceMap.keySet());
    }

    // filters attendees by a specific status (going, interested, maybe)
    public ArrayList<String> getAttendeesByStatus(AttendanceStatus status) {
        ArrayList<String> list = new ArrayList<>();
        for (var entry : attendanceMap.entrySet()) {
            if (entry.getValue() == status) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public int getGoingCount() {
        return getAttendeesByStatus(AttendanceStatus.GOING).size();
    }

    public int getInterestedCount() {
        return getAttendeesByStatus(AttendanceStatus.INTERESTED).size();
    }

    public int getMaybeCount() {
        return getAttendeesByStatus(AttendanceStatus.MAYBE).size();
    }

    public int getAttendeeCount() {
        return attendanceMap.size();
    }

    public boolean isFull() {
        return getGoingCount() >= capacity;
    }

    public boolean isDeadlinePassed() {
        return registrationDeadline != null && LocalDateTime.now().isAfter(registrationDeadline);
    }

    public boolean isEventPast() {
        return LocalDateTime.now().isAfter(dateTime);
    }

    // checks if the users xp is enough for this events tier requirement
    public boolean canJoin(int userXP) {
        if (minTierIndex <= 0) {
            return true;
        }
        return AppConstants.getTierIndex(userXP) >= minTierIndex;
    }

    // u = username, returns null if user hasnt rsvpd
    public AttendanceStatus getAttendanceStatus(String u) {
        return attendanceMap.get(u);
    }

    public boolean isAttending(String u) {
        return attendanceMap.containsKey(u);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setRegistrationDeadline(LocalDateTime deadline) {
        this.registrationDeadline = deadline;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void setAttendanceMap(HashMap<String, AttendanceStatus> map) {
        this.attendanceMap = map;
    }

    public void setImagePath(String path) {
        this.imagePath = path;
    }

    public void setXpReward(int xp) {
        this.xpReward = xp;
    }

    public void setMinTierIndex(int idx) {
        this.minTierIndex = idx;
    }

    // adds attendees that arent already in the map, defaults them to going
    public void setAttendees(ArrayList<String> attendees) {
        for (String u : attendees) {
            if (!attendanceMap.containsKey(u)) {
                attendanceMap.put(u, AttendanceStatus.GOING);
            }
        }
    }

    // u = username, s = their new status
    public void setAttendance(String u, AttendanceStatus s) {
        attendanceMap.put(u, s);
    }

    public void removeAttendance(String u) {
        attendanceMap.remove(u);
    }

    public void addAttendee(String u) {
        setAttendance(u, AttendanceStatus.GOING);
    }

    public void removeAttendee(String u) {
        removeAttendance(u);
    }

    // wont add duplicates
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void addComment(Comment c) {
        comments.add(c);
    }

    // searches through title, description, location, creator and tags
    // query should already be lowercase
    @Override
    public boolean matchesSearch(String query) {
        if (title.toLowerCase().contains(query)) {
            return true;
        }
        if (description.toLowerCase().contains(query)) {
            return true;
        }
        if (creatorUsername.contains(query)) {
            return true;
        }
        if (location.toLowerCase().contains(query)) {
            return true;
        }
        for (String tag : tags) {
            if (tag.toLowerCase().contains(query)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getSearchSummary() {
        return title + " - " + location + " (" + getDateStr() + ")";
    }

    @Override
    public String toString() {
        return "Event{" + id + ", " + title + ", by=" + creatorUsername + "}";
    }
}
