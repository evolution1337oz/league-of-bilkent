package model;

// quick test to make sure database works
public class TestDB {
    public static void main(String[] args) {
        Database.createConnection();
        System.out.println("connected to db");
        System.out.println("db empty: " + Database.isDatabaseEmpty());

        // insert some test data
        Database.testInsertUser("testuser", "Test User", "test@bilkent.edu.tr", "1234");
        Database.testInsertUser("admin", "Admin User", "admin@bilkent.edu.tr", "admin");

        Database.testInsertEvent("CS102 Study Group", "studying for midterm",
                "Bilkent Library", "2025-04-10T14:00", 30, "admin");
        Database.testInsertEvent("Football Match", "friendly match",
                "Bilkent Stadium", "2025-04-12T16:00", 22, "testuser");

        System.out.println("\n--- all users ---");
        Database.printAllUsers();

        System.out.println("\n--- all events ---");
        Database.printAllEvents();

        System.out.println("\ndb empty: " + Database.isDatabaseEmpty());
    }
}
