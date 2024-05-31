package first.project;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CLI_App {
    Bank bank = Bank.getInstance();

    public void openBank() {
        System.out.println("Welcome. We're pleased to greet you. We offer the best services in town." +
        "To get started, please enter your information or create your unique profile.");

        Scanner scanner = new Scanner(System.in);
        
        proccessChoise1(scanner);
    }

    public void openProfile(User activeUser, Scanner scanner) {
        System.out.println("\nWelcome, " + activeUser.getName() + " !\n");

        if(activeUser.getUserAccounts().isEmpty()) {
            System.out.println("Currently, you do not have any active accounts open. If you wish, " +
            "you can quickly and, absolutely safely open a new account at the most " +
            "reliable bank in the city!");
            proccessChoise2(activeUser, scanner);
        } else {
            proccessChoise3(activeUser, scanner);
        }

    }

    public void proccessChoise1(Scanner scanner) {
        System.out.println("1. Login");
        System.out.println("2. Create new profile.");
        switch (scanner.nextLine()) {
            case "1":
                proccessLogin(scanner);
                break;
            case "2":
                proccessCreateNewProfile(scanner);
                break;
            default:
                System.out.println("Please, provide only 1 - if you want to login or " +
                "2 - if you want create new profile\n");
                proccessChoise1(scanner);
                
        } 
    }
    public void proccessLogin(Scanner scanner) {
        int id = 0;
        String password = "";
        for(int i = 0; i < 5; i++) {
            System.out.println("Please, enter your userID: ");
            String providedID = scanner.nextLine();
            if(!checkUserID(providedID)) {
                System.out.println("UserID most contain 8 numbers!\n");
                continue;
            }

            System.out.println("Please, enter your password: ");
            String providedPassword = scanner.nextLine();
            if(!checkPassword(providedPassword)) {
                System.out.println("The password must contain at least 8 characters, including at least " +
                "one digit, one lowercase letter, one uppercase letter, and one special character from the set @#$%^&+=.\n");
                continue;
            }

            if(checkUserID(providedID) && checkPassword(providedPassword)) {
                id = Integer.parseInt(providedID);
                password = providedPassword;
                break;
            }
        }
        if(bank.login(id, password)) {
            System.out.println("You login successfully!");
            openProfile(bank.systemFindUser(id), scanner);
        } else {
            System.out.println("Please, check userID or password.\n");
            proccessChoise1(scanner);
        }
    }
    public void proccessCreateNewProfile(Scanner scanner) {
        System.out.println("It's your best choise for today!");
        String name = "";
        String address = "";
        String password = "";
        
        for(int i = 0; i < 5; i++) {
            System.out.println("Please, enter your name: ");
            String providedName = scanner.nextLine();
            if(!checkName(providedName)) {
                System.out.println("Incorrect name!");
                continue;
            }

            System.out.println("Please, enter your password: ");
            String providedPassword = scanner.nextLine();
            if(!checkPassword(providedPassword)) {
                System.out.println("The password must contain at least 8 characters, including at least " +
                "one digit, one lowercase letter, one uppercase letter, and one special character from the set @#$%^&+=.");
                continue;
            }

            System.out.println("Please, enter your address: ");
            String providedAddress = scanner.nextLine();
            if(!checkAddress(providedAddress)) {
                System.out.println("Incorrect address!");
                continue;
            }

            if(checkName(providedName) && checkPassword(providedPassword) && checkAddress(providedAddress)) {
                name = providedName;
                password = providedPassword;
                address = providedAddress;
                break;
            }
            if(i == 4) {
                proccessChoise1(scanner);
            }
        }

        int userID = bank.createNewProfile(name, address, password);

        System.out.println("New profile was created! Thank you for choosing our bank. We assure you that you will be satisfied with our services!");
        System.out.println("Your userID is " + userID + " please, remember it!");
    }

    public void proccessChoise2(User activeUser, Scanner scanner) {
        System.out.println("\n1. Open new account");

        switch (scanner.nextLine()) {
            case "1":
                    proccessOpenNewAccount(activeUser, scanner);
                break;
        
            default:
                System.out.println("Please, provide correct number.\n");
                proccessChoise2(activeUser, scanner);
        }
    }
    public void proccessOpenNewAccount(User activeUser, Scanner scanner) {
        System.out.println("We are generating your ID and account number...");
        if(bank.openNewAccount(activeUser)) {
            System.out.println("\nCongratulations! The account has been successfully opened.\n");
            openProfile(activeUser, scanner);
        }
    }

    public void proccessChoise3(User activeUser, Scanner scanner) {
        System.out.println("Active accounts: ");
        System.out.println(activeUser.getUserAccounts());
        System.out.println("\n1. Withdrawal");
        System.out.println("2. Deposit");
        System.out.println("3. Transactions history");
        System.out.println("4. Open new account");

        switch (scanner.nextLine()) {
            case "1":
                proccessWithdrawal(activeUser, scanner);
                break;
            case "2":
                proccessDeposit(activeUser, scanner);
                break;
            case "3":
                proccessTransactionsHistory(activeUser, scanner);
                break;
            case "4":
                proccessOpenNewAccount(activeUser, scanner);
                break;
            default:
                System.out.println("Please, provide correct number.\n");
                proccessChoise3(activeUser, scanner);
        }
    }
    public void proccessWithdrawal(User activeUser, Scanner scanner) {
        BankAccount fromAccount = activeUser.getUserAccounts().size() > 1 ? 
        proccessChooseAccount(activeUser, scanner) : activeUser.getUserAccounts().get(0);
        BankAccount toAccount = null;

        for(int i = 5; i > 0; i--) {
            System.out.println("Please enter the account number to which you wish to transfer money.");
            String providedAccountNumber = scanner.nextLine();
            if(!fromAccount.correctAccount(providedAccountNumber)) {
                System.out.println("Please, enter the correct account number.");
                continue;
            }
            if(!bank.accountPresent(providedAccountNumber)) {
                System.out.println("Please, check account number.");
                continue;
            }
            toAccount = bank.getAccountForTransfer(providedAccountNumber);
            break;
        }
        if(toAccount == null) {
            System.out.println("Transfer not executed.\n");
            return;
        }
 
        double amount = 0D;
        for(int i = 5; i > 0; i--) {
            System.out.println("Please enter the amount you wish to transfer.");
            String provided = scanner.nextLine();
 
            if(fromAccount.correctSum(provided)) {
                amount = Double.parseDouble(provided);
                break;
            } else {
                System.out.println("Please, provide correct amount for transfer.\n");
            }
        }
        if(amount == 0D) {
            System.out.println("Transfer not executed.");
            return;
        }
        
        if(fromAccount.withdrawal(toAccount, amount)) {
            System.out.println("Transfer was successfully executed.\n");
            openProfile(activeUser, scanner);
        } else {
            System.out.println("Transfer not executed.\n");
            openProfile(activeUser, scanner);
        }
    }
    public void proccessDeposit(User activeUser, Scanner scanner) {
        BankAccount account = activeUser.getUserAccounts().size() > 1 ? 
        proccessChooseAccount(activeUser, scanner) : activeUser.getUserAccounts().get(0);

        System.out.println("Please enter the amount you wish to deposit.");
        String amount = scanner.nextLine();
        if(account.correctSum(amount)) {
            account.deposit(Double.parseDouble(amount));
            openProfile(activeUser, scanner);
        } else {
            System.out.println("Please, enter correct amount for deposit.\n");
            proccessChoise3(activeUser, scanner);
        }
    }
    public void proccessTransactionsHistory(User activeUser, Scanner scanner) {
        BankAccount account = activeUser.getUserAccounts().size() > 1 ? 
        proccessChooseAccount(activeUser, scanner) : activeUser.getUserAccounts().get(0);

        if(account.getTransactionHistory().size() == 0) {
            System.out.println("At the moment, there haven't been any transactions made.\n");
        } else {
            account.getTransactionHistory();
            System.out.println();
            openProfile(activeUser, scanner);
        }
    }

    public BankAccount proccessChooseAccount(User activeUser, Scanner scanner) {
        System.out.println("Please enter the number which account you want use.");
        String provided = scanner.nextLine();
        
        if(checkNum(provided)) {
            if(activeUser.getUserAccounts().get(Integer.valueOf(provided)) == null) {
                System.out.println("Please, enter correct number.\n");
                proccessChoise3(activeUser, scanner);
            } else {
                return activeUser.getUserAccounts().get(Integer.valueOf(provided));
            }
        } else {
            System.out.println("Please, enter correct number.");
            proccessChoise3(activeUser, scanner);
        }

        return null;
    }

    public boolean checkName(String provided) {
        String nameRegex = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";
        Pattern namePattern = Pattern.compile(nameRegex);
        Matcher nameMatcher = namePattern.matcher(provided);

        return nameMatcher.matches();
    }
    public boolean checkAddress(String provided) {
        String addressRegex = "^([a-zA-Z]+\\s){0,2}[a-zA-Z]+\\s\\d+-\\d+$";
        Pattern addressPattern = Pattern.compile(addressRegex);
        Matcher addressMatcher = addressPattern.matcher(provided);

        return addressMatcher.matches();
    }
    public boolean checkPassword(String provided) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        Pattern passwordPattern = Pattern.compile(passwordRegex);
        Matcher passwordMatcher = passwordPattern.matcher(provided);

        return passwordMatcher.matches();
    }
    public boolean checkUserID(String provided) {
        String idRegex = "^\\d{6}$";
        Pattern idPattern = Pattern.compile(idRegex);
        Matcher idMatcher = idPattern.matcher(provided);

        return idMatcher.matches();
    }
    public boolean checkNum(String provided) {
        String numRegex = "[0-9]";
        Pattern numPattern = Pattern.compile(numRegex);
        Matcher numMatcher = numPattern.matcher(provided);

        return numMatcher.matches();
    }

    public static void main(String[] args) {
        CLI_App bank777 = new CLI_App();
        bank777.openBank();
    }
}
