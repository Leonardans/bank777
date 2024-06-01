package first.project;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;


public class User {
    private final int userID;
    private final String name;
    private final String address;
    private String password;
    private List<BankAccount> userAccounts;

    public User(int userID, String name, String address, String password) {
        this.userID = userID;
        this.name = name;
        this.address = address;
        this.password = password;
    }

    public int getUserID() {
        return userID;
    }
    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public List<BankAccount> getUserAccounts() {
        return userAccounts;
    }
    public synchronized void setUserAccountsFromDatabase(List<BankAccount> fromDatabase) {
        userAccounts = Objects.requireNonNullElseGet(fromDatabase, ArrayList::new);
    }

    public void showAccounts() {
        for(BankAccount account : userAccounts) {
            System.out.println(account);
        }
    }

    public void plusOne(BankAccount account) {
        if (userAccounts.size() < 3) {
            userAccounts.add(account);
        }
    }

    @Override
    public String toString() {
        return "User [userID=" + userID + ", name=" + name + ", address=" + address + ", userAccounts=" + userAccounts
                + "]";
    }
}
