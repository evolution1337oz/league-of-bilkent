package model;

// how a user rsvps to an event - going, interested, or maybe
// displayName is the nice version for showing in the ui
public enum AttendanceStatus {
    GOING("Going"),
    INTERESTED("Interested"),
    MAYBE("Maybe");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }

    // returns the ui friendly name like "Going" instead of GOING
    public String getDisplayName() {
        return displayName;
    }

    // takes a string like "GOING" or "going" and returns the matching enum
    // returns null if s is null or doesnt match anything
    public static AttendanceStatus fromString(String s) {
        if (s == null) {
            return null;
        }

        String upper = s.toUpperCase();
        if (upper.equals("GOING")) {
            return GOING;
        } else if (upper.equals("INTERESTED")) {
            return INTERESTED;
        } else if (upper.equals("MAYBE")) {
            return MAYBE;
        } else {
            return null;
        }
    }
}
