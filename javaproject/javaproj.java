
//package com.passwordmanager;
import java.io.*;
import java.security.SecureRandom;
import java.util.*;

class PasswordEntry implements Serializable {
    private String accountName;
    private String username;
    private List<String> encryptedPasswordHistory;

    public PasswordEntry(String accountName, String username, String encryptedPassword) {
        this.accountName = accountName;
        this.username = username;
        this.encryptedPasswordHistory = new ArrayList<>();
        this.encryptedPasswordHistory.add(encryptedPassword);
    }

    public String getAccountName() {
        return accountName;
    }

    public String getUsername() {
        return username;
    }

    public String getLatestPassword() {
        return encryptedPasswordHistory.get(encryptedPasswordHistory.size() - 1);
    }

    public List<String> getPasswordHistory() {
        return encryptedPasswordHistory;
    }

    public void addPasswordToHistory(String encryptedPassword) {
        this.encryptedPasswordHistory.add(encryptedPassword);
    }

    public boolean isPasswordUsedBefore(String encryptedPassword) {
        return encryptedPasswordHistory.contains(encryptedPassword);
    }
}

class PasswordManager {
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
        }+
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

public class javaproj {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PasswordManager passwordManager = new PasswordManager();

        while (true) {
            System.out.println("\nPassword Manager");
            System.out.println("1. Add Password");
            System.out.println("2. Retrieve Password");
            System.out.println("3. Delete Password");
            System.out.println("4. List All Accounts");
            System.out.println("5. Generate Password");
            System.out.println("6. View Password History");
            System.out.println("7. Exit\n");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: // Add Password
                    System.out.print("Enter account name: ");
                    String accountName = scanner.nextLine();
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    System.out.print("Enter password (or type 'generate' to generate one): ");
                    String password = scanner.nextLine();

                    if (password.equalsIgnoreCase("generate")) {
                        password = passwordManager.generatePassword();
                        System.out.println("Generated Password: " + password);
                    }

                    String strength = passwordManager.getPasswordStrength(password);
                    System.out.println("Password Strength: " + strength);

                    passwordManager.addPassword(accountName, username, password);
                    break;

                case 2: // Retrieve Password
                    System.out.print("Enter account name: ");
                    String retrieveAccount = scanner.nextLine();
                    PasswordEntry entry = passwordManager.retrievePassword(retrieveAccount);
                    if (entry != null) {
                        String decryptedPassword = passwordManager.decryptPassword(entry.getLatestPassword());
                        System.out.println("Account: " + entry.getAccountName());
                        System.out.println("Username: " + entry.getUsername());
                        System.out.println("Latest Password: " + decryptedPassword);
                    } else {
                        System.out.println("Account not found.");
                    }
                    break;

                case 3: // Delete Password
                    System.out.print("Enter account name: ");
                    String deleteAccount = scanner.nextLine();
                    passwordManager.deletePassword(deleteAccount);
                    break;

                case 4: // List All Accounts
                    System.out.println("Stored Accounts:");
                    passwordManager.listAccounts();
                    break;

                case 5: // Generate Password
                    String generatedPassword = passwordManager.generatePassword();
                    System.out.println("Generated Password: " + generatedPassword);
                    String genStrength = passwordManager.getPasswordStrength(generatedPassword);
                    System.out.println("Password Strength: " + genStrength);
                    break;

                case 6: // View Password History
                    System.out.print("Enter account name: ");
                    String historyAccount = scanner.nextLine();
                    passwordManager.viewPasswordHistory(historyAccount);
                    break;

                case 7: // Exit
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
