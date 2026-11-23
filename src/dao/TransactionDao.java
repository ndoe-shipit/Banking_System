package dao;

import model.Transaction;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {
    public void  addTransaction(Transaction transaction, String accountNumber) throws SQLException {
        String sql = "INSERT INTO transactions(transactionID, accountNumber, type, amount, timestamp) VALUES (?, ?, ?, ?, ?)";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, transaction.getTransactionID());
            stmt.setString(2, accountNumber);
            stmt.setString(3, transaction.getType());
            stmt.setDouble(4, transaction.getAmount());
            stmt.setTimestamp(5, Timestamp.valueOf(transaction.getTimestamp()));
            stmt.executeUpdate();
        }
    }
    public List<Transaction> getTransactionByAccount(String accountNumber) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE accountNumber = ? ORDER BY timestamp DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { // <-- LOOP STARTS HERE
                    LocalDateTime ts = rs.getTimestamp("timestamp").toLocalDateTime();
                    transactions.add(new Transaction(rs.getDouble("amount"), ts,
                            rs.getString("transactionID"), rs.getString("type")));
                } // <-- LOOP ENDS HERE
            }
        }
        return transactions;
    }

}
