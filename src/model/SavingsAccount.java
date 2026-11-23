package model;

public class SavingsAccount extends Account implements InterestCalculator{
    private static final double INTEREST_RATE = 0.0005;

    public SavingsAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
    }
    public double getInterestRate() {
        return INTEREST_RATE;
    }

    // SavingsAccount and InvestmentAccount
    @Override
    public double calculateInterest() {
        return getBalance() * INTEREST_RATE;
    }
}
