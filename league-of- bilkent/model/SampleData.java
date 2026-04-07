package model;

import tools.*;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class SampleData {

    // Generate unique salts for password hashing
    public static void loadSampleData() {
        String s1 = PasswordUtil.generateSalt();
        String s2 = PasswordUtil.generateSalt();
        String s3 = PasswordUtil.generateSalt();
        String s4 = PasswordUtil.generateSalt();
        String s5 = PasswordUtil.generateSalt();
        String s6 = PasswordUtil.generateSalt();
        String s7 = PasswordUtil.generateSalt();

        // Create 5 student users
        User damla = new User("damla", "Damla", "damla@ug.bilkent.edu.tr",
            PasswordUtil.hashPassword("1234", s1), s1, "CS Student | Event Organizer");
        damla.setVerified(true); damla.setXp(120);
        damla.getInterests().add("software"); damla.getInterests().add("ai");
        damla.getInterests().add("music"); damla.getInterests().add("philosophy");

        User eylul = new User("eylul", "Eylül", "eylul@ug.bilkent.edu.tr",
            PasswordUtil.hashPassword("1234", s2), s2, "Art & Photography Lover");
        eylul.setVerified(true); eylul.setXp(80);
        eylul.getInterests().add("art"); eylul.getInterests().add("photography");
        eylul.getInterests().add("cinema");

        User emir = new User("emir_selim", "Emir Selim", "emirselim@ug.bilkent.edu.tr",
            PasswordUtil.hashPassword("1234", s3), s3, "Runner & Sports Enthusiast");
        emir.setVerified(true); emir.setXp(95);
        emir.getInterests().add("sports"); emir.getInterests().add("fitness");
        emir.getInterests().add("basketball");

        User ege = new User("ege", "Ege", "ege@ug.bilkent.edu.tr",
            PasswordUtil.hashPassword("1234", s4), s4, "Music Producer & GameDev");
        ege.setVerified(true); ege.setXp(60);
        ege.getInterests().add("music"); ege.getInterests().add("gamedev");
        ege.getInterests().add("software");

        User bosman = new User("bosman", "Bosman", "bosman@ug.bilkent.edu.tr",
            PasswordUtil.hashPassword("1234", s5), s5, "Entrepreneur & Finance Geek");
        bosman.setVerified(true); bosman.setXp(45);
        bosman.getInterests().add("entrepreneurship"); bosman.getInterests().add("finance");
        bosman.getInterests().add("marketing");

        // Create 2 club accounts
        ClubUser ieee = new ClubUser("ieee_bilkent", "IEEE Bilkent", "ieee@bilkent.edu.tr",
            PasswordUtil.hashPassword("1234", s6), s6, "IEEE Bilkent Student Branch");
        ieee.setVerified(true); ieee.setXp(200);
        ieee.getInterests().add("software"); ieee.getInterests().add("ai");

        ClubUser music = new ClubUser("music_club", "Music Club", "music@bilkent.edu.tr",
            PasswordUtil.hashPassword("1234", s7), s7, "Bilkent Music & Performance Club");
        music.setVerified(true); music.setXp(150);
        music.getInterests().add("music");

        // Add all users to database
        Database.addToDatabase(damla);
        Database.addToDatabase(eylul);
        Database.addToDatabase(emir);
        Database.addToDatabase(ege);
        Database.addToDatabase(bosman);
        Database.addToDatabase(ieee);
        Database.addToDatabase(music);

        // Initialize XP for each user
        Database.addXP("damla", 120);
        Database.addXP("eylul", 80);
        Database.addXP("emir_selim", 95);
        Database.addXP("ege", 60);
        Database.addXP("bosman", 45);
        Database.addXP("ieee_bilkent", 200);
        Database.addXP("music_club", 150);

        // Create follow relationships between users
        Database.addFollow("damla", "ieee_bilkent");
        Database.addFollow("damla", "music_club");
        Database.addFollow("eylul", "damla");
        Database.addFollow("eylul", "music_club");
        Database.addFollow("emir_selim", "damla");
        Database.addFollow("emir_selim", "ege");
        Database.addFollow("ege", "ieee_bilkent");
        Database.addFollow("ege", "damla");
        Database.addFollow("bosman", "ieee_bilkent");
        Database.addFollow("bosman", "emir_selim");

        // Create 18 sample events (dates are relative to current time)
        LocalDateTime now = LocalDateTime.now();

        Event e1 = new Event(0, "GameDev Summit", "Learn about game development with Unity and Unreal Engine.",
            "EA-409", now.plusDays(18), now.plusDays(18).plusHours(3), now.plusDays(17), 80, "ieee_bilkent");
        e1.addTag("gamedev"); e1.addTag("software"); e1.setXpReward(15); e1.setMinTierIndex(1);
        int id1 = Database.addToDatabase(e1);
        String p1 = PosterGenerator.generateDefault(e1); if(p1!=null) Database.updateEventImage(id1,p1);

        Event e2 = new Event(0, "Algorithm Contest", "Competitive programming challenge for all levels.",
            "B-Lab", now.plusDays(24), now.plusDays(24).plusHours(4), now.plusDays(23), 40, "ieee_bilkent");
        e2.addTag("algorithms"); e2.addTag("software"); e2.setXpReward(10);
        int id2 = Database.addToDatabase(e2);
        String p2 = PosterGenerator.generateDefault(e2); if(p2!=null) Database.updateEventImage(id2,p2);

        Event e3 = new Event(0, "Acoustic Night", "Student musicians perform live at Odeon.",
            "Odeon", now.plusDays(30), now.plusDays(30).plusHours(3), now.plusDays(29), 100, "music_club");
        e3.addTag("music"); e3.addTag("concert"); e3.setXpReward(5);
        int id3 = Database.addToDatabase(e3);
        String p3 = PosterGenerator.generateDefault(e3); if(p3!=null) Database.updateEventImage(id3,p3);

        Event e4 = new Event(0, "Python Workshop", "Beginner-friendly Python programming workshop.",
            "EA-Z03", now.plusDays(5), now.plusDays(5).plusHours(2), now.plusDays(4), 50, "damla");
        e4.addTag("python"); e4.addTag("education"); e4.setXpReward(10);
        int id4 = Database.addToDatabase(e4);
        String p4 = PosterGenerator.generateDefault(e4); if(p4!=null) Database.updateEventImage(id4,p4);

        Event e5 = new Event(0, "Campus Run", "5km campus run - all fitness levels welcome!",
            "Main Gate", now.plusDays(7), now.plusDays(7).plusHours(2), now.plusDays(6), 200, "emir_selim");
        e5.addTag("sports"); e5.addTag("fitness"); e5.setXpReward(5);
        int id5 = Database.addToDatabase(e5);
        String p5 = PosterGenerator.generateDefault(e5); if(p5!=null) Database.updateEventImage(id5,p5);

        Event e6 = new Event(0, "Art Exhibition", "Student artwork showcase at the library.",
            "Library Foyer", now.plusDays(40), now.plusDays(41), now.plusDays(39), 150, "eylul");
        e6.addTag("art"); e6.addTag("exhibition"); e6.setXpReward(5);
        int id6 = Database.addToDatabase(e6);
        String p6 = PosterGenerator.generateDefault(e6); if(p6!=null) Database.updateEventImage(id6,p6);

        Event e7 = new Event(0, "Startup Pitch Night", "Present your startup idea and get feedback from mentors.",
            "CYBERPARK Auditorium", now.plusDays(3), now.plusDays(3).plusHours(3), now.plusDays(2), 60, "bosman");
        e7.addTag("entrepreneurship"); e7.addTag("finance"); e7.setXpReward(12);
        int id7 = Database.addToDatabase(e7);
        String p7 = PosterGenerator.generateDefault(e7); if(p7!=null) Database.updateEventImage(id7,p7);

        Event e8 = new Event(0, "Photography Walk", "Golden hour campus photography walk. Bring your camera!",
            "Bilkent Lake", now.plusDays(2), now.plusDays(2).plusHours(2), now.plusDays(1), 30, "eylul");
        e8.addTag("photography"); e8.addTag("art"); e8.setXpReward(5);
        int id8 = Database.addToDatabase(e8);
        String p8 = PosterGenerator.generateDefault(e8); if(p8!=null) Database.updateEventImage(id8,p8);

        Event e9 = new Event(0, "Basketball Tournament", "3v3 basketball tournament. Form your team!",
            "Sports Center", now.plusDays(10), now.plusDays(10).plusHours(5), now.plusDays(9), 48, "emir_selim");
        e9.addTag("basketball"); e9.addTag("sports"); e9.setXpReward(8);
        int id9 = Database.addToDatabase(e9);
        String p9 = PosterGenerator.generateDefault(e9); if(p9!=null) Database.updateEventImage(id9,p9);

        Event e10 = new Event(0, "AI Reading Group", "Weekly paper discussion - this week: transformers.",
            "EA-325", now.plusDays(1), now.plusDays(1).plusHours(2), now.plusDays(0), 25, "damla");
        e10.addTag("ai"); e10.addTag("software"); e10.setXpReward(7);
        int id10 = Database.addToDatabase(e10);
        String p10 = PosterGenerator.generateDefault(e10); if(p10!=null) Database.updateEventImage(id10,p10);

        Event e11 = new Event(0, "Open Mic Night", "Sing, rap, do stand-up - the stage is yours!",
            "Odeon", now.plusDays(14), now.plusDays(14).plusHours(3), now.plusDays(13), 80, "music_club");
        e11.addTag("music"); e11.addTag("art"); e11.setXpReward(5);
        int id11 = Database.addToDatabase(e11);
        String p11 = PosterGenerator.generateDefault(e11); if(p11!=null) Database.updateEventImage(id11,p11);

        Event e12 = new Event(0, "Cybersecurity CTF", "Capture the flag competition for beginners and pros.",
            "EA-409", now.plusDays(12), now.plusDays(12).plusHours(6), now.plusDays(11), 50, "ieee_bilkent");
        e12.addTag("cybersecurity"); e12.addTag("software"); e12.setXpReward(20); e12.setMinTierIndex(2);
        int id12 = Database.addToDatabase(e12);
        String p12 = PosterGenerator.generateDefault(e12); if(p12!=null) Database.updateEventImage(id12,p12);

        Event e13 = new Event(0, "Film Screening: Interstellar", "Movie night with popcorn and discussion after.",
            "FEASS Auditorium", now.plusDays(6), now.plusDays(6).plusHours(3), now.plusDays(5), 120, "eylul");
        e13.addTag("cinema"); e13.addTag("art"); e13.setXpReward(3);
        int id13 = Database.addToDatabase(e13);
        String p13 = PosterGenerator.generateDefault(e13); if(p13!=null) Database.updateEventImage(id13,p13);

        Event e14 = new Event(0, "Yoga at Sunrise", "Morning yoga session on the east campus lawn.",
            "East Campus Lawn", now.plusDays(4), now.plusDays(4).plusHours(1), now.plusDays(3), 40, "emir_selim");
        e14.addTag("fitness"); e14.addTag("sports"); e14.setXpReward(4);
        int id14 = Database.addToDatabase(e14);
        String p14 = PosterGenerator.generateDefault(e14); if(p14!=null) Database.updateEventImage(id14,p14);

        Event e15 = new Event(0, "Beat Making Workshop", "Learn FL Studio basics and produce your first beat.",
            "Music Room B-102", now.plusDays(9), now.plusDays(9).plusHours(3), now.plusDays(8), 20, "ege");
        e15.addTag("music"); e15.addTag("software"); e15.setXpReward(8);
        int id15 = Database.addToDatabase(e15);
        String p15 = PosterGenerator.generateDefault(e15); if(p15!=null) Database.updateEventImage(id15,p15);

        Event e16 = new Event(0, "Philosophy Cafe", "This week's topic: Ethics of AI. Open discussion format.",
            "Starbucks Bilkent", now.plusDays(8), now.plusDays(8).plusHours(2), now.plusDays(7), 30, "damla");
        e16.addTag("philosophy"); e16.addTag("ai"); e16.setXpReward(5);
        int id16 = Database.addToDatabase(e16);
        String p16 = PosterGenerator.generateDefault(e16); if(p16!=null) Database.updateEventImage(id16,p16);

        Event e17 = new Event(0, "Marketing 101", "Guest speaker from Google on digital marketing strategies.",
            "FEASS B-201", now.plusDays(15), now.plusDays(15).plusHours(2), now.plusDays(14), 70, "bosman");
        e17.addTag("marketing"); e17.addTag("entrepreneurship"); e17.setXpReward(6);
        int id17 = Database.addToDatabase(e17);
        String p17 = PosterGenerator.generateDefault(e17); if(p17!=null) Database.updateEventImage(id17,p17);

        Event e18 = new Event(0, "Robotics Demo Day", "See student-built robots in action and vote for the best!",
            "Engineering Building Lobby", now.plusDays(20), now.plusDays(20).plusHours(4), now.plusDays(19), 100, "ieee_bilkent");
        e18.addTag("robotics"); e18.addTag("software"); e18.setXpReward(10);
        int id18 = Database.addToDatabase(e18);
        String p18 = PosterGenerator.generateDefault(e18); if(p18!=null) Database.updateEventImage(id18,p18);

        // Set attendance status for events (GOING, INTERESTED, MAYBE)
        Database.setAttendance(id1, "damla", AttendanceStatus.GOING);
        Database.setAttendance(id1, "ege", AttendanceStatus.GOING);
        Database.setAttendance(id1, "emir_selim", AttendanceStatus.INTERESTED);
        Database.setAttendance(id2, "damla", AttendanceStatus.GOING);
        Database.setAttendance(id2, "bosman", AttendanceStatus.INTERESTED);
        Database.setAttendance(id3, "eylul", AttendanceStatus.GOING);
        Database.setAttendance(id3, "ege", AttendanceStatus.GOING);
        Database.setAttendance(id3, "damla", AttendanceStatus.GOING);
        Database.setAttendance(id4, "emir_selim", AttendanceStatus.GOING);
        Database.setAttendance(id4, "eylul", AttendanceStatus.GOING);
        Database.setAttendance(id4, "ege", AttendanceStatus.GOING);
        Database.setAttendance(id5, "damla", AttendanceStatus.GOING);
        Database.setAttendance(id5, "bosman", AttendanceStatus.MAYBE);
        Database.setAttendance(id6, "damla", AttendanceStatus.INTERESTED);
        Database.setAttendance(id6, "ege", AttendanceStatus.GOING);
        Database.setAttendance(id7, "damla", AttendanceStatus.GOING);
        Database.setAttendance(id7, "ege", AttendanceStatus.INTERESTED);
        Database.setAttendance(id7, "emir_selim", AttendanceStatus.MAYBE);
        Database.setAttendance(id8, "damla", AttendanceStatus.GOING);
        Database.setAttendance(id8, "ege", AttendanceStatus.GOING);
        Database.setAttendance(id9, "bosman", AttendanceStatus.GOING);
        Database.setAttendance(id9, "ege", AttendanceStatus.GOING);
        Database.setAttendance(id9, "damla", AttendanceStatus.INTERESTED);
        Database.setAttendance(id10, "eylul", AttendanceStatus.INTERESTED);
        Database.setAttendance(id10, "ege", AttendanceStatus.GOING);
        Database.setAttendance(id10, "bosman", AttendanceStatus.MAYBE);
        Database.setAttendance(id11, "damla", AttendanceStatus.GOING);
        Database.setAttendance(id11, "eylul", AttendanceStatus.GOING);
        Database.setAttendance(id11, "emir_selim", AttendanceStatus.INTERESTED);
        Database.setAttendance(id12, "damla", AttendanceStatus.GOING);
        Database.setAttendance(id12, "ege", AttendanceStatus.GOING);
        Database.setAttendance(id13, "damla", AttendanceStatus.GOING);
        Database.setAttendance(id13, "emir_selim", AttendanceStatus.GOING);
        Database.setAttendance(id13, "bosman", AttendanceStatus.GOING);
        Database.setAttendance(id13, "ege", AttendanceStatus.MAYBE);
        Database.setAttendance(id14, "eylul", AttendanceStatus.GOING);
        Database.setAttendance(id14, "damla", AttendanceStatus.GOING);
        Database.setAttendance(id15, "damla", AttendanceStatus.INTERESTED);
        Database.setAttendance(id15, "eylul", AttendanceStatus.GOING);
        Database.setAttendance(id16, "eylul", AttendanceStatus.GOING);
        Database.setAttendance(id16, "ege", AttendanceStatus.GOING);
        Database.setAttendance(id16, "bosman", AttendanceStatus.INTERESTED);
        Database.setAttendance(id17, "damla", AttendanceStatus.INTERESTED);
        Database.setAttendance(id17, "emir_selim", AttendanceStatus.MAYBE);
        Database.setAttendance(id18, "damla", AttendanceStatus.GOING);
        Database.setAttendance(id18, "ege", AttendanceStatus.GOING);
        Database.setAttendance(id18, "eylul", AttendanceStatus.INTERESTED);
        Database.setAttendance(id18, "bosman", AttendanceStatus.GOING);

        // Add sample comments to events
        Comment c1 = new Comment(0, "emir_selim", "Sounds awesome! Will there be prizes?", "14/02 10:30", 0);
        int cid1 = Database.addToDatabase(c1, id1);
        Comment c2 = new Comment(0, "damla", "Yes! Top 3 get awards.", "14/02 11:00", cid1);
        Database.addToDatabase(c2, id1);
        Comment c3 = new Comment(0, "eylul", "Can't wait for this!", "14/02 12:00", 0);
        Database.addToDatabase(c3, id3);
        Comment c4 = new Comment(0, "bosman", "Is this beginner friendly?", "14/02 14:00", 0);
        Database.addToDatabase(c4, id4);
        Comment c5 = new Comment(0, "ege", "I'm bringing my drone for aerial shots!", "15/02 09:00", 0);
        Database.addToDatabase(c5, id8);
        Comment c6 = new Comment(0, "damla", "Who needs a team for basketball?", "15/02 10:00", 0);
        Database.addToDatabase(c6, id9);
        Comment c7 = new Comment(0, "emir_selim", "I'll form a team, DM me!", "15/02 10:30", 0);
        Database.addToDatabase(c7, id9);
        Comment c8 = new Comment(0, "bosman", "This pitch night is going to be fire", "15/02 11:00", 0);
        Database.addToDatabase(c8, id7);
        Comment c9 = new Comment(0, "eylul", "Can I do a poetry reading at open mic?", "15/02 14:00", 0);
        Database.addToDatabase(c9, id11);
        Comment c10 = new Comment(0, "ege", "Of course! All art forms welcome", "15/02 14:30", 0);
        Database.addToDatabase(c10, id11);
        Comment c11 = new Comment(0, "damla", "The transformer paper is a must-read", "16/02 09:00", 0);
        Database.addToDatabase(c11, id10);
        Comment c12 = new Comment(0, "emir_selim", "Interstellar is my favorite movie!", "16/02 12:00", 0);
        Database.addToDatabase(c12, id13);

        // Add sample direct messages between users
        Database.sendMessage("emir_selim", "damla", "Hey! Are you coming to the campus run?");
        Database.sendMessage("damla", "emir_selim", "Definitely! What time should we meet?");
        Database.sendMessage("ege", "eylul", "Are you performing at acoustic night?");
        Database.sendMessage("eylul", "ege", "Yes! Playing two songs :)");

        // Create sample notifications
        Database.addNotification("damla", "eylul started following you!");
        Database.addNotification("damla", "emir_selim started following you!");
        Database.addNotification("ege", "emir_selim started following you!");
        Database.addNotification("emir_selim", "bosman started following you!");

    }
}
