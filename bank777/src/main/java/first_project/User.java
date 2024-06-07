package first_project;

import first_project.DAO.UserDAO;

import java.util.List;
import java.util.ArrayList;

public class User {
    private final int userID;
    private final String name;
    private final String address;
    private String password;
    private List<BankAccount> userAccounts;
    private final UserDAO userDAO = new UserDAO();

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

    public synchronized void setUserAccountsFromDB(int userID) {
        userAccounts = new ArrayList<>(userDAO.getUserAccountsFromDB(userID));
    }

    public void showAccounts() {
        userAccounts.forEach(System.out::println);
    }

    @Override
    public String toString() {
        return "User [userID=" + userID + ", name=" + name + ", address=" + address + ", userAccounts=" + userAccounts
                + "]";
    }
}
