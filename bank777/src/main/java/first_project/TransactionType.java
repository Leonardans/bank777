package first_project;

import java.math.BigDecimal;

public enum TransactionType {
    DEPOSIT(new BigDecimal("0.1")),
    WITHDRAWAL(new BigDecimal("0.2")),
    TRANSFER(new BigDecimal("0.3"));

    private final BigDecimal bankTax;

    TransactionType(BigDecimal bankTax) {
        this.bankTax = bankTax;
    }

    public BigDecimal getBankTax() {
        return bankTax;
    }
}
