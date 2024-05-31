package first.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bank {
    private static final Bank instance = new Bank();
    private final String name;
    private Double totalMoney;
    private Double bankFee;
    private final List<Admin> admins;
    private final List<User> users;
    private final List<BankAccount> accounts;

    private Bank() {
        this.name = "bank777";
        totalMoney = 1_000_000D;
        bankFee = 0D;
        admins = new ArrayList<>();
        users = new ArrayList<>();
        accounts = new ArrayList<>();
    }
    static {
        Bank bank = Bank.getInstance();
        bank.loadInitialData();
    }
    
    public static Bank getInstance() {
        return instance;
    }

    private void loadInitialData() {
        users.add(new User(generateUserID(), "User1", "Address1", "password1"));
        users.add(new User(generateUserID(), "User2", "Address2", "password2"));
        users.add(new User(generateUserID(), "User3", "Address3", "password3"));
        users.add(new User(generateUserID(), "User4", "Address4", "password4"));
        users.add(new User(generateUserID(), "User5", "Address5", "password5"));

        accounts.add(new BankAccount(generateAccountID(), generateAccountNumber(), 10_000D, users.get(0).getUserID(), this));
        accounts.add(new BankAccount(generateAccountID(), generateAccountNumber(), 10_000D, users.get(1).getUserID(), this));
        accounts.add(new BankAccount(generateAccountID(), generateAccountNumber(), 10_000D, users.get(2).getUserID(), this));
        accounts.add(new BankAccount(generateAccountID(), generateAccountNumber(), 10_000D, users.get(3).getUserID(), this));
        accounts.add(new BankAccount(generateAccountID(), generateAccountNumber(), 10_000D, users.get(4).getUserID(), this));

        users.get(0).plusOne(accounts.get(0));
        users.get(1).plusOne(accounts.get(1));
        users.get(2).plusOne(accounts.get(2));
        users.get(3).plusOne(accounts.get(3));
        users.get(4).plusOne(accounts.get(4));
    }

    public void setMoney(Double money) {
        totalMoney+= money;
    }
    public List<BankAccount> getAccounts() {
        return accounts;
    }
    public String getName() {
        return name;
    }
    public List<Admin> getAdmins() {
        return admins;
    }
    public List<User> getUsers() {
        return users;
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
            boolean check = true;
            for (User user : users) {
                if (id == user.getUserID()) {
                    id = random.nextInt(899_999) + 100_000;
                    check = false;
                    break;
                }
            }
            if(check) {
                break;
            }
        }
        return id;
    }
    public int generateAccountID() {
        Random random = new Random();
        int accID = random.nextInt(8_999_999) + 1_000_000;

        while(true) {
            boolean check = true;
            for (BankAccount account : accounts) {
                if (accID == account.getAccountID()) {
                    accID = random.nextInt(8_999_999) + 1_000_000;
                    check = false;
                }
            }
            if(check) break;
        }
        return accID;
    }
    public String generateAccountNumber() {
        Random random = new Random();
        String accNum = "E" + (random.nextLong(8_999_999_999_999L) + 1_000_000_000_000L);

        while(true) {
            boolean check = true;
            for (BankAccount account : accounts) {
                if (accNum.equals(account.getAccountNumber())) {
                    accNum = "E" + (random.nextLong(8_999_999_999_999L) + 1_000_000_000_000L);
                    check = false;
                }
            }
            if(check) break;
        }
        return accNum;
    }
    public int generateTransactionID() {
        Random random = new Random();
        return random.nextInt(88_999_999) + 10_000_000;
    }

    public boolean accountPresent(String accountNumber) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return true;
            }
        }
       return  false;
    }
    public BankAccount getAccountForTransfer(String accountNumber) {
        for (BankAccount account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }

    public void newAdmin(int userID, String password) {
        admins.add(new Admin(userID, password));
    }

    public int createNewProfile(String name, String address, String password) {
        int id = generateUserID();
        users.add(new User(id, name, address, password));
        
        return id;
    }

    public boolean login(int id, String password) {
        boolean check = false;
        for (User user : users) {
            if (user.getUserID() == id) {
                check = password.equals(user.getPassword());
                break;
            }
        }

        return check;
    }

    public User systemFindUser(int id) {
        for (User user : users) {
            if (id == user.getUserID()) {
                return user;
            }
        }
        return null;
    }
    
    public boolean openNewAccount(User user) {
        if(user.getUserAccounts().size() >= 3) {
            return false;
        }
        int accID = generateAccountID();
        String accNum = generateAccountNumber();
        BankAccount newAccount = new BankAccount(accID, accNum, 0D, user.getUserID(), this);
        user.plusOne(newAccount);
        accounts.add(newAccount);

        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}
