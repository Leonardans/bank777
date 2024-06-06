package first_project;

import first_project.DAO.AccountDAO;
import first_project.DAO.UserDAO;

import java.math.BigDecimal;

public class Bank {
    private static final Bank instance = new Bank();
    private final String name = "bank777";
    private BigDecimal totalMoney = new BigDecimal("1000000");
    private BigDecimal bankFee = new BigDecimal("0");
    private final UserDAO userDAO = new UserDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    private Bank() {

    }

    public static User systemFindUser(int id) {
        return UserDAO.getUserById(id);
    }
    public static Bank getInstance() {
        return instance;
    }
    public String getBankName() {
        return name;
    }
    public void addToMoney(BigDecimal amount, String sign) {
        switch (sign) {
            case "+" -> this.totalMoney = totalMoney.add(amount);
            case "-" -> this.totalMoney = totalMoney.subtract(amount);
        }
    }
    public void plusFee(BigDecimal amount) {
        bankFee = bankFee.add(amount);
    }
    public BigDecimal getTotalMoney() {
        return totalMoney;
    }
    public BigDecimal getBankFee() {
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
    public User login(int userID, String password) {
        User someUser = userDAO.checkUser(userID, password);
        if(someUser != null) someUser.setUserAccountsFromDB(userID);
        return someUser;
    }
    public boolean openNewAccount(User user) {
        boolean userHasMax = userDAO.hasMaxAccounts(user.getUserID(), 3);
        if(userHasMax) return false;

        boolean accountAdded = accountDAO.addAccount(user);
        if (!accountAdded) return false;
        else user.setUserAccountsFromDB(user.getUserID());
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}
