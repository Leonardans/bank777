package first.project;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final int userID;
    private String name;
    private String address;
    private String password;
    private List<BankAccount> userAccounts;

    public User(int userID, String name, String address, String password) {
        this.userID = userID;
        this.name = name;
        this.address = address;
        this.password = password;
        this.userAccounts = new ArrayList<>();
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

    public void plusOne(BankAccount account) {
        userAccounts.add(account);
    }

    @Override
    public String toString() {
        return "User [userID=" + userID + ", name=" + name + ", address=" + address + ", userAccounts=" + userAccounts
                + "]";
    }

}