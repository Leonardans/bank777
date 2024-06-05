package first_project;

public enum TransactionType {
    DEPOSIT(0.1),
    WITHDRAWAL(0.2),
    TRANSFER(0.3);

    private final double bankTax;

    TransactionType(double bankTax) {
        this.bankTax = bankTax;
    }

    public double getBankTax() {
        return bankTax;
    }
}
