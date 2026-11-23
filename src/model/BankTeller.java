package model;

public class BankTeller {
    private String tellerID;
    private String password;

    public BankTeller(String tellerID, String password) {
        this.password = password;
        this.tellerID = tellerID;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTellerID() {
        return tellerID;
    }

    public void setTellerID(String tellerID) {
        this.tellerID = tellerID;
    }
}
