package passwordmanager;

import java.io.*;
import java.security.SecureRandom;
import java.util.*;

import PasswordEntry;

public class PasswordManager {
    private Map<String, PasswordEntry> passwordStore;
    private final int PASSWORD_LENGTH = 10; // Fixed password length
    private final String FILE_NAME = "password_data.ser"; // File to store password data

    public PasswordManager() {
        passwordStore = loadFromFile();
    }

    public void addPassword(String accountName, String username, String password) {
        if (passwordStore.containsKey(accountName)) {
            System.out.println("Error: Account name already exists. Please choose a unique name.");
            return;
        }

        String encryptedPassword = encryptPassword(password); // Encrypt the password

        PasswordEntry entry = new PasswordEntry(accountName, username, encryptedPassword);
        passwordStore.put(accountName, entry);
        saveToFile();
        System.out.println("Password added successfully!");
    }

    public PasswordEntry retrievePassword(String accountName) {
        return passwordStore.get(accountName);
    }

    public void deletePassword(String accountName) {
        if (passwordStore.remove(accountName) != null) {
            saveToFile();
            System.out.println("Password deleted successfully!");
        } else {
            System.out.println("Account not found.");
        }
    }

    public void listAccounts() {
        for (PasswordEntry entry : passwordStore.values()) {
            System.out.println("Account: " + entry.getAccountName() + ", Username: " + entry.getUsername());
        }
    }

    public void viewPasswordHistory(String accountName) {
        PasswordEntry entry = passwordStore.get(accountName);
        if (entry != null) {
            System.out.println("Password History for Account: " + accountName);
            for (String encryptedPassword : entry.getPasswordHistory()) {
                System.out.println("Password: " + decryptPassword(encryptedPassword));
            }
        } else {
            System.out.println("Account not found.");
        }
    }

    private String encryptPassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes()); // Simple encryption (Base64)
    }

    public String decryptPassword(String encryptedPassword) {
        return new String(Base64.getDecoder().decode(encryptedPassword)); // Simple decryption (Base64)
    }

    public String generatePassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()-_=+[]{}|;:,.<>?";

        String allCharacters = upper + lower + digits + special;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(allCharacters.length());
            password.append(allCharacters.charAt(index));
        }

        return password.toString();
    }

    public String getPasswordStrength(String password) {
        int length = password.length();
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            } else if ("!@#$%^&*()-_=+[]{}|;:,.<>?".indexOf(c) >= 0) {
                hasSpecial = true;
            }
        }

        if (length >= 10 && hasUpper && hasLower && hasDigit && hasSpecial) {
            return "Strong";
        } else if (length >= 8 && ((hasUpper && hasLower) || (hasUpper && hasDigit) || (hasLower && hasDigit))) {
            return "Medium";
        } else {
            return "Weak";
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(passwordStore);
        } catch (IOException e) {
            System.out.println("Error saving data to file.");
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, PasswordEntry> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (Map<String, PasswordEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No previous data found. Starting with an empty password store.");
            return new HashMap<>();
        }
    }
}
