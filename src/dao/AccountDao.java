package dao;
import model.*;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {
private static final String Selectbase =  "SELECT a.accountNumber, a.balance, a.branch, a.customerID, " +
        "c.address, i.firstName, i.surname, o.registeredName, o.registrationNumber, " +
        "ca.employerName, ca.employerAddress, " +
        "sa.accountNumber AS savingsKey, " +
        "ia.accountNumber AS investmentKey " +
        "FROM account a " +
        "INNER JOIN customer c ON a.customerID = c.customerID " +
        "LEFT JOIN individualcustomer i ON i.customerID = c.customerID " +
        "LEFT JOIN organisationcustomer o ON o.customerID = c.customerID " +
        "LEFT JOIN ChequeAccount ca ON a.accountNumber = ca.accountNumber " +
        "LEFT JOIN SavingsAccount sa ON a.accountNumber = sa.accountNumber " +
        "LEFT JOIN InvestmentAccount ia ON a.accountNumber = ia.accountNumber";

    public void addAccount(Account account) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {

                String baseSql = "INSERT INTO account (accountNumber, balance, branch, customerID) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(baseSql)) {
                    stmt.setString(1, account.getAccountNumber());
                    stmt.setDouble(2, account.getBalance());
                    stmt.setString(3, account.getBranch());
                    stmt.setString(4, account.getCustomer().getCustomerID()); // Traverse object to find the ID relation
                    stmt.executeUpdate();
                }


                if (account instanceof ChequeAccount cheque) {
                    String sql = "INSERT INTO ChequeAccount (accountNumber, employerName, employerAddress) VALUES (?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, cheque.getAccountNumber());
                        stmt.setString(2, cheque.getEmployerName());
                        stmt.setString(3, cheque.getEmployerAddress());
                        stmt.executeUpdate();
                    }
                } else if (account instanceof SavingsAccount) {
                    String sql = "INSERT INTO SavingsAccount (accountNumber) VALUES (?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, account.getAccountNumber());
                        stmt.executeUpdate();
                    }
                } else if (account instanceof InvestmentAccount) {
                    String sql = "INSERT INTO InvestmentAccount (accountNumber) VALUES (?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, account.getAccountNumber());
                        stmt.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }


    public Account selectAccountByNumber(String accountNumber) throws SQLException {
        String sql = Selectbase + " WHERE a.accountNumber = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }


    public List<Account> getAllAccounts() throws SQLException {
        List<Account> accounts = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(Selectbase);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Account account = mapRow(rs);
                accounts.add(account);
            }
        }
        return accounts;
    }
    public List<Account> getAccountsByCustomer(String customerID) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = Selectbase + " WHERE a.customerID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customerID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    accounts.add(mapRow(rs));
                }
            }
        }
        return accounts;
    }

    private Account mapRow(ResultSet rs) throws SQLException {
        String customerID = rs.getString("customerID");
        String address = rs.getString("address");
        Customer customer;

        if (rs.getString("firstName") != null) {
            customer = new IndividualCustomer(customerID, address, rs.getString("firstName"), rs.getString("surname"));
        } else {
            customer = new OrganisationCustomer(customerID, address, rs.getString("registeredName"), rs.getString("registrationNumber"));
        }


        String accNum = rs.getString("accountNumber");
        double balance = rs.getDouble("balance");
        String branch = rs.getString("branch");


        if (rs.getString("employerName") != null) {
            return new ChequeAccount(accNum, balance, branch, customer, rs.getString("employerAddress"), rs.getString("employerName"));
        } else if (rs.getString("savingsKey") != null) {
            return new SavingsAccount(accNum, balance, branch, customer);
        } else if (rs.getString("investmentKey") != null) {
            return new InvestmentAccount(accNum, balance, branch, customer);
        } else {
            throw new SQLException("Account mapping error: Subtype record not found for account number " + accNum);
        }
    }


    public void updateAccount(Account account) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {

                String baseUpdate = "UPDATE account SET balance = ?, branch = ? WHERE accountNumber = ?";
                try (PreparedStatement stmt = conn.prepareStatement(baseUpdate)) {
                    stmt.setDouble(1, account.getBalance());
                    stmt.setString(2, account.getBranch());
                    stmt.setString(3, account.getAccountNumber());
                    stmt.executeUpdate();
                }


                if (account instanceof ChequeAccount cheque) {
                    String chequeUpdate = "UPDATE ChequeAccount SET employerName = ?, employerAddress = ? WHERE accountNumber = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(chequeUpdate)) {
                        stmt.setString(1, cheque.getEmployerName());
                        stmt.setString(2, cheque.getEmployerAddress());
                        stmt.setString(3, cheque.getAccountNumber());
                        stmt.executeUpdate();
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }


    public void deleteAccount(String accountNumber) throws SQLException {
        String sql = "DELETE FROM account WHERE accountNumber = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.executeUpdate();
        }
    }
}
