package passwordmanager;

import org.springframework.web.bind.annotation.*;

import PasswordEntry;
import PasswordManager;

import java.util.*;

@RestController
@RequestMapping("/api/passwords")
public class PasswordController {

    private PasswordManager passwordManager = new PasswordManager();

    @PostMapping("/add")
    public String addPassword(@RequestParam String accountName,
                              @RequestParam String username,
                              @RequestParam String password) {
        if (password.equalsIgnoreCase("generate")) {
            password = passwordManager.generatePassword();
        }
        passwordManager.addPassword(accountName, username, password);
        return "Password added successfully!";
    }

    @GetMapping("/{accountName}")
    public PasswordEntry getPassword(@PathVariable String accountName) {
        return passwordManager.retrievePassword(accountName);
    }

    @DeleteMapping("/{accountName}")
    public String deletePassword(@PathVariable String accountName) {
        passwordManager.deletePassword(accountName);
        return "Password deleted successfully!";
    }

    @GetMapping("/list")
    public List<String> listAccounts() {
        List<String> accounts = new ArrayList<>();
        for (PasswordEntry entry : passwordManager.passwordStore.values()) {
            accounts.add("Account: " + entry.getAccountName() + ", Username: " + entry.getUsername());
        }
        return accounts;
    }
}
