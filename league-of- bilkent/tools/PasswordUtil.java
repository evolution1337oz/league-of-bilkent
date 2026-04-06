package tools;
import model.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

// to hash password and check later them
public class PasswordUtil {

private static final Random random = new Random();
    
    // Generates a random salt 
    public static String generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    
    // make a SHA-256 hash with  password + salt.
     
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not supported!", e);
        }
    }

    // check whether password is true
    public static boolean verifyPassword(String password, String storedHash, String storedSalt) {
        String newHash = hashPassword(password, storedSalt);
        return newHash.equals(storedHash);
    }
}
