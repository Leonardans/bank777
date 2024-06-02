package first.project;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import first.project.DAO.DatabaseSetup;

public final class CLI_APP {
    Bank bank = Bank.getInstance();

    public void openBank() {
        System.out.println("\nWelcome. We're pleased to greet you. We offer the best services in town." +
        "To get started, please enter your information or create your unique profile.");

        Scanner scanner = new Scanner(System.in);

        processChoose1(scanner);
    }

    public void openProfile(User activeUser, Scanner scanner) {
        System.out.println("\nWelcome, " + activeUser.getName() + " !");

        if(activeUser.getUserAccounts().isEmpty()) {
            System.out.println("Currently, you do not have any active accounts open. If you wish, " +
            "you can quickly and, absolutely safely open a new account at the most " +
            "reliable bank in the city!");
            processChoose2(activeUser, scanner);
        } else {
            processChoose3(activeUser, scanner);
        }
    }

    public void processChoose1(Scanner scanner) {
        System.out.print("1. Login  ");
        System.out.println("2. Create new profile.");
        switch (scanner.nextLine()) {
            case "1":
                processLogin(scanner);
                break;
            case "2":
                processCreateNewProfile(scanner);
                break;
            default:
                System.out.println("Please, provide only 1 - if you want to login or " +
                "2 - if you want create new profile\n");
                processChoose1(scanner);

        }
    }
    public void processLogin(Scanner scanner) {
        int id = Integer.parseInt(enterUserID(scanner));
        String password = enterPassword(scanner);

        if(id == 0) {
            System.out.println("Please, check userID or password.\n");
            processChoose1(scanner);
        }

        User user = bank.login(id, password);
        if(user != null) {
            System.out.println("You login successfully!");
            openProfile(user, scanner);
        } else {
            System.out.println("Please, check userID or password.\n");
            processChoose1(scanner);
        }
    }
    public void processCreateNewProfile(Scanner scanner) {
        System.out.println("\nIt's your best choose for today!");
        String name = enterName(scanner);
        String password = enterPassword(scanner);
        String address = enterAddress(scanner);
        if(name.isEmpty()) {
            processChoose1(scanner);
        }
        int userID = bank.createNewProfile(name, address, password);

        if(userID == 0) {
            System.out.println("Profile not created.\n");
            openBank();
        } else {
            System.out.println("\nNew profile was created! Thank you for choosing our bank. We assure you that you will be satisfied with our services!");
            System.out.println("Your userID is " + userID + " please, remember it!");
            openBank();
        }
    }

    public void processChoose2(User activeUser, Scanner scanner) {
        System.out.println("\n1. Open new account");

        switch (scanner.nextLine()) {
            case "1" -> processOpenNewAccount(activeUser, scanner);
            default -> {
                System.out.println("Please, provide correct number.\n");
                processChoose2(activeUser, scanner);
            }
        }
    }
    public void processOpenNewAccount(User activeUser, Scanner scanner) {
        if(bank.openNewAccount(activeUser)) {
            System.out.println("\nCongratulations! The account has been successfully opened.\n");
            openProfile(activeUser, scanner);
        } else {
            System.out.println("You have too mach open accounts.\n");
            openProfile(activeUser, scanner);
        }
    }

    public void processChoose3(User activeUser, Scanner scanner) {
        System.out.println("Active accounts: ");
        activeUser.showAccounts();
        System.out.print("\n1. Withdrawal  ");
        System.out.print("2. Deposit  ");
        System.out.print("3. Transfer  ");
        System.out.println("4. Transactions history  ");
        System.out.println("5. Open new account");

        switch (scanner.nextLine()) {
            case "1":
                processWithdrawal(activeUser, scanner);
                break;
            case "2":
                processDeposit(activeUser, scanner);
                break;
            case "3":
                processTransfer(activeUser, scanner);
                break;
            case "4":
                processTransactionsHistory(activeUser, scanner);
                break;
            case "5":
                processOpenNewAccount(activeUser, scanner);
                break;
            default:
                System.out.println("Please, provide correct number.\n");
                processChoose3(activeUser, scanner);
        }
    }

    public void processWithdrawal(User activeUser, Scanner scanner) {
        BankAccount account = activeUser.getUserAccounts().size() > 1 ?
        processChooseAccount(activeUser, scanner) : activeUser.getUserAccounts().getFirst();

        String amount = enterAmount(account, scanner);

        if(amount.isEmpty()) {
            processChoose3(activeUser, scanner);
        }

        if(account.withdrawal(Double.parseDouble(amount))) {
            System.out.println("Withdrawal was successfully executed.\n");
        } else {
            System.out.println("Withdrawalnot executed.\n");
        }

        processChoose3(activeUser, scanner);
    }

    public void processDeposit(User activeUser, Scanner scanner) {
        BankAccount account = activeUser.getUserAccounts().size() > 1 ?
        processChooseAccount(activeUser, scanner) : activeUser.getUserAccounts().getFirst();

        String amount = enterAmount(account, scanner);

        if(amount.isEmpty()) {
            processChoose3(activeUser, scanner);
        }

        if(account.deposit(Double.parseDouble(amount))) {
            System.out.println(amount + " was successfully added to your account.\n");
        } else {
            System.out.println("Deposit not executed.\n");
        }

        processChoose3(activeUser, scanner);
    }

    public void processTransfer(User activeUser, Scanner scanner) {
        BankAccount fromAccount = activeUser.getUserAccounts().size() > 1 ?
        processChooseAccount(activeUser, scanner) : activeUser.getUserAccounts().getFirst();

        BankAccount toAccount = bank.getAccountForTransfer(enterAccountNumber(fromAccount, scanner));
        double amount = Double.parseDouble(enterAmount(toAccount, scanner));

        if(toAccount == null || amount == 0D) {
            System.out.println("Transfer not executed.\n");
            openProfile(activeUser, scanner);
        }

        if(fromAccount.transfer(toAccount, amount)) {
            System.out.println("Transfer was successfully executed.\n");
        } else {
            System.out.println("Transfer not executed.\n");
        }

        processChoose3(activeUser, scanner);
    }

    public void processTransactionsHistory(User activeUser, Scanner scanner) {
        BankAccount account = activeUser.getUserAccounts().size() > 1 ?
        processChooseAccount(activeUser, scanner) : activeUser.getUserAccounts().getFirst();

        List<Transaction> history = account.showTransactionsHistory();
        if(history.isEmpty()) {
            System.out.println("At the moment, there haven't been any transactions made.\n");
            openProfile(activeUser, scanner);
        } else {
            for(Transaction transaction : history) {
                System.out.println(transaction);
            }
            openProfile(activeUser, scanner);
        }
    }

    public BankAccount processChooseAccount(User activeUser, Scanner scanner) {
        int index = Integer.parseInt(enterNumForChooseAccount(scanner));

        if(activeUser.getUserAccounts().size() < index || index == 0) {
            System.out.println("Please, check number.\n");
            processChoose3(activeUser, scanner);
        }

        return activeUser.getUserAccounts().get(index - 1);
    }

    public String enterName(Scanner scanner) {
        String providedName = "";
        for(int i = 0; i < 3; i++) {
            System.out.println("\nPlease, enter your name: ");
            providedName = scanner.nextLine();
            if(!checkName(providedName)) {
                System.out.println("Incorrect name!\n");
            } else {
                break;
            }
        }
        return providedName;
    }
    public String enterPassword(Scanner scanner) {
        String providedPassword = "";
        for(int i = 0; i < 3; i++) {
            System.out.println("Please, enter your password: ");
            providedPassword  = scanner.nextLine();
            if(!checkPassword(providedPassword )) {
                System.out.println("The password must contain at least 8 characters, including at least " +
                "one digit, one lowercase letter, one uppercase letter, and one special character from the set @#$%^&+=.\n");
            } else {
                break;
            }
        }
        return providedPassword;
    }
    public String enterAddress(Scanner scanner) {
        String providedAddress= "";
        for(int i = 0; i < 3; i++) {
            System.out.println("Please, enter your address:\nExample: Black Street 2-54");
            providedAddress = scanner.nextLine();
            if(!checkAddress(providedAddress)) {
                System.out.println("Incorrect address!\n");
            } else {
                break;
            }
        }
        return providedAddress;
    }
    public String enterUserID(Scanner scanner) {
        String providedUserID= "";
        for(int i = 0; i < 3; i++) {
            System.out.println("\nPlease, enter your userID: ");
            providedUserID = scanner.nextLine();
            if(!checkUserID(providedUserID)) {
                System.out.println("UserID most contain 6 numbers!\n");
            } else {
                break;
            }
        }
        return providedUserID;
    }
    public String enterAmount(BankAccount account, Scanner scanner) {
        String providedAmount= "";
        for(int i = 0; i < 3; i++) {
            System.out.println("\nPlease enter amount.");
            providedAmount = scanner.nextLine();
            if(!account.correctSum(providedAmount)) {
                System.out.println("Please, provide correct amount for transfer.\n");
            } else {
                break;
            }
        }
        return providedAmount;
    }
    public String enterAccountNumber(BankAccount account, Scanner scanner) {
        String providedAccountNumber= "";
        for(int i = 0; i < 3; i++) {
            System.out.println("\nPlease enter the account number to which you wish to transfer money.");
            providedAccountNumber = scanner.nextLine();
            if(!account.correctAccount(providedAccountNumber)) {
                System.out.println("Please, enter the correct account number.\n");
                continue;
            } else if(!bank.accountPresent(providedAccountNumber)) {
                System.out.println("Please, check account number.\n");
                continue;
            } else {
                break;
            }
        }
        return providedAccountNumber;
    }
    public String enterNumForChooseAccount(Scanner scanner) {
        System.out.println("\nEnter the account number you want to use.");
        String provided = scanner.nextLine();

        if(!checkNum(provided)) {
            System.out.println("Please, enter correct number.");
            return "0";
        }
        return provided;
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
        String numRegex = "^[123]$";
        Pattern numPattern = Pattern.compile(numRegex);
        Matcher numMatcher = numPattern.matcher(provided);

        return numMatcher.matches();
    }

    public static void main(String[] args) {
        DatabaseSetup.setupDatabase();

        CLI_APP bank777 = new CLI_APP();
        bank777.openBank();
    }
}
