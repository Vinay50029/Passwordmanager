package passwordmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PasswordEntry implements Serializable {
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
