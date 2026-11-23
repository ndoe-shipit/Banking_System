package dao;

import model.BankTeller;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BankTellerDAO {
    public void addBankTeller(BankTeller bankTeller)throws SQLException {
        String sql = "INSERT INTO bankteller (tellerID,password) VALUES (?,?)";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bankTeller.getTellerID());
            stmt.setString(2, bankTeller.getPassword());
            stmt.executeUpdate();
        }
    }
   public BankTeller selectBankTellerByID(String tellerID) throws SQLException {
        String sql = "SELECT * FROM bankteller WHERE tellerID = ?";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tellerID);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return new BankTeller(
                        rs.getString("tellerID"),
                        rs.getString("password")
                );
            }
        }
        return null;
   }
   public List<BankTeller> selectAllBankTeller() throws SQLException {
        List<BankTeller> bankTellers = new ArrayList<>();
        String sql = "SELECT * FROM bankteller";
        try(Connection conn = DatabaseConnection.getConnection()){
           PreparedStatement stmt = conn.prepareStatement(sql);
           ResultSet rs = stmt.executeQuery();
            {
                while(rs.next()){
                    BankTeller bankTeller = new BankTeller(rs.getString("tellerID"),rs.getString("password"));
                    bankTellers.add(bankTeller);
                }
            }
        }
        return bankTellers;
   }
   public void updateBankTeller(BankTeller bankTeller) throws SQLException {
        String sql = "UPDATE bankteller SET password = ? WHERE tellerID = ?";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bankTeller.getPassword());
            stmt.setString(2, bankTeller.getTellerID());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
               System.out.println("No bankteller found with that id");
            } else{
                System.out.println("Bankteller updated successfully");
            }
        }
   }
   public void deleteBankTeller(String tellerID) throws SQLException {
        String sql = "DELETE FROM bankteller WHERE tellerID = ?";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, tellerID);
            stmt.executeUpdate();
        }
   }
}
