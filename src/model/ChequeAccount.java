package model;

public class ChequeAccount extends Account implements Withdrawable{
    private String employerName;
    private String employerAddress;

    public ChequeAccount(String accountNumber, double balance, String branch, Customer customer, String employerAddress, String employerName) {
        super(accountNumber, balance, branch, customer);
        this.employerAddress = employerAddress;
        this.employerName = employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }

    public void setEmployerAddress(String employerAddress) {
        this.employerAddress = employerAddress;
    }

    public String getEmployerName() {
        return employerName;
    }

    public void setEmployerName(String employerName) {
        this.employerName = employerName;
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
