package first.project;

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
    public void setUserAccountsFromDatabase(List<BankAccount> fromDatabase) {
        userAccounts = fromDatabase;
    }

    public void showAccounts() {
        for(BankAccount account : userAccounts) {
            System.out.println(account);
        }
    }
    
    public boolean plusOne(BankAccount account) {
        if(userAccounts.size() >= 3) {
            return false;
        } else {
            userAccounts.add(account);
            return true;
        }
    }

    @Override
    public String toString() {
        return "User [userID=" + userID + ", name=" + name + ", address=" + address + ", userAccounts=" + userAccounts
                + "]";
    }

}
