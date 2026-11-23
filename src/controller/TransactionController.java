package controller;

import dao.TransactionDao;
import model.Transaction;

import java.sql.SQLException;
import java.util.List;

public class TransactionController {
    private TransactionDao transactionDao = new  TransactionDao();
    public void addTransaction(Transaction transaction, String accountNumber) throws SQLException {
        transactionDao.addTransaction(transaction, accountNumber);
    }
public List<Transaction> getTransactionsByAccount(String accountNumber) throws SQLException {
return transactionDao.getTransactionByAccount(accountNumber);
}
}
