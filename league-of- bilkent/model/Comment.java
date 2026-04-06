package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// a comment on an event
// parentId = 0 means top-level, > 0 means its a reply to that comment id
public class Comment {

    private int id;
    private String username;    // who wrote it
    private String text;        // the actual comment
    private String time;        // formatted timestamp like "06/04 14:30"
    private int parentId;       // 0 = top-level, > 0 = reply

    // auto generates the timestamp
    public Comment(String username, String text) {
        this.id = 0;
        this.username = username;
        this.text = text;
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"));
        this.parentId = 0;
    }

    // when you already have the time string
    public Comment(String username, String text, String time) {
        this.id = 0;
        this.username = username;
        this.text = text;
        this.time = time;
        this.parentId = 0;
    }

    // full constructor, used when loading from db
    public Comment(int id, String username, String text, String time, int parentId) {
        this.id = id;
        this.username = username;
        this.text = text;
        this.time = time;
        this.parentId = parentId;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public int getParentId() {
        return parentId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public boolean isReply() {
        return parentId > 0;
    }

    @Override
    public String toString() {
        return "Comment{" + id + ", " + username + ": " + text + (isReply() ? " (reply to " + parentId + ")" : "") + "}";
    }
}
