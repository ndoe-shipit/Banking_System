package model;

public class InvestmentAccount extends Account implements InterestCalculator, Withdrawable{

    private static final double INTEREST_RATE = 0.05;

    public InvestmentAccount(String accountNumber, double balance, String branch, Customer customer) {
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

    @Override
    public boolean withdraw(double amount) {
        if (amount > getBalance()) {
            return false; // not enough funds reject it
        }
        setBalance(getBalance() - amount);
        return true; // withdrawal succeeded
    }
}
