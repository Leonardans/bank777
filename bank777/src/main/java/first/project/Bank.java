package first.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bank {
    private String name;
    private Integer totalMoney;
    private Integer bankMoney;
    private List<Admin> admins;
    private List<User> users;
    private List<BankAccount> accounts;

    public Bank(String name) {
        this.name = name;
        totalMoney = 0;
        bankMoney = 0;
        admins = new ArrayList<>();
        users = new ArrayList<>();
        accounts = new ArrayList<>();
        System.out.println("Bank" + name + " was created!");
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }
    public String getName() {
        return name;
    }
    public void changeName(String name) {
        this.name = name;
    }
    public List<Admin> getAdmins() {
        return admins;
    }
    public List<User> getUsers() {
        return users;
    }
    public Integer getTotalMoney() {
        return totalMoney;
    }
    public Integer getBankMoney() {
        return bankMoney;
    }
    
    public void bankCreation(String name) {
        Bank newBank = new Bank(name);
        newBank.newAdmin(0, "admin0");
        newBank.newAdmin(1, "admin1");
        newBank.setMoney(1_000_000);
    }
    
    public void newAdmin(int userID, String password) {
        admins.add(new Admin(userID, password));
    }

    public void setMoney(Integer money) {
        totalMoney = totalMoney + money;
        System.out.println(money + " was added successfully. Total money in bank is " + totalMoney);
    }

    public void openBank() {
        System.out.println("Welcome. We're pleased to greet you. We offer the best services in town." +
        "To get started, please enter your information or create your unique profile.");

        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("1. Login");
            System.out.println("2. Create new profile.");
            int op;
       
            try{
                op = scanner.nextInt();
                if(op == 1) {
                    if(login(scanner)) {
                        break;
                    }
                } else if(op == 2) {
                    if(createNewProfile(scanner)) {
                        if(login(scanner)) {
                            break;
                        }
                    }
                }
            }catch(NumberFormatException e) {
                System.out.println("Please, provide only 1 - if you want to login or " +
                "2 - if you want create new profile");
            }
        }
    }

    public boolean createNewProfile(Scanner scanner) {
        System.out.println("It's your best choise for today!");
        String name = "";
        String address = "";
        String password = "";
        String nameRegex = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
        Pattern namePattern = Pattern.compile(nameRegex);
        String addressRegex = "^([a-zA-Z]+\\s){0,2}[a-zA-Z]+\\s\\d+-\\d+$";
        Pattern addressPattern = Pattern.compile(addressRegex);
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern passwordPattern = Pattern.compile(passwordRegex);

        for(int i = 0; i < 5; i++) {
            System.out.println("Please, enter your name: ");
            String providedName = scanner.nextLine();
            Matcher nameMatcher = namePattern.matcher(providedName);
            if(!nameMatcher.matches()) {
                System.out.println("Incorrect name!");
                continue;
            }

            System.out.println("Please, enter your password: ");
            String providedPassword = scanner.nextLine();
            Matcher passwordMatcher = passwordPattern.matcher(providedPassword);
            if(!passwordMatcher.matches()) {
                System.out.println("The password must contain at least 8 characters, including at least " +
                "one digit, one lowercase letter, one uppercase letter, and one special character from the set @#$%^&+=.");
                continue;
            }

            System.out.println("Please, enter your address: ");
            String providedAddress = scanner.nextLine();
            Matcher addressMatcher = addressPattern.matcher(providedAddress);
            if(!addressMatcher.matches()) {
                System.out.println("Incorrect address!");
                continue;
            }

            if(nameMatcher.matches() && passwordMatcher.matches() && addressMatcher.matches()) {
                name = providedName;
                password = providedPassword;
                address = providedAddress;
                break;
            }

            if(i == 4) {
                return false;
            }
        }

    
        int id = (int)Math.random() * 1_000_000;

        while(true) {
            boolean check = true;
            for(int i = 0; i < users.size(); i++) {
                if(id == users.get(i).getUserID()) {
                    id = (int)Math.random() * 1_000_000;
                    check = false;
                    break;
                }
            }
            if(check) {
                break;
            }
        }
        
        users.add(new User(id, name, address, password));
        System.out.println("New profile was created!");
        System.out.println("Your userID is " + id + " please, remember it!");
        return true;
    }

    public boolean login(Scanner scanner) {
        int id = 0;
        String password = "";
        String idRegex = "^\\d{8}$";
        Pattern idPattern = Pattern.compile(idRegex);
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern passwordPattern = Pattern.compile(passwordRegex);

        for(int i = 0; i < 5; i++) {
            System.out.println("Please, enter your userID: ");
            String providedID = scanner.nextLine();
            Matcher idMatcher = idPattern.matcher(providedID);
            if(!idMatcher.matches()) {
                System.out.println("UserID most contain 8 numbers!");
                continue;
            }

            System.out.println("Please, enter your password: ");
            String providedPassword = scanner.nextLine();
            Matcher passwordMatcher = passwordPattern.matcher(providedPassword);
            if(!passwordMatcher.matches()) {
                System.out.println("The password must contain at least 8 characters, including at least " +
                "one digit, one lowercase letter, one uppercase letter, and one special character from the set @#$%^&+=.");
                continue;
            }

            if(idMatcher.matches() && passwordMatcher.matches()) {
                id = Integer.parseInt(providedID);
                password = providedPassword;
                break;
            }
        }
        
        boolean check = false;
        for(int i = 0; i < users.size(); i++) {
            if(users.get(i).getUserID() == id) {
                check = password.equals(users.get(i).getPassword()) ? true : false;
                break;
            }
        }

        if(check) {
            scanner.close();
            System.out.println("You login successfullly");
            openProfile(id);
        } else {
            System.out.println("Wrong userID or password");
        }
        return check;
    }

    public User systemFindUser(int id) {
        for(int i = 0; i < users.size(); i++) {
            if(id == users.get(i).getUserID()) {
                return users.get(i);
            }
        }
        return null;
    }

    public void openProfile(int id) {
        User activeUser = systemFindUser(id);
        System.out.println("\nWelcome, " + activeUser.getName() + " !\n");

        if(activeUser.getUserAccounts().size() == 0) {
            System.out.println("Currently, you do not have any active accounts open. If you wish, " + 
            "you can quickly and, absolutely safely open a new account at the most " +
            "reliable bank in the city!");
            System.out.println("\n1. Open new account");
        } else {
            System.out.println("Active accounts: ");
            System.out.println(activeUser.getUserAccounts());
            System.out.println("\n1. Withdrawal");
            System.out.println("2. Deposit");
            System.out.println("3. Transactions history");
        }

        

    }

    @Override
    public String toString() {
        return "Bank [name=" + name + "]";
    }
    
}
