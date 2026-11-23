package controller;

import dao.BankTellerDAO;
import model.BankTeller;

import java.sql.SQLException;
import java.util.List;

public class BankTellerController {
private BankTellerDAO bankTellerDAO  = new BankTellerDAO();
    public BankTeller login(String tellerID, String password) throws SQLException {
        if (tellerID == null || tellerID.isBlank())
            throw new IllegalArgumentException("Teller ID is required");
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Password is required");

        BankTeller teller = bankTellerDAO.selectBankTellerByID(tellerID.trim());
        if (teller == null || teller.getPassword() == null || !teller.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid Teller ID or Password");
        }
        return teller;
    }
public void addBankTeller(BankTeller bankTeller)throws SQLException {
bankTellerDAO.addBankTeller(bankTeller);
}
public BankTeller getBankTeller(String tellerID) throws SQLException {
    return bankTellerDAO.selectBankTellerByID(tellerID);
}
public List<BankTeller> selectAllBankTeller() throws SQLException {
    return bankTellerDAO.selectAllBankTeller();
}
public void deleteBankTeller(String tellerID) throws SQLException {
    bankTellerDAO.deleteBankTeller(tellerID);
}
public void updateBankTeller(BankTeller bankTeller) throws SQLException {
    bankTellerDAO.updateBankTeller(bankTeller);
}


}
