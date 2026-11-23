package controller;

import dao.AccountDao;
import dao.CustomerDao;
import dao.TransactionDao;
import model.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * All account rules live here so that the console Main and the future GUI
 * can share them. Errors are reported with exceptions carrying a readable message.
 */
public class AccountController {
    private final AccountDao accountDao = new AccountDao();
    private final CustomerDao customerDao = new CustomerDao();
    private final TransactionDao transactionDao = new TransactionDao();

    // ---------- opening accounts ----------
    public void addSavingsAccount(SavingsAccount savings) throws SQLException {
        validateNew(savings);
        accountDao.addAccount(savings);
    }

    public void addInvestmentAccount(InvestmentAccount investment) throws SQLException {
        validateNew(investment);
        if (investment.getBalance() < 500.00)
            throw new IllegalArgumentException("An investment account needs an initial deposit of at least BWP500.00");
        accountDao.addAccount(investment);
    }

    public void addChequeAccount(ChequeAccount cheque) throws SQLException {
        validateNew(cheque);
        if (isBlank(cheque.getEmployerName()) || isBlank(cheque.getEmployerAddress()))
            throw new IllegalArgumentException("A cheque account needs the employer name and address");
        accountDao.addAccount(cheque);
    }

    // ---------- money movement ----------
    public void deposit(String accountNumber, double amount) throws SQLException {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
        Account account = findOrThrow(accountNumber);
        account.deposit(amount);
        accountDao.updateAccount(account);
        record(accountNumber, amount, "DEPOSIT");
    }

    public void withdraw(String accountNumber, double amount) throws SQLException {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
        Account account = findOrThrow(accountNumber);
        if (!(account instanceof Withdrawable w))
            throw new UnsupportedOperationException("This account type does not allow withdrawals");
        if (!w.withdraw(amount))
            throw new IllegalStateException("Insufficient funds");
        accountDao.updateAccount(account);
        record(accountNumber, amount, "WITHDRAWAL");
    }

    /** Pays monthly interest to every interest-bearing account. Returns how many accounts were paid. */
    public int payInterest() throws SQLException {
        int paid = 0;
        for (Account account : accountDao.getAllAccounts()) {
            if (account instanceof InterestCalculator calculator) {   // polymorphism
                double before = account.getBalance();
                calculator.calculateInterest();                        // adds interest to the balance
                double interest = Math.round((account.getBalance() - before) * 100) / 100.0;
                if (interest <= 0) continue;
                account.setBalance(before + interest);                 // keep 2 decimal places
                accountDao.updateAccount(account);
                record(account.getAccountNumber(), interest, "INTEREST");
                paid++;
            }
        }
        return paid;
    }

    // ---------- queries / maintenance ----------
    public Account getAccountByNumber(String accountNumber) throws SQLException {
        return accountDao.selectAccountByNumber(accountNumber);
    }

    public List<Account> getAllAccounts() throws SQLException {
        return accountDao.getAllAccounts();
    }

    public List<Account> getAccountsByCustomer(String customerID) throws SQLException {
        return accountDao.getAccountsByCustomer(customerID);
    }

    public void updateAccount(Account account) throws SQLException {
        findOrThrow(account.getAccountNumber());
        accountDao.updateAccount(account);
    }

    public void deleteAccount(String accountNumber) throws SQLException {
        findOrThrow(accountNumber);
        accountDao.deleteAccount(accountNumber);
    }

    /** Readable account type for displays (console or GUI). */
    public String getAccountType(Account account) {
        if (account instanceof SavingsAccount) return "Savings";
        if (account instanceof InvestmentAccount) return "Investment";
        if (account instanceof ChequeAccount) return "Cheque";
        return "Account";
    }

    // ---------- helpers ----------
    private void validateNew(Account a) throws SQLException {
        if (isBlank(a.getAccountNumber()))
            throw new IllegalArgumentException("Account number is required");
        if (isBlank(a.getBranch()))
            throw new IllegalArgumentException("Branch is required");
        if (a.getBalance() < 0)
            throw new IllegalArgumentException("Opening deposit cannot be negative");
        if (a.getCustomer() == null || customerDao.selectCustomerByID(a.getCustomer().getCustomerID()) == null)
            throw new IllegalArgumentException("An account must belong to an existing customer");
        if (accountDao.selectAccountByNumber(a.getAccountNumber()) != null)
            throw new IllegalArgumentException("Account number already exists: " + a.getAccountNumber());
    }

    private Account findOrThrow(String accountNumber) throws SQLException {
        Account account = accountDao.selectAccountByNumber(accountNumber);
        if (account == null)
            throw new IllegalArgumentException("Account not found: " + accountNumber);
        return account;
    }

    private void record(String accountNumber, double amount, String type) throws SQLException {
        Transaction t = new Transaction(amount, LocalDateTime.now(), UUID.randomUUID().toString(), type);
        transactionDao.addTransaction(t, accountNumber);
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}