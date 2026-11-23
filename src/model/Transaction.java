package model;


import java.time.LocalDateTime;

public class Transaction {
    private String transactionID;
    private double amount;
    private String type;
    private LocalDateTime timestamp;

    public Transaction(double amount, LocalDateTime timestamp, String transactionID, String type) {
        this.amount = amount;
        this.timestamp = timestamp;
        this.transactionID = transactionID;
        this.type = type;
    }


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
