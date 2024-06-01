package first.project;

import java.util.List;
import java.util.Random;

import first.project.DAO.AccountDAO;
import first.project.DAO.BankDAO;
import first.project.DAO.TransactionDAO;
import first.project.DAO.UserDAO;

public class Bank {
    private static final Bank instance = new Bank();
    private final String name;
    private Double totalMoney;
    private Double bankFee;
    private UserDAO userDAO;
    private AccountDAO accDAO;
    private TransactionDAO transDAO;

    private Bank() {
        this.name = "bank777";
        totalMoney = 1_000_000D;
        bankFee = 0D;
        userDAO = new UserDAO();
        accDAO = new AccountDAO();
        transDAO = new TransactionDAO();
    }
     static {
        if(!BankDAO.insertBankData("bank777", 1_000_000D, 0, 0)) {
            instance.setTotalMoney(BankDAO.getTotalMoney());
            instance.plusFee(BankDAO.getBankFee());
        }
    }
    public static Bank getInstance() {
        return instance;
    }

    public void setTotalMoney(Double money) {
        totalMoney+= money;
    }
  
    public Double getTotalMoney() {
        return totalMoney;
    }
    
    public Double  getBankFee() {
        return bankFee;
    }
    
    public void plusFee(double amount) {
        bankFee+= amount;
    }

    public int generateUserID() {
        Random random = new Random();
        int id = random.nextInt(899_999) + 100_000;

        while(true) {
            boolean check = userDAO.checkUserIdExistence(id);
            if (check) id = random.nextInt(899_999) + 100_000;
            else break;
        }

        return id;
    }
    
    public int generateAccountID() {
        Random random = new Random();
        int accID = random.nextInt(8_999_999) + 1_000_000;

        while(true) {
            boolean check = accDAO.doesAccountIdExist(accID);
            if(check) accID = random.nextInt(8_999_999) + 1_000_000;
            else break;
        }
        
        return accID;
    }
    
    public String generateAccountNumber() {
        Random random = new Random();
        String accNum = "E" + (random.nextLong(8_999_999_999_999L) + 1_000_000_000_000L);

        while(true) {
            boolean check = accDAO.doesAccountNumberExist(accNum);
            if (check) accNum = "E" + (random.nextLong(8_999_999_999_999L) + 1_000_000_000_000L);  
            else break; 
        }

        return accNum;
    }
    
    public int generateTransactionID() {
        Random random = new Random();
        int transId = random.nextInt(88_999_999) + 10_000_000;

        while(true) {
            boolean check = transDAO.checkTransactionExistence(transId);
            if (check) transId = random.nextInt(88_999_999) + 10_000_000; 
            else break; 
        }

        return transId;
    }

    public boolean accountPresent(String accountNumber) {
       return accDAO.doesAccountNumberExist(accountNumber);
    }
    
    public BankAccount getAccountForTransfer(String accountNumber) {
        return accDAO.getAccountByAccountNumber(accountNumber);
    }

    public int createNewProfile(String name, String address, String password) {
        int id = generateUserID();
        
        if(userDAO.addUser(id, name, address, password)) return id;

        return 0;
    }

    public boolean login(int id, String password) {
        User someOne = userDAO.getUserByUsernameAndPassword(id, password);

        if(someOne == null) {
            return false;
        }

        someOne.setUserAccountsFromDatabase(userDAO.getUserAccounts(id));
        return true;
    }

    public User systemFindUser(int id) {
       return userDAO.getUserById(id);
    }
    
    public boolean openNewAccount(User user) {
        List<BankAccount> userAccounts = userDAO.getUserAccounts(user.getUserID());

        if(userAccounts.size() >= 3) {
            return false;
        }
        int accID = generateAccountID();
        String accNum = generateAccountNumber();
        BankAccount newAccount = new BankAccount(accID, accNum, 0D, user.getUserID());
        user.plusOne(newAccount);

        if(accDAO.addAccount(accID,user.getUserID(), accNum, accID)) return true;
        else return false;
    }

    @Override
    public String toString() {
        return name;
    }

}
