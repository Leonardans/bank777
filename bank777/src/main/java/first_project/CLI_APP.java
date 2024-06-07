package first_project;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import first_project.utils.ValidationUtils;

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
        System.out.print("1. Login\t");
        System.out.print("2. Create new profile.\t");
        System.out.println("\tQ. Exit from bank777\t");
        switch (scanner.nextLine()) {
            case "1":
                processLogin(scanner);
                break;
            case "2":
                processCreateNewProfile(scanner);
                break;
            case "Q":
                processCloseApp();
                break;
            default:
                System.out.println("Please, provide only, 1 - if you want to login, " +
                "2 - if you want create new profile or Q - if you want exit\n");
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
        if(name.isEmpty() || password.isEmpty() || address.isEmpty()) {
            System.out.println("Profile not created.\n");
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
        System.out.print("\n1. Open new account\t");
        System.out.println("\tQ. Exit from bank777\t");

        switch (scanner.nextLine()) {
            case "1" -> processOpenNewAccount(activeUser, scanner);
            case "Q" -> processCloseApp();
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
            processChoose3(activeUser, scanner);
        }
    }

    public void processChoose3(User activeUser, Scanner scanner) {
        System.out.println("Active accounts: ");
        activeUser.showAccounts();
        System.out.print("\n1. Withdrawal\t");
        System.out.print("2. Deposit\t ");
        System.out.print("3. Transfer\t");
        System.out.print("4. Transactions history\t\t\t");
        System.out.println("Q. Exit from bank777\t");
        System.out.println("5. Open new account\t");

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
            case "Q":
                processCloseApp();
                break;
            default:
                System.out.println("Please, provide correct number.\n");
                processChoose3(activeUser, scanner);
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

    public void processDeposit(User activeUser, Scanner scanner) {
        BankAccount account = activeUser.getUserAccounts().size() > 1 ?
            processChooseAccount(activeUser, scanner) : activeUser.getUserAccounts().getFirst();

        BigDecimal amount = enterAmount(scanner);
        if(amount.compareTo(BigDecimal.ZERO) == 0) {
            processChoose3(activeUser, scanner);
        }

        boolean confirm = processInformingAboutTax(TransactionType.DEPOSIT, amount, scanner);
        if(confirm) {
            if(account.deposit(amount)) {
                activeUser.setUserAccountsFromDB(activeUser.getUserID());
                System.out.println("\n\n" + amount.subtract(TransactionType.DEPOSIT.getBankTax()) +
                    "$ was successfully deposited into your account." + " [ Tax is " + TransactionType.TRANSFER.getBankTax() + "$ ]");
            } else {
                System.out.println("\nDeposit not executed.");
            }
        }

        openProfile(activeUser, scanner);
    }
    public void processWithdrawal(User activeUser, Scanner scanner) {
        BankAccount account = activeUser.getUserAccounts().size() > 1 ?
        processChooseAccount(activeUser, scanner) : activeUser.getUserAccounts().getFirst();

        BigDecimal amount = enterAmount(scanner);
        if(amount.compareTo(BigDecimal.ZERO) == 0) processChoose3(activeUser, scanner);

        boolean confirm = processInformingAboutTax(TransactionType.WITHDRAWAL, amount, scanner);
        if(confirm) {
            if(account.withdrawal(amount)) {
                activeUser.setUserAccountsFromDB(activeUser.getUserID());
                System.out.println("\n\n" + amount.add(TransactionType.WITHDRAWAL.getBankTax()) +
                    "$ have been withdrawn from your account." + " [ Tax is " + TransactionType.WITHDRAWAL.getBankTax() + "$ ]");
            } else {
                System.out.println("\nWithdrawal not executed.");
            }
        }
        openProfile(activeUser, scanner);
    }
    public void processTransfer(User activeUser, Scanner scanner) {
        BankAccount fromAccount = activeUser.getUserAccounts().size() > 1 ?
            processChooseAccount(activeUser, scanner) : activeUser.getUserAccounts().getFirst();

        String toAccountNumber = enterAccountNumber(scanner);
        BankAccount toAccount = null;
        if(!toAccountNumber.isEmpty())
            toAccount = bank.getAccountForTransfer(toAccountNumber);
        else {
            System.out.println("\nTransfer not executed.");
            processChoose3(activeUser, scanner);
        }

        BigDecimal amount = enterAmount(scanner);
        if(toAccount == null && amount.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("\nTransfer not executed.\n");
            openProfile(activeUser, scanner);
        }

        boolean confirm = processInformingAboutTax(TransactionType.TRANSFER, amount, scanner);
        if(confirm) {
            if(fromAccount.transfer(Objects.requireNonNull(toAccount), amount)) {
                activeUser.setUserAccountsFromDB(activeUser.getUserID());
                System.out.println("\n\n" + amount + "$ was successfully transferred to account " + toAccount.getAccountNumber() +
                    " [ Tax is " + TransactionType.TRANSFER.getBankTax() + "$ ]");
            } else {
                System.out.println("\nTransfer not executed.");
            }
        }
        openProfile(activeUser, scanner);
    }
    public void processTransactionsHistory(User activeUser, Scanner scanner) {
        BankAccount account = activeUser.getUserAccounts().size() > 1 ?
            processChooseAccount(activeUser, scanner) : activeUser.getUserAccounts().getFirst();

        List<Transaction> history = account.showTransactionsHistory();
        if(history.isEmpty()) {
            System.out.println("\n\nAt the moment, there haven't been any transactions made.");
            openProfile(activeUser, scanner);
        } else {
            System.out.println("\n");
            for(Transaction transaction : history) {
                System.out.println(transaction);
            }
            openProfile(activeUser, scanner);
        }
    }

    public boolean processInformingAboutTax(Enum<TransactionType> operation, BigDecimal amount, Scanner scanner) {
        switch(operation.name()) {
            case "DEPOSIT":
                BigDecimal tax1 =TransactionType.DEPOSIT.getBankTax();
                System.out.println("Tax for deposit is " + tax1 + "$ you receive " + (amount.subtract(tax1)) + "$");
                return processConfirm(scanner);
            case "WITHDRAWAL":
                BigDecimal tax2 = TransactionType.WITHDRAWAL.getBankTax();
                System.out.println("Tax for withdrawal is " + tax2 + "$ from your account will be deducted " + (amount.add(tax2)) + "$");
                return processConfirm(scanner);
            case "TRANSFER":
                BigDecimal tax3 =TransactionType.TRANSFER.getBankTax();
                System.out.println("Tax for transfer is " + tax3 + "$ from your account will be deducted " + (amount.add(tax3)) + "$");
                return processConfirm(scanner);
            default:
                return false;
        }
    }
    public boolean processConfirm(Scanner scanner) {
        System.out.println("Press Y if you agree or any other key if you disagree.");
        return scanner.nextLine().equalsIgnoreCase("Y");
    }

    public void processCloseApp() {
        System.exit(0);
    }

    public String enterName(Scanner scanner) {
        String name = "";
        for(int i = 0; i < 3; i++) {
            System.out.println("\nPlease, enter your name: ");
            String providedName = scanner.nextLine();
            if(!ValidationUtils.checkName(providedName)) {
                System.out.println("Incorrect name!\n");
            } else {
                name = providedName;
                break;
            }
        }
        return name;
    }
    public String enterPassword(Scanner scanner) {
        String password = "";
        for(int i = 0; i < 3; i++) {
            System.out.println("Please, enter your password: ");
            String providedPassword  = scanner.nextLine();
            if(!ValidationUtils.checkPassword(providedPassword )) {
                System.out.println("The password must contain at least 8 characters, including at least " +
                "one digit, one lowercase letter, one uppercase letter, and one special character from the set @#$%^&+=.\n");
            } else {
                password = providedPassword;
                break;
            }
        }
        return password;
    }
    public String enterAddress(Scanner scanner) {
        String address= "";
        for(int i = 0; i < 3; i++) {
            System.out.println("Please, enter your address:\nExample: Black Street 2-54");
            String providedAddress = scanner.nextLine();
            if(!ValidationUtils.checkAddress(providedAddress)) {
                System.out.println("Incorrect address!\n");
            } else {
                address = providedAddress;
                break;
            }
        }
        return address;
    }
    public String enterUserID(Scanner scanner) {
        String userID= "";
        for(int i = 0; i < 3; i++) {
            System.out.println("\nPlease, enter your userID: ");
            String providedUserID = scanner.nextLine();
            if(!ValidationUtils.checkUserID(providedUserID)) {
                System.out.println("UserID most contain 6 numbers!\n");
            } else {
                userID = providedUserID;
                break;
            }
        }
        return userID;
    }
    public BigDecimal enterAmount(Scanner scanner) {
        String amount= "";
        for(int i = 0; i < 3; i++) {
            System.out.println("\nPlease enter amount.");
            String providedAmount = scanner.nextLine();
            if(!ValidationUtils.correctSum(providedAmount)) {
                System.out.println("Please, provide correct amount for transfer.\n");
            } else {
                amount = providedAmount;
                break;
            }
        }
        return BigDecimal.valueOf(Double.parseDouble(amount));
    }
    public String enterAccountNumber(Scanner scanner) {
        String accountNumber= "";
        for(int i = 0; i < 3; i++) {
            System.out.println("\nPlease enter the account number to which you wish to transfer money.");
            String providedAccountNumber = scanner.nextLine();
            boolean check = ValidationUtils.correctAccount(providedAccountNumber) && bank.accountPresent(providedAccountNumber);

            if(check) {
                accountNumber = providedAccountNumber;
                break;
            } else System.out.println("Please, enter the correct account number.\n");
        }
        return accountNumber;
    }
    public String enterNumForChooseAccount(Scanner scanner) {
        System.out.println("\nEnter the account number you want to use.");
        String provided = scanner.nextLine();

        if(!ValidationUtils.checkNum(provided)) {
            System.out.println("Please, enter correct number.");
            return "0";
        }
        return provided;
    }

    public static void main(String[] args) {
        CLI_APP bank777 = new CLI_APP();
        bank777.openBank();
    }
}
