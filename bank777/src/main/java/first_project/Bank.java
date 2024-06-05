package first_project;

import java.util.List;
import first_project.DAO.AccountDAO;
import first_project.DAO.BankDAO;
import first_project.DAO.UserDAO;

public class Bank {
    private static final Bank instance = new Bank();
    private final String name = "bank777";
    private Double totalMoney = 1_000_000D;
    private Double bankFee = 0D;
    private final BankDAO bankDAO = new BankDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    private Bank() {

    }

    public static Bank getInstance() {
        return instance;
    }
    public String getBankName() {
        return name;
    }
    public void addToMoney(Double amount, String sign) {
        switch (sign) {
            case "+" -> this.totalMoney += amount;
            case "-" -> this.totalMoney -= amount;
        }
    }
    public void plusFee(double amount) {
        bankFee += amount;
    }
    public Double getTotalMoney() {
        return totalMoney;
    }
    public Double  getBankFee() {
        return bankFee;
    }
    public boolean accountPresent(String accountNumber) {
       return accountDAO.doesAccountNumberExist(accountNumber);
    }
    public BankAccount getAccountForTransfer(String accountNumber) {
        return accountDAO.getAccountByAccountNumber(accountNumber);
    }
    public int createNewProfile(String name, String address, String password) {
        return userDAO.addUser(name, address, password);
    }
    public User login(int id, String password) {
        User someUser = userDAO.checkUser(id, password);
        if(someUser != null) someUser.setUserAccountsFromDatabase(userDAO.getUserAccounts(id));
        return someUser;
    }
    public static User systemFindUser(int id) {
        return UserDAO.getUserById(id);
    }
    public boolean openNewAccount(User user) {
        List<BankAccount> userAccounts = userDAO.getUserAccounts(user.getUserID());
        if(userAccounts.size() >= 3) return false;

        return accountDAO.addAccount(user);
    }

    @Override
    public String toString() {
        return name;
    }
}
