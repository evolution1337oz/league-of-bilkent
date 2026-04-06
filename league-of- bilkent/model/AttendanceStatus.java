package model;

// how a user rsvps to an event
public enum AttendanceStatus {
    GOING("Going"),
    INTERESTED("Interested"),
    MAYBE("Maybe");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // converts db string back to enum
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
